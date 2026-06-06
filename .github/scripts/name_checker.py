import argparse
import json
import re
import sys
import xml.etree.ElementTree as ET
from abc import ABC, abstractmethod
from dataclasses import asdict, dataclass, field
from enum import Enum
from pathlib import Path
from typing import Any, Callable, Dict, List, Optional, TextIO, Type
from unidecode import unidecode


class Status(Enum):
    PASS = "PASS"
    FAIL = "FAIL"


@dataclass(frozen=True)
class Finding:
    rule_id: str
    category: str
    status: Status
    target: str
    message: str


@dataclass(frozen=True)
class RuleSummary:
    rule_id: str
    category: str
    status: Status
    message: str


@dataclass
class CheckContext:
    xml_items: Dict[str, str]
    disk_svgs: set[str]
    changed_set: Optional[set[str]]

    @property
    def xml_drawables(self) -> set[str]:
        return set(self.xml_items.keys())

    @property
    def xml_drawables_to_check(self) -> set[str]:
        if self.changed_set is None:
            return self.xml_drawables
        return self.xml_drawables & self.changed_set

    @property
    def disk_svgs_to_check(self) -> set[str]:
        if self.changed_set is None:
            return self.disk_svgs
        return self.disk_svgs & self.changed_set


@dataclass
class RunReport:
    findings: List[Finding] = field(default_factory=list)
    summaries: List[RuleSummary] = field(default_factory=list)
    fatal_error: Optional[str] = None

    @property
    def has_failures(self) -> bool:
        return any(f.status == Status.FAIL for f in self.findings)


RuleFunc = Callable[[CheckContext, str, str], List[Finding]]
RULES_REGISTRY: List[tuple[RuleFunc, str, str]] = []


def register_rule(rule_id: str, category: str = "Naming") -> Callable[[RuleFunc], RuleFunc]:
    def decorator(func: RuleFunc) -> RuleFunc:
        RULES_REGISTRY.append((func, rule_id, category))
        return func
    return decorator


def slugify_app_name(app_name: str) -> str:
    """Convert an app name from appfilter.xml into a drawable filename slug."""
    try:
        primary_name = app_name.split("~~", 1)[1].strip()
    except IndexError:
        primary_name = app_name

    primary_name = primary_name.replace("&amp;", "&")

    slug = unidecode(primary_name)
    slug = slug.lower()
    slug = slug.replace("'", "")
    slug = slug.replace("&", "and")
    slug = slug.replace("+", "_plus_")
    slug = re.sub(r"[^a-z0-9]+", "_", slug)
    slug = slug.strip("_")

    if slug and slug[0].isdigit():
        slug = f"_{slug}"

    return slug


@register_rule("NR01", category="Naming")
def rule_orphan_file(ctx: CheckContext, rule_id: str, category: str) -> List[Finding]:
    orphaned_svgs = sorted(ctx.disk_svgs_to_check - ctx.xml_drawables)
    return [
        Finding(
            rule_id=rule_id,
            category=category,
            status=Status.FAIL,
            target=f"{drawable}.svg",
            message="Not declared in `appfilter.xml`",
        )
        for drawable in orphaned_svgs
    ]


@register_rule("NR02", category="Naming")
def rule_missing_file(ctx: CheckContext, rule_id: str, category: str) -> List[Finding]:
    missing_svgs = sorted(ctx.xml_drawables_to_check - ctx.disk_svgs)
    return [
        Finding(
            rule_id=rule_id,
            category=category,
            status=Status.FAIL,
            target=f"{drawable}.svg",
            message="Declared in `appfilter.xml` but file not found",
        )
        for drawable in missing_svgs
    ]


@register_rule("NR03", category="Naming")
def rule_naming_mismatch(ctx: CheckContext, rule_id: str, category: str) -> List[Finding]:
    findings: List[Finding] = []
    for drawable in sorted(ctx.xml_drawables_to_check):
        if drawable not in ctx.disk_svgs:
            continue
        app_name = ctx.xml_items.get(drawable, "")
        expected = slugify_app_name(app_name)
        if drawable != expected:
            findings.append(
                Finding(
                    rule_id=rule_id,
                    category=category,
                    status=Status.FAIL,
                    target=f"{drawable}.svg",
                    message=f"Rename: `{expected}` (based on app name `{app_name}`)",
                )
            )
    return findings


class OutputHandler(ABC):
    def __init__(self, dest: TextIO, verbose: bool):
        self.dest = dest
        self.verbose = verbose

    @abstractmethod
    def emit(self, report: RunReport) -> None:
        raise NotImplementedError


class ConsoleOutput(OutputHandler):
    def emit(self, report: RunReport) -> None:
        if report.fatal_error:
            self.dest.write(f"FATAL_ERROR: {report.fatal_error}\n")
            return

        if self.verbose:
            for summary in sorted(report.summaries, key=lambda s: s.rule_id):
                self.dest.write(f"[{summary.status.value}] [{summary.category}: {summary.rule_id}] {summary.message}\n")

        actionable = sorted(report.findings, key=lambda f: (f.target, f.rule_id, f.message))
        for finding in actionable:
            self.dest.write(f"{finding.target}: {finding.rule_id}: {finding.message}\n")


class JsonOutput(OutputHandler):
    def emit(self, report: RunReport) -> None:
        data: Dict[str, Any] = {
            "fatal_error": report.fatal_error,
            "findings": [asdict(f) for f in sorted(report.findings, key=lambda i: (i.target, i.rule_id, i.message))],
            "summary": [asdict(s) for s in sorted(report.summaries, key=lambda i: i.rule_id)] if self.verbose else [],
        }
        for entry in data["findings"]:
            entry["status"] = entry["status"].value
        for entry in data["summary"]:
            entry["status"] = entry["status"].value
        self.dest.write(json.dumps(data, indent=2))
        self.dest.write("\n")


class CompactOutput(OutputHandler):
    def emit(self, report: RunReport) -> None:
        if report.fatal_error:
            self.dest.write(f"FATAL_ERROR: {report.fatal_error}\n")
            return

        grouped: Dict[str, set[str]] = {}
        for finding in report.findings:
            grouped.setdefault(finding.target, set()).add(finding.rule_id)

        for target in sorted(grouped.keys()):
            ids = ", ".join(sorted(grouped[target]))
            self.dest.write(f"{target}: {ids}\n")


OUTPUT_FACTORIES: Dict[str, Type[OutputHandler]] = {
    "text": ConsoleOutput,
    "json": JsonOutput,
    "compact": CompactOutput,
}


def load_context(appfilter_path: Path, drawables_path: Path, changed_set: Optional[set[str]]) -> CheckContext:
    if not appfilter_path.exists() or not appfilter_path.is_file():
        raise ValueError(f"Invalid appfilter path: {appfilter_path}")
    if not drawables_path.exists() or not drawables_path.is_dir():
        raise ValueError(f"Invalid drawables directory: {drawables_path}")

    try:
        tree = ET.parse(appfilter_path)
    except ET.ParseError as exc:
        raise ValueError(f"XML_PARSE_ERROR ({exc})") from exc

    root = tree.getroot()
    xml_items: Dict[str, str] = {
        drawable: (item.get("name") or "")
        for item in root.findall(".//item")
        if (drawable := item.get("drawable"))
    }
    disk_svgs = {p.stem for p in drawables_path.glob("*.svg")}
    return CheckContext(xml_items=xml_items, disk_svgs=disk_svgs, changed_set=changed_set)


def run_checks(ctx: CheckContext) -> RunReport:
    report = RunReport()

    for rule_func, rule_id, category in RULES_REGISTRY:
        findings = rule_func(ctx, rule_id, category)
        report.findings.extend(findings)

        if findings:
            report.summaries.append(
                RuleSummary(
                    rule_id=rule_id,
                    category=category,
                    status=Status.FAIL,
                    message=f"{len(findings)} finding(s)",
                )
            )
        else:
            report.summaries.append(
                RuleSummary(
                    rule_id=rule_id,
                    category=category,
                    status=Status.PASS,
                    message="No findings.",
                )
            )

    return report


def parse_args(argv: Optional[List[str]] = None) -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Drawable naming consistency linter")
    parser.add_argument("--appfilter", required=True, help="Path to appfilter.xml")
    parser.add_argument("--drawables-dir", required=True, help="Path to the directory containing SVGs")
    parser.add_argument(
        "--changed-drawables",
        nargs="*",
        default=None,
        help="Only check these drawable names",
    )
    parser.add_argument("--format", choices=OUTPUT_FACTORIES.keys(), default="text", help="Output format")
    parser.add_argument("--verbose", action="store_true", help="Show rule summaries in text/json output")
    parser.add_argument("--output-file", help="Write output to file instead of stdout")
    return parser.parse_args(argv)


def main(argv: Optional[List[str]] = None) -> int:
    args = parse_args(argv)
    changed_set: Optional[set[str]] = None
    if args.changed_drawables is not None:
        changed_set = set(args.changed_drawables)

    try:
        ctx = load_context(Path(args.appfilter), Path(args.drawables_dir), changed_set)
    except ValueError as exc:
        report = RunReport(fatal_error=str(exc))
        handler = OUTPUT_FACTORIES[args.format](sys.stderr, verbose=args.verbose)
        handler.emit(report)
        return 2

    report = run_checks(ctx)

    out_stream: TextIO = sys.stdout
    should_close = False
    if args.output_file:
        out_stream = open(args.output_file, "w", encoding="utf-8")
        should_close = True

    try:
        handler = OUTPUT_FACTORIES[args.format](out_stream, verbose=args.verbose)
        handler.emit(report)
    finally:
        if should_close:
            out_stream.close()

    return 1 if report.has_failures else 0


if __name__ == "__main__":
    sys.exit(main())
