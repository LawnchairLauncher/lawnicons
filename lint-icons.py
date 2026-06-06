import re
import sys
import time
import logging
import argparse
import json
import xml.etree.ElementTree as ET # lint: ignore
from pathlib import Path
from enum import Enum, IntEnum
from dataclasses import dataclass, field, asdict
from concurrent.futures import ProcessPoolExecutor
from abc import ABC, abstractmethod
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
    FAST = 1     # Regex / String only
    MEDIUM = 2   # XML Tree
    SLOW = 3     # svgelements / Geometry


@dataclass
class CheckContext:
    filename: str
    raw_content: str
    xml_tree: Optional[ET.Element] = None
    svg_doc: Optional[Any] = None


RULES_REGISTRY: List[tuple[Callable, str, str]] = []


def register_rule(rule_id: str, category: str = "Core"):
    """Decorator to register a pure function rule."""
    def decorator(func):
        RULES_REGISTRY.append((func, rule_id, category))
        return func
    return decorator


@dataclass(frozen=True)
class CheckResult:
    id: str
    category: str
    check_name: str
    status: Status
    message: str
    duration_ms: float


@dataclass
class FileReport:
    file_path: str
    results: List[CheckResult] = field(default_factory=list)
    error: Optional[str] = None

    @property
    def has_failure(self):
        return any(r.status == Status.FAIL for r in self.results)


# --- Plugin System ---
PLUGIN_REGISTRY: Dict[Speed, List[tuple[Callable, str]]] = {
    s: [] for s in Speed}


def register_check(speed: Speed, check_id: str):
    def decorator(func):
        PLUGIN_REGISTRY[speed].append((func, check_id))
        return func
    return decorator

# --- Helper Functions ---
STYLE_PAIR_RE = re.compile(r'([\w-]+)\s*:\s*([^;]+)')

def parse_style_attribute(style_str: Optional[str]) -> Dict[str, str]:
    if not style_str:
        return {}
    return {k: v.strip() for k, v in STYLE_PAIR_RE.findall(style_str.lower())}

# --- Rules Implementation ---

# --- Core Rules ---

@register_rule(rule_id="C01", category="Core")
def rule_canvas_size(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Ensures canvas is exactly 192x192."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped canvas check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing, cannot verify canvas."
    vb = ctx.xml_tree.get('viewBox', '').split()
    w = ctx.xml_tree.get('width', '').strip('px ')
    h = ctx.xml_tree.get('height', '').strip('px ')
    if vb == ['0', '0', '192', '192'] or (w == '192' and h == '192'):
        return Status.PASS, "Confirmed 192x192."
    return Status.FAIL, f"Invalid canvas: viewBox={vb}, w={w}, h={h}"


@register_rule(rule_id="C02", category="Core")
def rule_placeholder_too_small(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: [Placeholder] Checks if icons are too small."""
    return Status.PASS, "Placeholder: C02 Not Implemented (Requires Geometry)."


@register_rule(rule_id="C03", category="Core")
def rule_placeholder_outside_content(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: [Placeholder] Checks for elements outside the content area."""
    return Status.PASS, "Placeholder: C03 Not Implemented (Requires Geometry)."


@register_rule(rule_id="C04", category="Core")
def rule_placeholder_square_size(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: [Placeholder] Checks size of square icons."""
    return Status.PASS, "Placeholder: C04 Not Implemented (Requires Geometry)."


@register_rule(rule_id="C05", category="Core")
def rule_transparency(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Bans transparency and opacity effects."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped transparency check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing, cannot check transparency."

    forbidden_attrs = ['opacity', 'fill-opacity', 'stroke-opacity', 'stop-opacity', 'filter']
    forbidden_style_props = set(forbidden_attrs)

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]

        for attr in forbidden_attrs:
            val = el.get(attr)
            if not val:
                continue

            normalized = val.strip().lower()
            if 'opacity' in attr and normalized in {'1', '1.0'}:
                continue

            return Status.FAIL, f"Forbidden effect/transparency '{attr}' in <{tag}>"

        style_val = el.get('style')
        if style_val:
            style_map = parse_style_attribute(style_val)
            for prop, value in style_map.items():
                if prop not in forbidden_style_props:
                    continue

                normalized = value.strip().lower()
                if 'opacity' in prop and normalized in {'1', '1.0'}:
                    continue

                return Status.FAIL, f"Forbidden effect/transparency '{prop}' in style on <{tag}>"

    return Status.PASS, "No transparency found."


@register_rule(rule_id="C06", category="Core")
def rule_stroke_weight(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Validates stroke weights (6,8,10,12,14px)."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped stroke weight check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing."

    valid = {6.0, 8.0, 10.0, 12.0, 14.0}
    strokes = []
    for el in ctx.xml_tree.iter():
        sw = el.get('stroke-width')
        if sw:
            try:
                strokes.append(float(sw.replace('px', '').strip()))
            except ValueError:
                return Status.FAIL, f"Non-numeric stroke-width: {sw}"
    if not strokes:
        return Status.PASS, "No stroked elements."

    unique_weights = set(strokes)
    if not unique_weights.issubset(valid):
        return Status.FAIL, f"Forbidden weights: {unique_weights - valid}"

    if len(strokes) == 1:
        weight = strokes[0]
        if weight == 14.0:
            return Status.REVIEW, "Minimal icon (14px): Confirm optical weight matches set."
        if weight == 10.0:
            return Status.REVIEW, "Dense     icon (10px): Confirm it doesn't look too thin."
        return Status.REVIEW, f"Minimal icon using fine-detail weight ({weight}px)."

    if any(w < 12.0 for w in unique_weights):
        return Status.REVIEW, f"Fine details/dense icon detected: {unique_weights}"

    return Status.PASS, f"Weights valid: {unique_weights}"

@register_rule(rule_id="C07", category="Core")
def rule_fill_color(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Checks fill state (expects 'none', no stroke, or black color)."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped fill check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing."

    allowed_colors = {'none', '#000000', '#000', 'black'}

    # The default SVG initial value for fill is technically 'black'
    root = ctx.xml_tree.getroot() if hasattr(ctx.xml_tree, 'getroot') else ctx.xml_tree

    root_style = parse_style_attribute(root.get('style'))
    root_fill = root_style.get('fill') or root.get('fill', 'black').lower()

    stack = [(root, root_fill)]

    while stack:
        el, inherited_fill = stack.pop()
        tag = el.tag.split('}')[-1]

        local_style = parse_style_attribute(el.get('style'))

        local_stroke = local_style.get('stroke') or el.get('stroke')
        if local_stroke:
            local_stroke = local_stroke.lower()

        local_fill = local_style.get('fill') or el.get('fill')
        current_fill = local_fill.lower() if local_fill else inherited_fill

        if tag in ['defs', 'style', 'clipPath', 'linearGradient', 'radialGradient']:
            for child in el:
                stack.append((child, current_fill))
            continue

        if tag in ['g', 'svg']:
            for child in el:
                stack.append((child, current_fill))
            continue

        if local_stroke:
            if local_stroke not in allowed_colors:
                return Status.FAIL, f"<{tag}> has non-black stroke '{local_stroke}'."

            if not local_fill and current_fill == 'black':
                return Status.REVIEW, f"<{tag}> has implicit fill (renders black because no parent sets 'none')."

            if current_fill not in allowed_colors:
                return Status.FAIL, f"<{tag}> resolves to unauthorized fill '{current_fill}'. Expected 'none' or black."

        for child in el:
            stack.append((child, current_fill))

    return Status.PASS, "Fill states are compliant."


@register_rule(rule_id="C08", category="Core")
def rule_rounding_caps(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Validates 'round' stroke-linecap on open paths."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped cap check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing."

    root_cap = ctx.xml_tree.get('stroke-linecap')
    if root_cap == 'round':
        return Status.PASS, "Root defines 'round' cap."

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]
        if not el.get('stroke'):
            continue
        if tag not in ['path', 'line', 'polyline']:
            continue  # stroke-linecap is only relevant for open-ended shapes

        is_open = True
        if tag == 'path':
            d = el.get('d', '').strip()
            if not d or d.endswith('z') or d.endswith('Z'):
                is_open = False
            elif max_speed >= Speed.SLOW and HAS_SVGELEMENTS:
                try:
                    is_open = not SVGPath(d).closed  # type: ignore
                except Exception:
                    return Status.FAIL, "Unparseable path data."

        if is_open and el.get('stroke-linecap') != 'round':
            return Status.FAIL, f"Open <{tag}> lacks 'round' stroke-linecap."

    return Status.PASS, "All open paths have round caps."


@register_rule(rule_id="C09", category="Core")
def rule_rounding_joints(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Validates 'round' stroke-linejoin."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped join check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing."

    root_join = ctx.xml_tree.get('stroke-linejoin')
    if root_join == 'round':
        return Status.PASS, "Root defines 'round' join."

    for el in ctx.xml_tree.iter():
        tag = el.tag.split('}')[-1]
        if not el.get('stroke') or tag in ['svg', 'g', 'defs']:
            continue
        if el.get('stroke-linejoin') != 'round':
            return Status.FAIL, f"<{tag}> lacks 'round' stroke-linejoin."
    return Status.PASS, "All path joints are round."


@register_rule(rule_id="C10", category="Core")
def rule_rounded_corners(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Core: Validates <rect> corner rounding (rx)."""
    if max_speed < Speed.MEDIUM:
        return Status.PASS, "Skipped rect rounding check."
    if ctx.xml_tree is None:
        return Status.FAIL, "XML missing."

    for rect in ctx.xml_tree.iter():
        tag = rect.tag.split('}')[-1]
        if tag != 'rect':
            continue
        try:
            rx = rect.get('rx')
            if rx is None:
                return Status.FAIL, "Rect lacks rx attribute."
            if not (6 <= float(rx) <= 32):
                return Status.FAIL, f"Rect rx='{rx}' out of 6-32 range."
        except (ValueError, TypeError):
            return Status.FAIL, f"Rect has invalid rx: {rect.get('rx')}"
    return Status.PASS, "All rects properly rounded."

# --- Quality Rules ---

@register_rule(rule_id="Q01", category="Quality")
def rule_placeholder_adjacent_stroke_weight(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Quality: [Placeholder] Checks for large differences in adjacent stroke weights."""
    return Status.PASS, "Placeholder: Q01 Not Implemented (Requires SLOW/Geometry)."


@register_rule(rule_id="Q02", category="Quality")
def rule_placeholder_black_spots(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Quality: [Placeholder] Detects unintentional black spots from overlapping paths."""
    return Status.PASS, "Placeholder: Q02 Not Implemented (Requires SLOW/Geometry)."


@register_rule(rule_id="Q03", category="Quality")
def rule_placeholder_strokes_too_close(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Quality: [Placeholder] Checks for strokes that are too close to each other."""
    return Status.PASS, "Placeholder: Q03 Not Implemented (Requires SLOW/Geometry)."


@register_rule(rule_id="Q04", category="Quality")
def rule_placeholder_visual_alignment(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Quality: [Placeholder] Checks for visual alignment instead of bounding-box alignment."""
    return Status.PASS, "Placeholder: Q04 Not Implemented (Requires SLOW/Geometry)."

# --- Optimization Rules ---
@register_rule(rule_id="O01", category="Optimization")
def rule_svg_size(ctx: CheckContext, max_speed: Speed) -> tuple[Status, str]:
    """Optimization: Flags SVGs larger than 3KB."""
    if len(ctx.raw_content.encode('utf-8')) > 3 * 1024:
        return Status.WARN, "SVG file size exceeds 3KB."
    return Status.PASS, "SVG file size is within limits."

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
        Status.PASS: "\033[92m",   # Green
        Status.WARN: "\033[93m",   # Yellow
        Status.FAIL: "\033[91m",   # Red
        Status.REVIEW: "\033[95m",  # Magenta
        Status.EXEMPT: "\033[90m",  # Grey
    }
    RESET = "\033[0m"

    def start(self):
        pass

    def process(self, report: FileReport):
        # 1. Handle Catastrophic Errors
        if report.error:
            print(
                f"{self.COLORS[Status.FAIL]}ERR{self.RESET} {report.file_path}: {report.error}",
                file=self.dest,
            )
            self.failed_count += 1
            return

        # 2. Determine Visibility
        # Actionable = anything that isn't a PASS
        actionable_results = [
            r for r in report.results if r.status not in (Status.PASS, Status.EXEMPT)]
        has_failure = any(r.status == Status.FAIL for r in report.results)

        if has_failure:
            self.failed_count += 1

        # Skip printing if not verbose and nothing is wrong
        if not self.verbose and not actionable_results:
            return

        # 3. Printing Logic
        print(f"\n{report.file_path}", file=self.dest)
            if not self.verbose and r.status == Status.PASS:
                continue

            print(
                f"  [{color}{r.status.name:6}{self.RESET}] [{r.category}: {r.id}] {r.message}{timing}",
                file=self.dest,
            )

    def finish(self):
        print(f"\nAnalysis complete. Failed files: {self.failed_count}", file=self.dest)


class JsonOutput(OutputHandler):
    def start(self):
        self.dest.write("[\n")
        self.first = True

    def process(self, report: FileReport):
        # In non-verbose mode, skip files that passed everything perfectly
        actionable = [r for r in report.results if r.status != Status.PASS]
        if not self.verbose and not actionable and not report.error:
            return

        if not self.first:
            self.dest.write(",\n")
        self.first = False

        if any(r.status == Status.FAIL for r in report.results):
            self.failed_count += 1

        data = asdict(report)
        # Enums to strings for JSON
        for r in data['results']:
            r['status'] = r['status'].value

        # Strip PASS results from JSON if not verbose to save disk/bandwidth on 8k files
        if not self.verbose:
            data['results'] = [r for r in data['results'] if r['status'] != "PASS"]

        self.dest.write(json.dumps(data))

    def finish(self):
        self.dest.write("\n]\n")


class CompactOutput(OutputHandler):
    """
    Minimalist output:
    filename.svg: ID-01, ID-02
    """

    def start(self):
        pass

    def process(self, report: FileReport):
        # Gather IDs of all results that are FAIL
        failed_ids = [r.id for r in report.results if r.status == Status.FAIL]

        if report.error:
            # Handle catastrophic file errors (e.g. unreadable)
            self.dest.write(
                f"{Path(report.file_path).name}: CRITICAL_ERROR ({report.error}) \n")
            self.failed_count += 1
        elif failed_ids:
            # Sort IDs for consistent output
            ids_str = ", ".join(sorted(set(failed_ids)))
            self.dest.write(f"{Path(report.file_path).name}: {ids_str} \n")
            self.failed_count += 1

    def finish(self):
        # Summary sent to stderr to avoid polluting stdout if user pipes output
        if self.failed_count > 0:
            print(
                f"\nFound {self.failed_count} files with failures.", file=sys.stderr)


OUTPUT_FACTORIES: Dict[str, Type[OutputHandler]] = {
    'text': ConsoleOutput,
    'json': JsonOutput,
    'compact': CompactOutput
}

# --- Worker Logic ---


def analyze_file(filepath_str: str, max_speed: Speed, exceptions: Dict[str, List[str]]) -> FileReport:
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
    for rule_func, rule_id, category in RULES_REGISTRY:
        t0 = time.perf_counter()

        try:
            status, msg = rule_func(ctx, max_speed)
        except Exception as e:
            status, msg = Status.FAIL, f"Rule Exception: {e}"

        dt = (time.perf_counter() - t0) * 1000

        # 3. Handle Exceptions / Exemptions natively
        if status not in (Status.PASS, Status.WARN):
            if filename in exceptions.get(rule_id, []):
                status = Status.EXEMPT
                msg = f"[EXEMPTED] {msg}"

        report.results.append(CheckResult(
            rule_id, category, rule_func.__name__, status, msg, dt))

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
            files, [max_speed]*len(files), [exceptions]*len(files),
            chunksize=chunk_size
        )

        for report in futures:
            handler.process(report)

    handler.finish()

    if args.output_file:
        out_stream.close()


if __name__ == "__main__":
    main()
