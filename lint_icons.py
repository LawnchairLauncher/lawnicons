#!/usr/bin/env python3
import argparse
import json
import logging
import re
import sys
import time
import xml.etree.ElementTree as ET
from abc import ABC, abstractmethod
from concurrent.futures import ProcessPoolExecutor
from dataclasses import dataclass, field, asdict
from enum import Enum, IntEnum
from pathlib import Path
from typing import Callable, List, Dict, Optional, Any, Type

try:
    from svgelements import SVG, Path as SVGPath
    HAS_SVGELEMENTS = True
except ImportError:
    HAS_SVGELEMENTS = False
    SVG = None
    SVGPath = None

# --- Configuration ---
logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.INFO)
logger = logging.getLogger(__name__)


class Status(Enum):
    PASS = "PASS"
    WARN = "WARN"
    FAIL = "FAIL"
    REVIEW = "REVIEW"
    EXEMPT = "EXEMPT"


class Speed(IntEnum):
    # Regex / String only
    FAST = 1
    # XML Tree
    MEDIUM = 2
    # svgelements / Geometry
    SLOW = 3


@dataclass(frozen=True)
class Outcome:
    id: str
    name: str
    output: str


@dataclass(frozen=True)
class RuleDefinition:
    id: str
    name: str
    description: str
    outcomes: Any  # Namespace class or Dict[str, Outcome]
    category: str = "Core"


@dataclass(frozen=True)
class Finding:
    outcome: Outcome
    metadata: Dict[str, Any] = field(default_factory=dict)
    status: Status = Status.FAIL


@dataclass
class CheckContext:
    filename: str
    raw_content: str
    xml_tree: Optional[ET.Element] = None
    svg_doc: Optional[Any] = None


RULES_REGISTRY: List[tuple[Callable, RuleDefinition]] = []


def register_rule(id: str, name: str, outcomes: Any, description: str = "", category: str = "Core"):
    """Decorator to register a rule with structured outcomes."""

    def decorator(func):
        rule_def = RuleDefinition(id, name, description, outcomes, category)
        RULES_REGISTRY.append((func, rule_def))
        return func

    return decorator


@dataclass(frozen=True)
class CheckResult:
    id: str
    name: str
    category: str
    status: Status
    outcome_id: Optional[str] = None
    outcome_name: Optional[str] = None
    message: str = ""
    metadata: Dict[str, Any] = field(default_factory=dict)
    duration_ms: float = 0.0


@dataclass
class FileReport:
    file_path: str
    results: List[CheckResult] = field(default_factory=list)
    error: Optional[str] = None

    @property
    def has_failure(self):
        return any(r.status == Status.FAIL for r in self.results)


# --- Helper Functions ---
STYLE_PAIR_RE = re.compile(r'([\w-]+)\s*:\s*([^;]+)')


def parse_style_attribute(style_str: Optional[str]) -> Dict[str, str]:
    if not style_str:
        return {}
    return {k: v.strip() for k, v in STYLE_PAIR_RE.findall(style_str.lower())}


# --- Rules Implementation ---

# --- Core Rules ---

class C01Outcomes:
    WRONG_SIZE = Outcome("WRONG_SIZE", "Wrong canvas size", "canvas: {width}×{height} px")
    MISSING = Outcome("MISSING", "No canvas", "no canvas")
    MALFORMED = Outcome("MALFORMED", "Malformed viewBox", "canvas: malformed viewBox '{vb}'")


@register_rule(
    id="C01",
    name="Canvas size",
    description="Ensures canvas is exactly 192x192.",
    outcomes=C01Outcomes
)
def rule_canvas_size(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM:
        return []
    if ctx.xml_tree is None:
        return [Finding(C01Outcomes.MISSING)]

    vb_str = ctx.xml_tree.get('viewBox', '')
    vb = vb_str.split()
    w = ctx.xml_tree.get('width', '').strip('px ')
    h = ctx.xml_tree.get('height', '').strip('px ')

    if vb == ['0', '0', '192', '192'] or (w == '192' and h == '192'):
        return []

    if not vb and not (w or h):
        return [Finding(C01Outcomes.MISSING)]

    if vb and len(vb) != 4:
        return [Finding(C01Outcomes.MALFORMED, {"vb": vb_str})]

    return [Finding(C01Outcomes.WRONG_SIZE, {"width": w or "?", "height": h or "?"})]


@register_rule(id="C02", name="Icon too small", outcomes={},
               description="Checks if icons are too small.")
def rule_placeholder_too_small(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    return []


@register_rule(id="C03", name="Outside content", outcomes={},
               description="Checks for elements outside the content area.")
def rule_placeholder_outside_content(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    return []


@register_rule(id="C04", name="Square size", outcomes={},
               description="Checks size of square icons.")
def rule_placeholder_square_size(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    return []


class C05Outcomes:
    FORBIDDEN_EFFECT = Outcome("FORBIDDEN_EFFECT", "Forbidden effect", "{effect}: yes")
    OPACITY = Outcome("OPACITY", "Opacity", "opacity: {opacity}%")


@register_rule(
    id="C05",
    name="Effects",
    description="Bans transparency and opacity effects.",
    outcomes=C05Outcomes
)
def rule_effects(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    findings = []
    forbidden_attrs = ['opacity', 'fill-opacity', 'stroke-opacity', 'stop-opacity', 'filter']
    forbidden_style_props = set(forbidden_attrs)

    def parse_opacity(val: str) -> Optional[int]:
        val = val.strip().lower()
        try:
            if val.endswith('%'):
                return int(float(val[:-1]))
            opacity_val = float(val)
            if opacity_val >= 1.0:
                return None
            return int(opacity_val * 100)
        except ValueError:
            return None

    effects = set()
    opacities = set()

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]
        if tag == 'filter':
            effects.add('filter')
        elif tag.startswith('fe'):
            effects.add('shadow or effect')

        for attr in forbidden_attrs:
            val = el.get(attr)
            if not val:
                continue

            normalized = val.strip().lower()
            if 'opacity' in attr:
                opacity_pct = parse_opacity(normalized)
                if opacity_pct is not None:
                    opacities.add(opacity_pct)
                continue

            if attr == 'filter':
                effects.add("shadow or effect" if any(x in normalized for x in ["shadow", "blur"]) else "filter")
            else:
                effects.add(attr)

        style_val = el.get('style')
        if style_val:
            style_map = parse_style_attribute(style_val)
            for prop, value in style_map.items():
                if prop not in forbidden_style_props:
                    continue

                normalized = value.strip().lower()
                if 'opacity' in prop:
                    opacity_pct = parse_opacity(normalized)
                    if opacity_pct is not None:
                        opacities.add(opacity_pct)
                    continue

                if prop == 'filter':
                    effects.add("shadow or effect" if any(x in normalized for x in ["shadow", "blur"]) else "filter")
                else:
                    effects.add(prop)

    for op in sorted(opacities):
        findings.append(Finding(C05Outcomes.OPACITY, {"opacity": op}))

    if effects:
        if "shadow or effect" in effects:
            findings.append(Finding(C05Outcomes.FORBIDDEN_EFFECT, {"effect": "shadow or effect"}))
        elif "filter" in effects:
            findings.append(Finding(C05Outcomes.FORBIDDEN_EFFECT, {"effect": "filter"}))
        else:
            for effect in sorted(effects):
                findings.append(Finding(C05Outcomes.FORBIDDEN_EFFECT, {"effect": effect}))

    return findings


class C06Outcomes:
    FORBIDDEN_WEIGHT = Outcome("FORBIDDEN_WEIGHT", "Forbidden stroke weight", "stroke: {weight} px")
    NON_NUMERIC = Outcome("NON_NUMERIC", "Non-numeric stroke weight", "stroke: {weight}")
    MINIMAL_ICON = Outcome("MINIMAL_ICON", "Minimal icon", "stroke: {weight} px (review)")


@register_rule(
    id="C06",
    name="Stroke weight",
    description="Validates stroke weights (6,8,10,12,14px).",
    outcomes=C06Outcomes
)
def rule_stroke_weight(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    valid = {6.0, 8.0, 10.0, 12.0, 14.0}
    strokes = []
    findings = []
    for el in ctx.xml_tree.iter():
        sw = el.get('stroke-width')
        if sw:
            try:
                val = float(sw.replace('px', '').strip())
                strokes.append(val)
            except ValueError:
                findings.append(Finding(C06Outcomes.NON_NUMERIC, {"weight": sw}))

    if not strokes:
        return findings

    unique_weights = set(strokes)
    forbidden = unique_weights - valid
    if forbidden:
        findings.append(Finding(C06Outcomes.FORBIDDEN_WEIGHT,
                                {"weight": ", ".join(map(str, sorted(forbidden)))}))

    if len(strokes) == 1:
        weight = strokes[0]
        if weight in {10.0, 14.0}:
            findings.append(Finding(C06Outcomes.MINIMAL_ICON, {"weight": weight}, Status.REVIEW))
    elif any(w < 12.0 for w in unique_weights):
        findings.append(Finding(C06Outcomes.MINIMAL_ICON,
                                {"weight": ", ".join(map(str, sorted(unique_weights)))},
                                Status.REVIEW))

    return findings


class C07Outcomes:
    NON_BLACK_STROKE = Outcome("NON_BLACK_STROKE", "Non-black stroke", "color: {color}")
    IMPLICIT_FILL = Outcome("IMPLICIT_FILL", "Implicit fill", "fill: implicit black")
    UNAUTHORIZED_FILL = Outcome("UNAUTHORIZED_FILL", "Unauthorized fill", "fill: {fill}")
    HAS_FILL = Outcome("HAS_FILL", "Fill", "fill: yes")


@register_rule(
    id="C07",
    name="Fill color",
    description="Checks fill state (expects 'none', no stroke, or black color).",
    outcomes=C07Outcomes
)
def rule_fill_color(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    allowed_colors = {'none', 'transparent', '#000000', '#000', 'black'}
    findings = []

    # The default SVG initial value for fill is technically 'black'
    root = ctx.xml_tree.getroot() if hasattr(ctx.xml_tree, 'getroot') else ctx.xml_tree

    root_style = parse_style_attribute(root.get('style'))
    root_fill = root_style.get('fill') or root.get('fill', 'black').lower()

    stack = [(root, root_fill)]

    has_fill = False

    while stack:
        el, inherited_fill = stack.pop()
        tag = el.tag.split('}')[-1]

        local_style = parse_style_attribute(el.get('style'))

        local_stroke = local_style.get('stroke') or el.get('stroke')
        if local_stroke:
            local_stroke = local_stroke.lower()

        local_fill = local_style.get('fill') or el.get('fill')
        current_fill = local_fill.lower() if local_fill else inherited_fill

        if tag in ['defs', 'style', 'clipPath', 'linearGradient', 'radialGradient', 'g', 'svg']:
            for child in el:
                stack.append((child, current_fill))
            continue

        if current_fill not in {'none', 'transparent'}:
            has_fill = True

        if current_fill not in allowed_colors:
            findings.append(Finding(C07Outcomes.UNAUTHORIZED_FILL, {"fill": current_fill}))

        if local_stroke:
            if local_stroke not in allowed_colors:
                findings.append(Finding(C07Outcomes.NON_BLACK_STROKE, {"color": local_stroke}))

            if not local_fill and current_fill == 'black':
                findings.append(Finding(C07Outcomes.IMPLICIT_FILL))

        for child in el:
            stack.append((child, current_fill))

    if has_fill:
        findings.append(Finding(C07Outcomes.HAS_FILL))

    return findings


class RoundingOutcomes:
    MISSING_CAP = Outcome("MISSING_CAP", "Missing round cap", "cap: square/butt")
    MISSING_JOIN = Outcome("MISSING_JOIN", "Missing round join", "join: miter/bevel")
    MISSING_RECT_ROUND = Outcome("MISSING_RECT_ROUND", "Rect missing rounding", "rect: no rx")
    INVALID_RECT_ROUND = Outcome("INVALID_RECT_ROUND", "Invalid rect rounding", "rect rx: {rx}")


@register_rule(id="C08", name="Rounding caps", outcomes=RoundingOutcomes,
               description="Validates 'round' stroke-linecap.")
def rule_rounding_caps(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    root_cap = ctx.xml_tree.get('stroke-linecap')
    if root_cap == 'round':
        return []

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]
        if not el.get('stroke') or tag not in ['path', 'line', 'polyline']:
            continue
        is_open = True
        if tag == 'path':
            d = el.get('d', '').strip()
            if not d or d.endswith('z') or d.endswith('Z'):
                is_open = False
            elif max_speed >= Speed.SLOW and HAS_SVGELEMENTS:
                try:
                    is_open = not SVGPath(d).closed  # type: ignore
                except Exception:
                    pass
        if is_open and el.get('stroke-linecap') != 'round':
            return [Finding(RoundingOutcomes.MISSING_CAP)]
    return []


@register_rule(id="C09", name="Rounding joints", outcomes=RoundingOutcomes,
               description="Validates 'round' stroke-linejoin.")
def rule_rounding_joints(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    root_join = ctx.xml_tree.get('stroke-linejoin')
    if root_join == 'round':
        return []

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]
        if not el.get('stroke') or tag in ['svg', 'g', 'defs']:
            continue
        if el.get('stroke-linejoin') != 'round':
            return [Finding(RoundingOutcomes.MISSING_JOIN)]
    return []


@register_rule(id="C10", name="Rounded corners", outcomes=RoundingOutcomes,
               description="Validates <rect> corner rounding.")
def rule_rounded_corners(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    if max_speed < Speed.MEDIUM or ctx.xml_tree is None:
        return []

    for rect in ctx.xml_tree.iter():
        tag = rect.tag.split('}')[-1]
        if tag != 'rect':
            continue
        rx = rect.get('rx')
        if rx is None:
            return [Finding(RoundingOutcomes.MISSING_RECT_ROUND)]
        try:
            if not (6 <= float(rx) <= 32):
                return [Finding(RoundingOutcomes.INVALID_RECT_ROUND, {"rx": rx})]
        except (ValueError, TypeError):
            return [Finding(RoundingOutcomes.INVALID_RECT_ROUND, {"rx": rx})]
    return []


# --- Quality Rules ---

@register_rule(id="Q01", name="Adjacent stroke weight", outcomes={},
               description="Checks for large differences in adjacent stroke weights.",
               category="Quality")
def rule_placeholder_adjacent_stroke_weight(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    """Quality: [Placeholder] Checks for large differences in adjacent stroke weights."""
    return []


@register_rule(id="Q02", name="Black spots", outcomes={},
               description="Detects unintentional black spots from overlapping paths.",
               category="Quality")
def rule_placeholder_black_spots(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    """Quality: [Placeholder] Detects unintentional black spots from overlapping paths."""
    return []


@register_rule(id="Q03", name="Strokes too close", outcomes={},
               description="Checks for strokes that are too close to each other.",
               category="Quality")
def rule_placeholder_strokes_too_close(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    """Quality: [Placeholder] Checks for strokes that are too close to each other."""
    return []


@register_rule(id="Q04", name="Visual alignment", outcomes={},
               description="Checks for visual alignment.", category="Quality")
def rule_placeholder_visual_alignment(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    """Quality: [Placeholder] Checks for visual alignment instead of bounding-box alignment."""
    return []


class O01Outcomes:
    TOO_LARGE = Outcome("TOO_LARGE", "SVG too large", "size: {size} KB")


@register_rule(id="O01", name="SVG size", outcomes=O01Outcomes,
               description="Flags SVGs larger than 3KB.", category="Optimization")
def rule_svg_size(ctx: CheckContext, max_speed: Speed) -> List[Finding]:
    size_kb = len(ctx.raw_content.encode('utf-8')) / 1024
    if size_kb > 3:
        return [Finding(O01Outcomes.TOO_LARGE, {"size": round(size_kb, 1)}, Status.FAIL)]
    return []


# --- Output System (Modular) ---

class OutputHandler(ABC):
    """Base class for modular output formats."""

    def __init__(self, dest: Any = sys.stdout, verbose: bool = False):
        self.dest = dest
        self.verbose = verbose
        self.failed_count = 0

    @abstractmethod
    def start(self): pass

    @abstractmethod
    def process(self, report: FileReport): pass

    @abstractmethod
    def finish(self): pass


class ConsoleOutput(OutputHandler):
    COLORS = {
        Status.PASS: "\033[92m",  # Green
        Status.WARN: "\033[93m",  # Yellow
        Status.FAIL: "\033[91m",  # Red
        Status.REVIEW: "\033[95m",  # Magenta
        Status.EXEMPT: "\033[90m",  # Grey
    }
    RESET = "\033[0m"

    def start(self):
        pass

    def process(self, report: FileReport):
        if report.error:
            print(f"{self.COLORS[Status.FAIL]}ERR{self.RESET} {report.file_path}: {report.error}",
                  file=self.dest)
            self.failed_count += 1
            return

        actionable = [r for r in report.results if r.status not in (Status.PASS, Status.EXEMPT)]
        if any(r.status == Status.FAIL for r in report.results):
            self.failed_count += 1

        if not self.verbose and not actionable:
            return

        print(f"\n{report.file_path}", file=self.dest)
        for r in report.results:
            if not self.verbose and r.status == Status.PASS:
                continue
            color = self.COLORS.get(r.status, "")
            timing = f" ({r.duration_ms:.1f} ms)" if self.verbose else ""
            print(
                f"  [{color}{r.status.name:6}{self.RESET}] [{r.category}: {r.id}] {r.message}{timing}",
                file=self.dest)

    def finish(self):
        print(f"\nAnalysis complete. Failed files: {self.failed_count}", file=self.dest)


class JsonOutput(OutputHandler):
    def start(self):
        self.dest.write("[\n")
        self.first = True

    def process(self, report: FileReport):
        if any(r.status == Status.FAIL for r in report.results):
            self.failed_count += 1

        actionable = [r for r in report.results if r.status != Status.PASS]
        if not self.verbose and not actionable and not report.error:
            return

        if not self.first:
            self.dest.write(",\n")
        self.first = False

        data = asdict(report)
        for r in data['results']:
            r['status'] = r['status'].value

        if not self.verbose:
            data['results'] = [r for r in data['results'] if r['status'] != "PASS"]

        self.dest.write(json.dumps(data))

    def finish(self):
        self.dest.write("\n]\n")


OUTPUT_FACTORIES: Dict[str, Type[OutputHandler]] = {
    'text': ConsoleOutput,
    'json': JsonOutput,
}


# --- Worker Logic ---


def analyze_file(filepath_str: str, max_speed: Speed,
                 exceptions: Dict[str, List[str]]) -> FileReport:
    path = Path(filepath_str)
    report = FileReport(filepath_str)
    filename = path.name

    # 1. Load context
    try:
        raw = path.read_text(encoding='utf-8')
    except Exception as e:
        report.error = f"Read Error: {e}"
        return report

    ctx = CheckContext(filename=filename, raw_content=raw)

    # Lazy Load parsing based on max_speed
    if max_speed >= Speed.MEDIUM:
        try:
            ctx.xml_tree = ET.fromstring(raw)
        except ET.ParseError:
            pass  # Handled inside rules that require XML

    if max_speed >= Speed.SLOW and HAS_SVGELEMENTS and ctx.xml_tree is not None:
        try:
            from io import BytesIO
            ctx.svg_doc = SVG.parse(  # type: ignore
                BytesIO(raw.encode('utf-8')))  # type: ignore
        except Exception:
            pass

    # 2. Execute Rules sequentially
    for rule_func, rule_def in RULES_REGISTRY:
        t0 = time.perf_counter()

        try:
            findings = rule_func(ctx, max_speed)
        except Exception as e:
            findings = [Finding(Outcome("EXCEPTION", "Rule Exception", str(e)), status=Status.FAIL)]

        dt = (time.perf_counter() - t0) * 1000

        if not findings:
            report.results.append(CheckResult(
                id=rule_def.id,
                name=rule_def.name,
                category=rule_def.category,
                status=Status.PASS,
                duration_ms=dt
            ))
            continue

        for f in findings:
            status = f.status
            msg = f.outcome.output.format(**f.metadata)

            if status not in (Status.PASS, Status.WARN):
                if filename in exceptions.get(rule_def.id, []):
                    status = Status.EXEMPT
                    msg = f"[EXEMPTED] {msg}"

            report.results.append(CheckResult(
                id=rule_def.id,
                name=rule_def.name,
                category=rule_def.category,
                status=status,
                outcome_id=f.outcome.id,
                outcome_name=f.outcome.name,
                message=msg,
                metadata=f.metadata,
                duration_ms=dt
            ))

    return report


# --- Main ---


def main():
    parser = argparse.ArgumentParser(description="SVG Linter & Optimizer")
    parser.add_argument("inputs", nargs="+", help="One or more SVG files and/or directories")
    parser.add_argument("--verbose", action="store_true",
                        help="Show all checks, including PASS")
    parser.add_argument("--format", choices=OUTPUT_FACTORIES.keys(),
                        default="text", help="Output format")
    parser.add_argument(
        "--output-file", help="Write output to file instead of stdout")
    parser.add_argument(
        "--speed", choices=["fast", "medium", "slow"], default="slow", help="Max check complexity")
    parser.add_argument("--workers", type=int, default=4,
                        help="Parallel processes")
    parser.add_argument("--exceptions", default="exceptions.json",
                        help="JSON file for allowlists")

    args = parser.parse_args()

    # Load Exceptions
    exceptions = {}
    exc_path = Path(args.exceptions)
    if exc_path.exists():
        try:
            exceptions = json.loads(exc_path.read_text())
            # Format: { "WGT-01": ["icon.svg", "icon2.svg"] }
        except Exception as e:
            print(f"Error loading exceptions: {e}")
            sys.exit(1)

    speed_map = {
        "fast": Speed.FAST,
        "medium": Speed.MEDIUM,
        "slow": Speed.SLOW
    }
    max_speed = speed_map[args.speed]

    files: List[str] = []
    seen: set[str] = set()
    for raw_input in args.inputs:
        input_path = Path(raw_input)
        if input_path.is_file():
            candidates = [input_path]
        elif input_path.is_dir():
            candidates = list(input_path.rglob("*.svg"))
        else:
            print(f"Input not found: {input_path}")
            sys.exit(2)

        for candidate in candidates:
            candidate_str = str(candidate)
            if candidate_str in seen:
                continue
            seen.add(candidate_str)
            files.append(candidate_str)

    if not files:
        print("No SVG files found.")
        sys.exit(0)

    out_stream = sys.stdout
    if args.output_file:
        out_stream = open(args.output_file, 'w', encoding='utf-8')
    handler = OUTPUT_FACTORIES[args.format](
        out_stream, verbose=args.verbose)

    handler.start()

    # imap_unordered is crucial for large file counts: it yields results as they finish.
    # We use a chunksize to reduce IPC overhead.
    workers = max(1, args.workers)

    chunk_size = max(1, len(files) // (workers * 4))

    with ProcessPoolExecutor(max_workers=workers) as executor:
        # We use a partial or a list comprehension approach
        futures = executor.map(
            analyze_file,
            files, [max_speed] * len(files), [exceptions] * len(files),
            chunksize=chunk_size
        )

        for report in futures:
            handler.process(report)

    handler.finish()

    if args.output_file:
        out_stream.close()


if __name__ == "__main__":
    main()
