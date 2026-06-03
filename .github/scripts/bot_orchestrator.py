import os
import subprocess
import sys
from pathlib import Path
from github import Github

# --- Configuration ---
REPO_NAME = os.getenv("GITHUB_REPOSITORY")
PR_NUMBER = int(os.getenv("PR_NUMBER"))  # type: ignore
GITHUB_TOKEN = os.getenv("GITHUB_TOKEN")

# Assuming a standard project structure
REPO_ROOT = Path(__file__).parent.parent.parent
APPFILTER_PATH = REPO_ROOT / "app/assets/appfilter.xml"
DRAWABLES_DIR = REPO_ROOT / "svgs/"
SVG_LINTER_PATH = REPO_ROOT / "lint-icons.py"
NAME_CHECKER_PATH = REPO_ROOT / ".github/scripts/name_checker.py"

BOT_SIGNATURE = "--- \n*Linter Bot Report*"
NEEDS_REVIEW_LABEL = "needs review"

# --- Main Logic ---


def get_changed_svgs() -> list[str]:
    """Finds SVG files changed in this PR compared to the target branch."""
    target_branch = os.getenv("GITHUB_BASE_REF")
    drawables_pathspec = DRAWABLES_DIR.relative_to(REPO_ROOT).as_posix()
    cmd = ["git", "diff", "--name-only",
           f"origin/{target_branch}", "HEAD", "--", drawables_pathspec]
    result = subprocess.run(
        cmd,
        capture_output=True,
        text=True,
        check=False,
        cwd=REPO_ROOT,
    )
    if result.returncode != 0:
        return []


def run_linter(script_path: Path, args: list[str]) -> str:
    """Runs a linter script and returns its stdout."""
    try:
        result = subprocess.run(
            [sys.executable, str(script_path)] + args,
            # check=False to capture output even on failure
            capture_output=True, text=True, check=False
        )

        if result.returncode != 0:
            error_output = stderr or stdout or "No output captured."
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


# --- Orchestration ---
if __name__ == "__main__":
    changed_svg_files = get_changed_svgs()
    all_errors = []

    # 1. Run SVG Linter on changed files only
    if changed_svg_files:
        print(f"Checking {len(changed_svg_files)} changed SVG files...")
        svg_linter_args = ["--format", "compact"] + changed_svg_files
        svg_errors = run_linter(SVG_LINTER_PATH, svg_linter_args)
        if svg_errors:
            all_errors.append(svg_errors)

    # 2. Run Name Checker
    print("Checking appfilter.xml consistency...")
    name_errors = run_linter(
        NAME_CHECKER_PATH, [str(APPFILTER_PATH), str(DRAWABLES_DIR)])
    if name_errors:
        all_errors.append(name_errors)

    # 3. Connect to GitHub
    g = Github(GITHUB_TOKEN)
    repo = g.get_repo(REPO_NAME)  # type: ignore
    pr = repo.get_pull(PR_NUMBER)

    final_report = "\n".join(all_errors).strip()

    bot_comment = find_bot_comment(pr)

    # 4. Post or Update Comment
    if final_report:
        # Group errors by file for cleaner output
        error_map = {}
        for line in final_report.splitlines():
            if ':' not in line:
                continue
            filename, error_msg = line.split(':', 1)
            if filename not in error_map:
                error_map[filename] = []
            error_map[filename].append(error_msg.strip())

        # Build Markdown comment
        comment_body = "### :warning: Linter found issues\n\n"
        for filename, issues in sorted(error_map.items()):
            comment_body += f"- **`{filename}`**\n"
            for issue in issues:
                comment_body += f"  - `{issue}`\n"
        comment_body += f"\n{BOT_SIGNATURE}"

        if bot_comment:
            print("Updating existing comment.")
            bot_comment.edit(comment_body)
        else:
            print("Posting new comment.")
            pr.create_issue_comment(comment_body)

        # Ensure "needs review" label is removed if errors are found
        if NEEDS_REVIEW_LABEL in [label.name for label in pr.get_labels()]:
            pr.remove_from_labels(NEEDS_REVIEW_LABEL)

    else:
        # No errors found
        print("All checks passed.")
        if bot_comment:
            print("Deleting old comment.")
            bot_comment.edit("All checks passed.")

        # Add "needs review" label if it's not there
        if NEEDS_REVIEW_LABEL not in [label.name for label in pr.get_labels()]:
            pr.add_to_labels(NEEDS_REVIEW_LABEL)
