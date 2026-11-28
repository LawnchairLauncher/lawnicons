#!/usr/bin/env python3
"""Maintain contributor point files: validate events, credit merged PRs, recompute totals."""

import argparse
import json
import os
import re
import sys
import urllib.error
import urllib.request
from dataclasses import dataclass, field
from pathlib import Path

LOGIN_RE = re.compile(r"[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])")
APP_ID_RE = re.compile(
    r"[A-Za-z0-9_]+(?:\.[A-Za-z0-9_]+)+/[A-Za-z0-9_]+(?:\.[A-Za-z0-9_]+)+",
    re.IGNORECASE,
)
NUMBER_RE = re.compile(r"\d+")
AWARD_RE = re.compile(
    r"\+(\d+)\s+points\s+for\s+pr\s+(\d+)\s+with\s+(\d+)\s+(icons?/updates?|icons?|updates?)"
)
PR_TITLE_RE = re.compile(
    r"(?:\+\s*)?(?:(?:added|add|new)\s+)?(\d+)\s+(?:new\s+)?(icons?|updates?|adds?)(?:\s+added)?\b",
    re.IGNORECASE,
)
COMPONENT_RE = re.compile(r'component="ComponentInfo\{([^}]+)\}"')
HEADER_KEYS = ("earned", "spent", "balance")
SECTIONS = ("spends", "fulfilled", "awards")
POINTS_PER_ICON = 5
DEFAULT_REPO = "LawnchairLauncher/lawnicons"
TEMPLATE_COMMENTS = {
    "spends": [
        "# To spend points, add one line: appID weight",
        "# Weight: 1, 2, 3, 4, 5 or a multiple of 5 for higher priority.",
        "# Example: app.lawnchair.lawnicons/app.lawnchair.lawnicons.MainActivity 3",
        "# Balance is recalculated automatically.",
    ],
    "fulfilled": [],
    "awards": [],
}


class FormatError(ValueError):
    def __init__(self, message: str, line: int | None = None):
        super().__init__(f"line {line}: {message}" if line else message)


@dataclass(frozen=True)
class Award:
    pr: int
    points: int
    count: int
    noun: str


@dataclass(frozen=True)
class Spend:
    app_id: str
    weight: int


@dataclass
class ContributorFile:
    path: Path
    login: str
    header: tuple[int, int, int]
    awards: list[Award] = field(default_factory=list)
    spends: list[Spend] = field(default_factory=list)
    fulfilled: list[Spend] = field(default_factory=list)
    comments: dict[str, list[str]] = field(default_factory=dict)


def new_contributor_file(path: Path, login: str) -> ContributorFile:
    return ContributorFile(
        path,
        login,
        (0, 0, 0),
        comments={name: list(lines) for name, lines in TEMPLATE_COMMENTS.items()},
    )


def parse_header(lines: list[str], path: Path) -> tuple[int, int, int]:
    if len(lines) < 9:
        raise FormatError("expected header, awards, spends and fulfilled sections")
    values = {}
    for index, key in enumerate(HEADER_KEYS):
        match = re.fullmatch(rf"{key} (\d+)", lines[index])
        if not match:
            raise FormatError(f"expected '{key} <non-negative integer>'", index + 1)
        values[key] = int(match.group(1))
    if lines[3] != "":
        raise FormatError("expected a blank line after the header", 4)
    return values["balance"], values["earned"], values["spent"]


def split_sections(lines: list[str]) -> tuple[dict[str, list[str]], dict[str, int]]:
    sections = {}
    start_lines = {}
    position = 4
    for index, name in enumerate(SECTIONS):
        if position >= len(lines) or lines[position] != name:
            raise FormatError(f"expected '{name}' section header", position + 1)
        position += 1
        start_lines[name] = position + 1
        section_lines = []
        while position < len(lines) and lines[position] != "":
            section_lines.append(lines[position])
            position += 1
        sections[name] = section_lines
        if index < len(SECTIONS) - 1:
            if position >= len(lines):
                raise FormatError(f"expected a blank line after '{name}'", position + 1)
            position += 1
        elif position != len(lines):
            raise FormatError(f"unexpected lines after '{name}'", position + 1)
    return sections, start_lines


def parse_awards(lines: list[str], start_line: int, comments: list[str]) -> list[Award]:
    awards = []
    seen_prs = set()
    for offset, raw_line in enumerate(lines):
        line_number = start_line + offset
        if raw_line.lstrip().startswith("#"):
            comments.append(raw_line.strip())
            continue
        match = AWARD_RE.fullmatch(raw_line)
        if not match:
            raise FormatError(
                "expected '+<points> points for pr <number> with <count> icon(s)/update(s)'",
                line_number,
            )
        points, pr, count = int(match.group(1)), int(match.group(2)), int(match.group(3))
        word = match.group(4).lower()
        if "/" in word:
            noun = word if count > 1 else "icon/update"
        else:
            singular = word[:-1] if word.endswith("s") else word
            noun = singular if count == 1 else singular + "s"
        if points <= 0 or count <= 0:
            raise FormatError("points and count must be positive", line_number)
        if points != count * POINTS_PER_ICON:
            raise FormatError(f"points must equal {POINTS_PER_ICON} × count", line_number)
        if pr in seen_prs:
            raise FormatError(f"duplicate PR award: #{pr}", line_number)
        seen_prs.add(pr)
        awards.append(Award(pr, points, count, noun))
    return awards


def parse_spends(
    lines: list[str], start_line: int, section: str, seen_app_ids: set[str], comments: list[str]
) -> list[Spend]:
    spends = []
    for offset, raw_line in enumerate(lines):
        line_number = start_line + offset
        if raw_line.lstrip().startswith("#"):
            comments.append(raw_line.strip())
            continue
        parts = raw_line.split()
        if len(parts) != 2:
            raise FormatError("expected '<appID> <weight>'", line_number)
        app_id, raw_weight = parts
        if not APP_ID_RE.fullmatch(app_id):
            raise FormatError(f"invalid app ID: {app_id!r}", line_number)
        if app_id in seen_app_ids:
            raise FormatError(f"duplicate app ID in {section}: {app_id}", line_number)
        seen_app_ids.add(app_id)
        if not NUMBER_RE.fullmatch(raw_weight):
            raise FormatError(f"invalid weight: {raw_weight!r}", line_number)
        weight = int(raw_weight)
        if weight <= 0 or (weight > 5 and weight % 5):
            raise FormatError("weight must be 1-5 or a multiple of 5", line_number)
        spends.append(Spend(app_id, weight))
    return spends


def parse_contributor(path: Path) -> ContributorFile:
    login = path.stem
    if not LOGIN_RE.fullmatch(login):
        raise FormatError(f"invalid GitHub login in file name: {login!r}")

    lines = path.read_text(encoding="utf-8").splitlines()
    while lines and lines[-1] == "":
        lines.pop()
    header = parse_header(lines, path)
    sections, start_lines = split_sections(lines)
    comments = {name: [] for name in SECTIONS}
    seen_app_ids = set()
    spends = parse_spends(
        sections["spends"], start_lines["spends"], "spends", seen_app_ids, comments["spends"]
    )
    fulfilled = parse_spends(
        sections["fulfilled"], start_lines["fulfilled"], "fulfilled", seen_app_ids,
        comments["fulfilled"],
    )
    awards = parse_awards(sections["awards"], start_lines["awards"], comments["awards"])

    return ContributorFile(path, login, header, awards, spends, fulfilled, comments)


def totals(contributor: ContributorFile) -> tuple[int, int, int]:
    earned = sum(award.points for award in contributor.awards)
    spent = sum(spend.weight for spend in contributor.spends + contributor.fulfilled)
    return earned - spent, earned, spent


def render(contributor: ContributorFile, header: tuple[int, int, int]) -> str:
    lines = [f"earned {header[1]}", f"spent {header[2]}", f"balance {header[0]}"]
    entries = {
        "awards": [
            f"+{award.points} points for pr {award.pr} with {award.count} {award.noun}"
            for award in contributor.awards
        ],
        "spends": [f"{spend.app_id} {spend.weight}" for spend in contributor.spends],
        "fulfilled": [f"{spend.app_id} {spend.weight}" for spend in contributor.fulfilled],
    }
    for name in SECTIONS:
        lines += ["", name]
        lines += contributor.comments.get(name, [])
        lines += entries[name]
    return "\n".join(lines) + "\n"


def validate_contributor(contributor: ContributorFile, check_header: bool, errors: list[str]):
    prefix = f"{contributor.path}: "
    balance, earned, spent = totals(contributor)

    for name, value in (("balance", balance), ("earned", earned), ("spent", spent)):
        if value < 0:
            errors.append(f"{prefix}{name} is negative ({value})")
    if check_header and contributor.header != (balance, earned, spent):
        errors.append(
            f"{prefix}header is {contributor.header}, expected {(balance, earned, spent)}"
        )


def write_if_changed(contributor: ContributorFile) -> bool:
    content = render(contributor, totals(contributor))
    if contributor.path.exists() and contributor.path.read_text(encoding="utf-8") == content:
        return False
    contributor.path.write_text(content, encoding="utf-8")
    return True


def signed(value: int) -> str:
    return f"+{value}" if value > 0 else str(value)


def write_summary(
    path: Path,
    contributors: list[ContributorFile],
    before: dict[str, tuple[int, int, int]],
    before_fulfilled: dict[str, int],
    changed_logins: list[str],
) -> None:
    lines = [
        "## Contributor points update",
        "",
        "| Contributor | Credited | Spent | Fulfilled | Balance |",
        "| --- | ---: | ---: | ---: | ---: |",
    ]
    by_login = {contributor.login: contributor for contributor in contributors}
    for login in changed_logins:
        contributor = by_login[login]
        old_balance, old_earned, old_spent = before.get(login, (0, 0, 0))
        balance, earned, spent = totals(contributor)
        moved = len(contributor.fulfilled) - before_fulfilled.get(login, 0)
        lines.append(
            f"| {login} | {signed(earned - old_earned)} | {signed(spent - old_spent)} "
            f"| {signed(moved)} | {balance} ({signed(balance - old_balance)}) |"
        )
    lines += ["", "_Auto-generated by the Contributor Points workflow._"]
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text("\n".join(lines) + "\n", encoding="utf-8")


COUNT_VERBS = ("added", "add", "new")
SKIP_VERBS = (
    "fix", "fixed", "link", "linked", "remove", "removed",
    "delete", "deleted", "revert", "reverted", "replace", "replaced",
)


def parse_pr_title_matches(title: str) -> list[tuple[int, int, str]]:
    results = []
    for match in PR_TITLE_RE.finditer(title):
        prefix = title[: match.start()].rsplit(",", 1)[-1].lower()
        verbs = list(re.finditer(
            r"\b(?:" + "|".join((*COUNT_VERBS, *SKIP_VERBS)) + r")\b", prefix
        ))
        if verbs and verbs[-1].group(0) in SKIP_VERBS:
            continue
        count = int(match.group(1))
        if count <= 0:
            continue
        word = match.group(2).lower()
        singular = "icon" if word.startswith("add") else word[:-1] if word.endswith("s") else word
        noun = singular if count == 1 else singular + "s"
        results.append((count * POINTS_PER_ICON, count, noun))
    return results


def parse_pr_title(title: str) -> tuple[int, int, str] | None:
    matches = parse_pr_title_matches(title)
    if not matches:
        return None
    points = sum(item[0] for item in matches)
    count = sum(item[1] for item in matches)
    singulars = {item[2].rstrip("s") for item in matches}
    if len(singulars) == 1:
        singular = next(iter(singulars))
        noun = singular if count == 1 else singular + "s"
    else:
        noun = "icon/update" if count == 1 else "icons/updates"
    return points, count, noun


def github_get(repo: str, path: str, token: str | None):
    url = f"https://api.github.com/repos/{repo}{path}"
    headers = {
        "Accept": "application/vnd.github+json",
        "User-Agent": "lawnicons-contributor-points",
    }
    if token:
        headers["Authorization"] = f"Bearer {token}"
    request = urllib.request.Request(url, headers=headers)
    try:
        with urllib.request.urlopen(request, timeout=15) as response:
            return json.load(response)
    except urllib.error.HTTPError as error:
        raise ValueError(f"GitHub API returned HTTP {error.code} for {path}") from error
    except (urllib.error.URLError, TimeoutError) as error:
        raise ValueError(f"could not reach GitHub API: {error}") from error


def fetch_pull(repo: str, number: int, token: str | None) -> dict:
    return github_get(repo, f"/pulls/{number}", token)


def fetch_pulls_for_commit(repo: str, sha: str, token: str | None) -> list[dict]:
    if not re.fullmatch(r"[0-9a-f]{7,40}", sha):
        raise ValueError(f"invalid commit SHA: {sha!r}")
    pulls = github_get(repo, f"/commits/{sha}/pulls", token)
    if not isinstance(pulls, list):
        raise ValueError(f"unexpected GitHub API response for commit {sha}")
    return pulls


def merged_pull_for_commit(pulls: list[dict]) -> dict | None:
    merged = [
        pull
        for pull in pulls
        if pull.get("merged_at") and isinstance(pull.get("number"), int)
    ]
    if not merged:
        return None
    return max(merged, key=lambda pull: pull["merged_at"])


def credit_pull(
    contributors: list[ContributorFile],
    all_prs: dict[int, str],
    points_dir: Path,
    pull: dict,
) -> None:
    number = pull["number"]
    award_info = parse_pr_title(pull.get("title") or "")
    if award_info is None:
        print(f"PR #{number}: no counted icons/updates in title, skipping award")
        return
    if number in all_prs:
        print(f"PR #{number}: already credited to {all_prs[number]}, skipping award")
        return

    points, count, noun = award_info
    login = (pull.get("user") or {}).get("login") or ""
    if not LOGIN_RE.fullmatch(login):
        raise ValueError(f"invalid GitHub login in PR response: {login!r}")

    path = points_dir / f"{login}.txt"
    contributor = next((item for item in contributors if item.login == login), None)
    if contributor is None:
        contributor = new_contributor_file(path, login)
        contributors.append(contributor)
    contributor.awards.append(Award(number, points, count, noun))
    all_prs[number] = login


def appfilter_app_ids(appfilter_path: Path) -> set[str]:
    return set(COMPONENT_RE.findall(appfilter_path.read_text(encoding="utf-8")))


def load_contributors(points_dir: Path, errors: list[str]) -> list[ContributorFile]:
    contributors = []
    for path in sorted(points_dir.glob("*.txt")):
        try:
            contributors.append(parse_contributor(path))
        except (FormatError, OSError, UnicodeDecodeError) as error:
            errors.append(f"{path}: {error}")
    return contributors


def main() -> int:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument(
        "--root",
        type=Path,
        default=Path(__file__).resolve().parents[2],
        help="repository root (default: parent of the .github directory)",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="only validate files; do not rewrite or fetch anything",
    )
    source = parser.add_mutually_exclusive_group()
    source.add_argument(
        "--pr",
        type=int,
        help="credit a merged PR from the GitHub API and write its award to the author's file",
    )
    source.add_argument(
        "--commit",
        metavar="SHA",
        help="credit the merged PR that introduced this commit (push trigger)",
    )
    source.add_argument(
        "--mark-fulfilled",
        action="store_true",
        help="move spends whose app ID is now in appfilter.xml to the fulfilled section",
    )
    parser.add_argument("--repo", default=DEFAULT_REPO, help="GitHub repository for --pr/--commit")
    parser.add_argument(
        "--summary-file",
        type=Path,
        help="write a Markdown summary of point changes to this file",
    )
    args = parser.parse_args()

    points_dir = args.root / "contribution-points"
    errors = []
    contributors = load_contributors(points_dir, errors)
    before = {contributor.login: contributor.header for contributor in contributors}
    before_fulfilled = {contributor.login: len(contributor.fulfilled) for contributor in contributors}

    all_prs = {}
    for contributor in contributors:
        for award in contributor.awards:
            if award.pr in all_prs:
                errors.append(f"duplicate PR award #{award.pr} across contributor files")
            all_prs[award.pr] = contributor.login

    if args.check and (args.pr is not None or args.commit or args.mark_fulfilled):
        errors.append("--check cannot be combined with --pr, --commit or --mark-fulfilled")
    if errors:
        for error in errors:
            print(f"error: {error}", file=sys.stderr)
        return 1

    if args.pr is not None or args.commit:
        token = os.environ.get("GITHUB_TOKEN") or os.environ.get("GH_TOKEN")
        try:
            pull = None
            if args.pr is not None:
                pull = fetch_pull(args.repo, args.pr, token)
                if not pull.get("merged"):
                    raise ValueError(f"PR #{args.pr} is not merged")
            else:
                pull = merged_pull_for_commit(fetch_pulls_for_commit(args.repo, args.commit, token))
                if pull is None:
                    print(f"Commit {args.commit}: no merged PR associated, skipping award")
            if pull is not None:
                credit_pull(contributors, all_prs, points_dir, pull)
        except ValueError as error:
            errors.append(str(error))
    elif args.mark_fulfilled:
        try:
            supported = appfilter_app_ids(args.root / "app/assets/appfilter.xml")
        except OSError as error:
            errors.append(str(error))
        else:
            for contributor in contributors:
                moved = [spend for spend in contributor.spends if spend.app_id in supported]
                if moved:
                    contributor.spends = [
                        spend for spend in contributor.spends if spend.app_id not in supported
                    ]
                    contributor.fulfilled.extend(moved)

    for contributor in contributors:
        validate_contributor(contributor, args.check, errors)
    if errors:
        for error in errors:
            print(f"error: {error}", file=sys.stderr)
        return 1

    if args.check:
        print(f"OK: {len(contributors)} contributors, events and headers are consistent")
        return 0

    changed = 0
    changed_logins = []
    for contributor in contributors:
        if write_if_changed(contributor):
            changed += 1
            changed_logins.append(contributor.login)

    if args.summary_file:
        write_summary(args.summary_file, contributors, before, before_fulfilled, changed_logins)

    active = sum(len(contributor.spends) for contributor in contributors)
    fulfilled = sum(len(contributor.fulfilled) for contributor in contributors)
    print(
        f"OK: {len(contributors)} contributors, {active} active and {fulfilled} fulfilled "
        f"spends, {changed} file(s) updated"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
