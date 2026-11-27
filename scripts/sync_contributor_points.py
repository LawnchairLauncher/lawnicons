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
AWARD_RE = re.compile(r"\+(\d+) pr (\d+) (.+)")
PR_TITLE_RE = re.compile(r"\+\s*(\d+)\s+(icons?|updates?)\b", re.IGNORECASE)
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
    title: str


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
            raise FormatError("expected '+<points> pr <number> <title>'", line_number)
        points, pr, title = int(match.group(1)), int(match.group(2)), match.group(3).strip()
        if points <= 0:
            raise FormatError("award must be a positive integer", line_number)
        if pr in seen_prs:
            raise FormatError(f"duplicate PR award: #{pr}", line_number)
        seen_prs.add(pr)
        awards.append(Award(pr, points, title))
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
        "awards": [f"+{award.points} pr {award.pr} {award.title}" for award in contributor.awards],
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


def parse_pr_title(title: str) -> int | None:
    match = PR_TITLE_RE.search(title)
    return int(match.group(1)) * POINTS_PER_ICON if match and int(match.group(1)) > 0 else None


def fetch_pull(repo: str, number: int, token: str | None) -> dict:
    url = f"https://api.github.com/repos/{repo}/pulls/{number}"
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
        raise ValueError(f"GitHub API returned HTTP {error.code} for PR #{number}") from error
    except (urllib.error.URLError, TimeoutError) as error:
        raise ValueError(f"could not reach GitHub API: {error}") from error


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
        default=Path(__file__).resolve().parent.parent,
        help="repository root (default: parent of the scripts directory)",
    )
    parser.add_argument(
        "--check",
        action="store_true",
        help="only validate files; do not rewrite or fetch anything",
    )
    parser.add_argument(
        "--pr",
        type=int,
        help="credit a merged PR from the GitHub API and write its award to the author's file",
    )
    parser.add_argument(
        "--mark-fulfilled",
        action="store_true",
        help="move spends whose app ID is now in appfilter.xml to the fulfilled section",
    )
    parser.add_argument("--repo", default=DEFAULT_REPO, help="GitHub repository for --pr")
    args = parser.parse_args()

    points_dir = args.root / "contributor_points"
    errors = []
    contributors = load_contributors(points_dir, errors)

    all_prs = {}
    for contributor in contributors:
        for award in contributor.awards:
            if award.pr in all_prs:
                errors.append(f"duplicate PR award #{award.pr} across contributor files")
            all_prs[award.pr] = contributor.login

    if args.check and (args.pr or args.mark_fulfilled):
        errors.append("--check cannot be combined with --pr or --mark-fulfilled")
    if errors:
        for error in errors:
            print(f"error: {error}", file=sys.stderr)
        return 1

    if args.pr:
        try:
            pull = fetch_pull(
                args.repo, args.pr, os.environ.get("GITHUB_TOKEN") or os.environ.get("GH_TOKEN")
            )
            if not pull.get("merged"):
                raise ValueError(f"PR #{args.pr} is not merged")
            points = parse_pr_title(pull.get("title") or "")
            if points is None:
                print(f"PR #{args.pr}: no '+X icons/updates' in title, skipping award")
            elif args.pr in all_prs:
                print(f"PR #{args.pr}: already credited to {all_prs[args.pr]}, skipping award")
            else:
                login = (pull.get("user") or {}).get("login") or ""
                if not LOGIN_RE.fullmatch(login):
                    raise ValueError(f"invalid GitHub login in PR response: {login!r}")

                path = points_dir / f"{login}.txt"
                contributor = next((item for item in contributors if item.login == login), None)
                if contributor is None:
                    contributor = new_contributor_file(path, login)
                    contributors.append(contributor)
                contributor.awards.append(
                    Award(args.pr, points, " ".join((pull.get("title") or "").split()))
                )
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
    for contributor in contributors:
        if write_if_changed(contributor):
            changed += 1

    active = sum(len(contributor.spends) for contributor in contributors)
    fulfilled = sum(len(contributor.fulfilled) for contributor in contributors)
    print(
        f"OK: {len(contributors)} contributors, {active} active and {fulfilled} fulfilled "
        f"spends, {changed} file(s) updated"
    )
    return 0


if __name__ == "__main__":
    sys.exit(main())
