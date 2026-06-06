import os
import argparse
import re
import subprocess
import sys
from pathlib import Path

# --- Configuration ---
REPO_NAME = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER_RAW = os.getenv("PR_NUMBER")
PR_NUMBER = int(PR_NUMBER_RAW) if PR_NUMBER_RAW else None
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
GITHUB_BASE_REF = os.getenv("GITHUB_BASE_REF")

# Assuming a standard project structure
REPO_ROOT = Path(__file__).parent.parent.parent
APPFILTER_PATH = REPO_ROOT / "app/assets/appfilter.xml"
DRAWABLES_DIR = REPO_ROOT / "svgs/"
SVG_LINTER_PATH = REPO_ROOT / "lint-icons.py"
NAME_CHECKER_PATH = REPO_ROOT / ".github/scripts/name_checker.py"

BOT_SIGNATURE = "--- \n*Linter Bot Report*"
NEEDS_REVIEW_LABEL = "needs review"

# --- Main Logic ---


def configure_repo_paths(repo_dir: str | None) -> None:
    """Override repository-relative paths for local testing runs."""
    global REPO_ROOT, APPFILTER_PATH, DRAWABLES_DIR

    if not repo_dir:
        return

    resolved_repo_root = Path(repo_dir).expanduser().resolve()
    REPO_ROOT = resolved_repo_root
    APPFILTER_PATH = REPO_ROOT / "app/assets/appfilter.xml"
    DRAWABLES_DIR = REPO_ROOT / "svgs/"


def get_changed_svgs(base_ref: str) -> list[str]:
    """Finds SVG files changed in this PR compared to the target branch."""
    drawables_pathspec = DRAWABLES_DIR.relative_to(REPO_ROOT).as_posix()
    cmd = ["git", "diff", "--name-only",
           f"origin/{base_ref}", "HEAD", "--", drawables_pathspec]
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=REPO_ROOT,
    )
    if result.returncode != 0:
        return []
    changed_files = result.stdout.strip().splitlines()
    return [str(DRAWABLES_DIR / Path(f).name) for f in changed_files if f.endswith(".svg")]

def get_changed_drawables(base_ref: str) -> list[str]:
    """Extracts drawable names from added/modified lines in appfilter.xml diff."""
    cmd = ["git", "diff", f"origin/{base_ref}", "HEAD", "--",
           str(APPFILTER_PATH.as_posix())]
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=REPO_ROOT,
    )
    if result.returncode != 0:
        return []

    drawables = []
    for line in result.stdout.splitlines():
        # Only look at added lines (+ prefix, not the +++ header)
        if line.startswith('+') and not line.startswith('+++'):
            match = re.search(r'drawable="([^"]+)"', line)
            if match:
                drawables.append(match.group(1))
    return drawables

def run_linter(script_path: Path, args: list[str], accepted_exit_codes: set[int] | None = None) -> str:
    """Runs a linter script and returns its stdout."""
    if accepted_exit_codes is None:
        accepted_exit_codes = {0}

    try:
        child_env = os.environ.copy()
        child_env["PYTHONUTF8"] = "1"
        child_env["PYTHONIOENCODING"] = "utf-8"

        result = subprocess.run(
            [sys.executable, str(script_path)] + args,
            # check=False to capture output even on failure
            capture_output=True,
            text=True,
            encoding="utf-8",
            errors="replace",
            check=False,
            env=child_env,
        )

        if result.returncode not in accepted_exit_codes:
            error_output = result.stderr or result.stdout or "No output captured."
            return (
                f"CRITICAL_ERROR: {script_path.name} exited with code "
                f"{result.returncode}: {error_output}"
            )

        return result.stdout.strip()
    except Exception as e:
        return f"CRITICAL_ERROR: Failed to run {script_path.name}: {e}"


def find_bot_comment(pr):
    """Finds a previous comment made by this bot."""
    for comment in pr.get_issue_comments():
        if BOT_SIGNATURE in comment.body:
            return comment
    return None


def normalize_issue_message(message: str) -> str:
    """Convert internal check IDs (e.g. C01, Q04) to plain numbers."""
    token = message.strip()
    match = re.fullmatch(r"[A-Za-z]+(\d+)", token)
    if match:
        return str(int(match.group(1)))
    return token


def chunk_for_command(values: list[str], fixed_args: list[str], max_chars: int = 7000) -> list[list[str]]:
    """Split dynamic CLI values into safe chunks to avoid command-line length errors."""
    if not values:
        return []

    chunks: list[list[str]] = []
    base_len = sum(len(arg) + 1 for arg in fixed_args)
    current_chunk: list[str] = []
    current_len = base_len

    for value in values:
        value_len = len(value) + 1
        if current_chunk and current_len + value_len > max_chars:
            chunks.append(current_chunk)
            current_chunk = [value]
            current_len = base_len + value_len
            continue

        current_chunk.append(value)
        current_len += value_len

    if current_chunk:
        chunks.append(current_chunk)

    return chunks


def resolve_base_ref(explicit_base_ref: str | None) -> str:
    def normalize_ref(ref: str) -> str:
        ref = ref.strip()
        if ref.startswith("refs/heads/"):
            return ref[len("refs/heads/"):]
        if ref.startswith("origin/"):
            return ref[len("origin/"):]
        return ref

    if explicit_base_ref:
        return normalize_ref(explicit_base_ref)

    if GITHUB_BASE_REF:
        return normalize_ref(GITHUB_BASE_REF)

    # Fallback for local CLI runs where GITHUB_BASE_REF is not set.
    result = subprocess.run(
        ["git", "symbolic-ref", "refs/remotes/origin/HEAD"],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=REPO_ROOT,
    )
    if result.returncode == 0:
        ref = result.stdout.strip()
        if "/" in ref:
            return ref.rsplit("/", 1)[-1]

    return "main"


def collect_final_report(base_ref: str) -> str:
    changed_svg_files = get_changed_svgs(base_ref)
    all_errors = []

    # 1. Run SVG Linter on changed files only
    if changed_svg_files:
        print(f"Checking {len(changed_svg_files)} changed SVG files...")
        svg_base_args = ["--format", "compact"]
        for svg_chunk in chunk_for_command(changed_svg_files, svg_base_args):
            svg_errors = run_linter(SVG_LINTER_PATH, svg_base_args + svg_chunk)
            if svg_errors:
                all_errors.append(svg_errors)

    # 2. Run Name Checker (only on changed drawables)
    print("Checking appfilter.xml consistency...")
    changed_drawables = get_changed_drawables(base_ref)
    name_checker_args = [
        "--appfilter", str(APPFILTER_PATH),
        "--drawables-dir", str(DRAWABLES_DIR),
        "--format", "text",
    ]
    if changed_drawables:
        drawable_flag_args = name_checker_args + ["--changed-drawables"]
        for drawable_chunk in chunk_for_command(changed_drawables, drawable_flag_args):
            name_errors = run_linter(
                NAME_CHECKER_PATH,
                drawable_flag_args + drawable_chunk,
                accepted_exit_codes={0, 1},
            )
            if name_errors:
                all_errors.append(name_errors)
    else:
        print("No changed drawables detected; skipping name checker.")

    return "\n".join(all_errors).strip()


def parse_report_by_file(final_report: str) -> dict[str, dict[str, object]]:
    error_map: dict[str, dict[str, object]] = {}
    current_file: str | None = None
    code_pattern = re.compile(r"^[A-Za-z]+\d+$")

    def ensure_bucket(filename: str) -> dict[str, object]:
        if filename not in error_map:
            error_map[filename] = {
                "lint_icons_codes": set(),
                "name_checker_messages": [],
                "other_messages": [],
            }
        return error_map[filename]

    def add_unique_message(bucket: dict[str, object], key: str, message: str) -> None:
        if not message:
            return
        values = bucket.get(key)
        if isinstance(values, list) and message not in values:
            values.append(message)

    for raw_line in final_report.splitlines():
        line = raw_line.strip()
        if not line:
            continue

        file_parts = [p.strip() for p in line.split(":", 1)]
        if len(file_parts) == 2 and file_parts[0].lower().endswith(".svg"):
            current_file = file_parts[0]
            bucket = ensure_bucket(current_file)
            payload = file_parts[1].strip()
            if not payload:
                continue

            # lint-icons compact format: "icon.svg: C01, C05"
            payload_tokens = [token.strip() for token in payload.split(",") if token.strip()]
            if payload_tokens and all(code_pattern.fullmatch(token) for token in payload_tokens):
                cast_codes = bucket["lint_icons_codes"]
                if isinstance(cast_codes, set):
                    for token in payload_tokens:
                        normalized = normalize_issue_message(token)
                        if normalized.isdigit():
                            cast_codes.add(normalized)
                continue

            # name-checker text format: "icon.svg: NR03: Rename: ..."
            finding_parts = [p.strip() for p in payload.split(":", 1)]
            if len(finding_parts) == 2 and code_pattern.fullmatch(finding_parts[0]):
                add_unique_message(bucket, "name_checker_messages", finding_parts[1])
                continue

            normalized = normalize_issue_message(payload)
            if normalized.isdigit():
                cast_codes = bucket["lint_icons_codes"]
                if isinstance(cast_codes, set):
                    cast_codes.add(normalized)
            else:
                add_unique_message(bucket, "other_messages", payload)
            continue

        if current_file is not None:
            bucket = ensure_bucket(current_file)
            continuation_parts = [p.strip() for p in line.split(":", 1)]
            if len(continuation_parts) == 2 and code_pattern.fullmatch(continuation_parts[0]):
                add_unique_message(bucket, "name_checker_messages", continuation_parts[1])
            else:
                add_unique_message(bucket, "other_messages", line)

    return error_map


def build_comment_body(final_report: str) -> str:
    error_map = parse_report_by_file(final_report)
    comment_body = "**Common issues**\n\n"

    if not error_map:
        comment_body += f"{final_report}\n\n{BOT_SIGNATURE}"
        return comment_body

    for filename in sorted(error_map.keys()):
        bucket = error_map[filename]
        codes = bucket.get("lint_icons_codes", set())
        name_messages = bucket.get("name_checker_messages", [])
        other_messages = bucket.get("other_messages", [])

        comment_body += f"{filename}\n"

        if isinstance(codes, set) and codes:
            sorted_codes = sorted(codes, key=lambda x: int(x))
            comment_body += f"{', '.join(sorted_codes)}\n"

        if isinstance(name_messages, list):
            for message in name_messages:
                comment_body += f"{message}\n"

        if isinstance(other_messages, list):
            for message in other_messages:
                comment_body += f"{message}\n"

        comment_body += "\n"

    comment_body += f"{BOT_SIGNATURE}"
    return comment_body


def publish_to_github(final_report: str) -> int:
    if not (REPO_NAME and GITHUB_TOKEN and PR_NUMBER is not None):
        print("Missing GitHub environment variables for GitHub mode.")
        return 2

    from github import Github, Auth

    auth = Auth.Token(GITHUB_TOKEN)
    g = Github(auth=auth)
    repo = g.get_repo(REPO_NAME)
    pr = repo.get_pull(PR_NUMBER)

    bot_comment = find_bot_comment(pr)

    if final_report:
        comment_body = build_comment_body(final_report)

        if bot_comment:
            print("Updating existing comment.")
            bot_comment.edit(comment_body)
        else:
            print("Posting new comment.")
            pr.create_issue_comment(comment_body)

        # Ensure "needs review" label is removed if errors are found.
        if NEEDS_REVIEW_LABEL in [label.name for label in pr.get_labels()]:
            pr.remove_from_labels(NEEDS_REVIEW_LABEL)
    else:
        print("All checks passed.")
        if bot_comment:
            print("Deleting old comment.")
            bot_comment.edit(f"All checks passed.\n\n{BOT_SIGNATURE}")
        # Add "needs review" label if it's not there.
        if NEEDS_REVIEW_LABEL not in [label.name for label in pr.get_labels()]:
            pr.add_to_labels(NEEDS_REVIEW_LABEL)

    return 0


def run_cli_output(final_report: str) -> int:
    if final_report:
        print(build_comment_body(final_report))
        return 1

    print("All checks passed.")
    return 0


# --- Orchestration ---
if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Run icon lint checks and publish to GitHub or print as CLI output."
    )
    parser.add_argument(
        "--mode",
        choices=["auto", "github", "cli"],
        default="auto",
        help="Output mode. 'auto' uses GitHub mode if required env vars are present.",
    )
    parser.add_argument(
        "--base-ref",
        default=None,
        help="Base branch to diff against (default: GITHUB_BASE_REF, origin/HEAD, or main).",
    )
    parser.add_argument(
        "--repo-dir",
        default=None,
        help="Repository root directory to run against (default: script-based auto-detection).",
    )
    args = parser.parse_args()

    configure_repo_paths(args.repo_dir)
    base_ref = resolve_base_ref(args.base_ref)
    final_report = collect_final_report(base_ref)

    mode = args.mode
    if mode == "auto":
        mode = "github" if (REPO_NAME and GITHUB_TOKEN and PR_NUMBER is not None) else "cli"

    if mode == "github":
        sys.exit(publish_to_github(final_report))

    sys.exit(run_cli_output(final_report))

