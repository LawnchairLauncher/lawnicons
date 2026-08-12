import argparse
import json
import os
import re
import subprocess
import sys
from pathlib import Path

# --- Configuration ---
REPO_NAME = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER_RAW = os.getenv("PR_NUMBER")
PR_NUMBER = int(PR_NUMBER_RAW) if PR_NUMBER_RAW else None
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")
BASE_REF = os.getenv("BASE_REF")

# Assuming a standard project structure
SCRIPT_REPO_ROOT = Path(__file__).parent.parent.parent
DATA_REPO_ROOT = SCRIPT_REPO_ROOT

# We separate data repo root to prevent PWN attacks in the CI
APPFILTER_PATH = DATA_REPO_ROOT / "app/assets/appfilter.xml"
DRAWABLES_DIR = DATA_REPO_ROOT / "svgs/"
SVG_LINTER_PATH = SCRIPT_REPO_ROOT / "lint_icons.py"
NAME_CHECKER_PATH = SCRIPT_REPO_ROOT / ".github/scripts/name_checker.py"

BOT_SIGNATURE = "<!-- Linter bot report -->"
NEEDS_REVIEW_LABEL = "needs review"

SPEC_MESSAGE = """> [!TIP]
> **Spec**
> canvas: 192×192 px, color: #000000, opacity: 100%, shadow or effect: none, stroke: 12 px (core), fill: none, size: max 3 KB"""

# --- Main Logic ---


def configure_paths(data_repo_dir: str | None, script_repo_dir: str | None) -> None:
    """Override repository-relative paths for local testing runs."""
    global SCRIPT_REPO_ROOT, DATA_REPO_ROOT, APPFILTER_PATH, DRAWABLES_DIR, SVG_LINTER_PATH, NAME_CHECKER_PATH

    if script_repo_dir:
        SCRIPT_REPO_ROOT = Path(script_repo_dir).expanduser().resolve()
        SVG_LINTER_PATH = SCRIPT_REPO_ROOT / "lint_icons.py"
        NAME_CHECKER_PATH = SCRIPT_REPO_ROOT / ".github/scripts/name_checker.py"

    if data_repo_dir:
        DATA_REPO_ROOT = Path(data_repo_dir).expanduser().resolve()
    elif script_repo_dir:
        DATA_REPO_ROOT = SCRIPT_REPO_ROOT

    APPFILTER_PATH = DATA_REPO_ROOT / "app/assets/appfilter.xml"
    DRAWABLES_DIR = DATA_REPO_ROOT / "svgs/"


def get_changed_svgs(base_ref: str) -> list[str]:
    """
    Finds SVG files changed in this PR compared to the target branch.
    We use triple-dot here and in `get_changed_drawables` to get only the changes
    introduced by the PR branch relative to the common ancestor.
    """
    drawables_pathspec = DRAWABLES_DIR.relative_to(DATA_REPO_ROOT).as_posix()
    cmd = ["git", "diff", "--name-only",
           f"origin/{base_ref}...HEAD", "--", drawables_pathspec]
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=DATA_REPO_ROOT,
    )
    if result.returncode != 0:
        return []
    changed_files = result.stdout.strip().splitlines()
    return [
        str(DRAWABLES_DIR / Path(f).name)
        for f in changed_files if f.endswith(".svg") and (DRAWABLES_DIR / Path(f).name).exists()
    ]

def get_changed_drawables(base_ref: str) -> list[str]:
    """Extracts drawable names from added/modified lines in appfilter.xml diff."""
    cmd = ["git", "diff", f"origin/{base_ref}...HEAD", "--",
           str(APPFILTER_PATH.as_posix())]
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=DATA_REPO_ROOT,
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

    if BASE_REF:
        return normalize_ref(BASE_REF)

    # Fallback for local CLI runs where BASE_REF is not set.
    result = subprocess.run(
        ["git", "symbolic-ref", "refs/remotes/origin/HEAD"],
        capture_output=True,
        text=True,
        encoding="utf-8",
        errors="replace",
        check=False,
        cwd=DATA_REPO_ROOT,
    )
    if result.returncode == 0:
        ref = result.stdout.strip()
        if "/" in ref:
            return ref.rsplit("/", 1)[-1]

    return "develop"


def collect_final_report(base_ref: str) -> dict[str, list[dict[str, str]]]:
    changed_svg_files = get_changed_svgs(base_ref)
    final_file_messages: dict[str, list[dict[str, str]]] = {}

    # 1. Run SVG Linter
    if changed_svg_files:
        print(f"Checking {len(changed_svg_files)} changed SVG files...")
        svg_base_args = ["--format", "json"]
        for svg_chunk in chunk_for_command(changed_svg_files, svg_base_args):
            raw_json = run_linter(SVG_LINTER_PATH, svg_base_args + svg_chunk)
            if not raw_json:
                continue
            try:
                reports = json.loads(raw_json)
                for report in reports:
                    filename = Path(report["file_path"]).name
                    for res in report["results"]:
                        if res.get("status") == "FAIL" and res.get("message"):
                            msg_obj = {
                                "message": res["message"],
                                "category": res.get("category", "Core")
                            }
                            final_file_messages.setdefault(filename, []).append(msg_obj)
            except Exception as e:
                print(f"Error parsing SVG linter JSON: {e}")
                print(f"Raw JSON was: {raw_json[:100]}...")

    # 2. Run Name Checker
    print("Checking appfilter.xml consistency...")
    changed_drawables = get_changed_drawables(base_ref)
    name_checker_args = [
        "--appfilter", str(APPFILTER_PATH),
        "--drawables-dir", str(DRAWABLES_DIR),
        "--format", "json",
    ]
    if changed_drawables:
        drawable_flag_args = name_checker_args + ["--changed-drawables"]
        for drawable_chunk in chunk_for_command(changed_drawables, drawable_flag_args):
            raw_json = run_linter(NAME_CHECKER_PATH, drawable_flag_args + drawable_chunk, accepted_exit_codes={0, 1})
            if not raw_json:
                continue
            try:
                report = json.loads(raw_json)
                for res in report.get("results", []):
                    if res.get("status") == "FAIL":
                        filename = res.get("target", "unknown.svg")
                        msg = res.get("message")
                        if msg:
                            msg_obj = {
                                "message": msg,
                                "category": res.get("category", "Naming")
                            }
                            final_file_messages.setdefault(filename, []).append(msg_obj)
            except Exception as e:
                print(f"Error parsing name checker JSON: {e}")

    return final_file_messages


def build_comment_body(file_messages: dict[str, list[dict[str, str]]], is_first_review: bool) -> str:
    if not file_messages:
        lines = []
        if is_first_review:
            lines.append("Thanks for your contribution!\n")
            lines.append("Please fix all common issues and ensure Lawnicons builds correctly.\n")
        
        lines.append("### Common issues\n")
        lines.append("![](https://raw.githubusercontent.com/LawnchairLauncher/lawnicons/refs/heads/develop/docs/images/common-issues-to-fix.png)\n")
        
        lines.append(BOT_SIGNATURE)
        return "\n".join(lines)

    lines = []
    if is_first_review:
        lines.append("Thanks for your contribution!\n")
        lines.append("Please fix all bot-detected issues to get a human review — and the rest to merge. Ensure Lawnicons builds correctly.\n")

    lines.append("### Bot-detected issues")
    lines.append(SPEC_MESSAGE + "\n")

    for filename in sorted(file_messages.keys()):
        all_msgs = file_messages[filename]

        # De-duplicate messages
        seen_msgs = set()
        unique_msgs = []
        for m in all_msgs:
            msg_key = (m["message"], m["category"])
            if msg_key not in seen_msgs:
                unique_msgs.append(m)
                seen_msgs.add(msg_key)

        general_msgs = [m["message"] for m in unique_msgs if m["category"] != "Naming"]
        naming_msgs = [m["message"] for m in unique_msgs if m["category"] == "Naming"]

        lines.append(f"**{filename}**")
        if general_msgs:
            lines.append(", ".join(general_msgs))

        for n_msg in naming_msgs:
            lines.append(n_msg)

        lines.append("") # Empty line between files

    lines.append("### Common issues\n")
    lines.append("![](https://raw.githubusercontent.com/LawnchairLauncher/lawnicons/refs/heads/develop/docs/images/common-issues-to-fix.png)\n")

    lines.append(BOT_SIGNATURE)
    return "\n".join(lines)


def publish_to_github(file_messages: dict[str, list[dict[str, str]]]) -> int:
    if not (REPO_NAME and GITHUB_TOKEN and PR_NUMBER is not None):
        print("Missing GitHub environment variables for GitHub mode.")
        return 2

    from github import Github, Auth

    auth = Auth.Token(GITHUB_TOKEN)
    g = Github(auth=auth)
    repo = g.get_repo(REPO_NAME)
    pr = repo.get_pull(PR_NUMBER)

    bot_comment = find_bot_comment(pr)
    comment_body = build_comment_body(file_messages, is_first_review=(bot_comment is None))

    if file_messages:
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
            print("Updating old comment to success.")
            bot_comment.edit(comment_body)
        else:
            print("Posting success comment.")
            pr.create_issue_comment(comment_body)
        # Add "needs review" label if it's not there.
        if NEEDS_REVIEW_LABEL not in [label.name for label in pr.get_labels()]:
            pr.add_to_labels(NEEDS_REVIEW_LABEL)

    return 0


def run_cli_output(file_messages: dict[str, list[dict[str, str]]]) -> int:
    print(build_comment_body(file_messages, is_first_review=True))
    return 1 if file_messages else 0


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
        "--data-repo-dir", "--repo-dir",
        dest="data_repo_dir",
        default=None,
        help="Data repository root directory (default: same as script repo).",
    )
    parser.add_argument(
        "--script-repo-dir",
        default=None,
        help="Script repository root directory (default: auto-detected from __file__).",
    )
    parser.add_argument(
        "--base-ref",
        default=None,
        help="Base branch to diff against (default: BASE_REF, origin/HEAD, or develop).",
    )
    args = parser.parse_args()

    configure_paths(args.data_repo_dir, args.script_repo_dir)
    base_ref = resolve_base_ref(args.base_ref)
    file_messages = collect_final_report(base_ref)

    mode = args.mode
    if mode == "auto":
        mode = "github" if (REPO_NAME and GITHUB_TOKEN and PR_NUMBER is not None) else "cli"

    if mode == "github":
        sys.exit(publish_to_github(file_messages))

    sys.exit(run_cli_output(file_messages))

