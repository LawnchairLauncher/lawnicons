#!/usr/bin/env python3
import subprocess
import json
import re
import os
from collections import defaultdict
from datetime import datetime, timedelta, timezone


def run(cmd: str) -> str:
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    if result.returncode != 0:
        return ""
    return result.stdout.strip()


def get_merged_prs() -> list[dict]:
    # Get latest stable release tag (exclude nightly)
    latest_tag = run("git tag --list 'v*' --sort=-version:refname | grep -v 'nightly' | head -1")
    
    # Get tag date
    if latest_tag:
        tag_date = run(f"git log -1 --format=%aI {latest_tag}")
        date_only = tag_date[:10] if tag_date else None
    else:
        tag_date = None
        date_only = None
    
    # Get all merged PRs since the stable release
    if date_only:
        cmd = f'gh pr list --state merged --json title,number,author,labels,mergedAt,baseRefName --limit 1000 --search "base:develop merged:>={date_only}"'
    else:
        cmd = "gh pr list --state merged --json title,number,author,labels,mergedAt,baseRefName --limit 200"
    
    output = run(cmd)
    
    if not output:
        return []
    
    all_prs = json.loads(output)
    
    # Filter by develop branch
    prs = [pr for pr in all_prs if pr.get("baseRefName") == "develop"]
    
    # Additional filter by exact tag datetime
    if tag_date:
        prs = [pr for pr in prs if pr.get("mergedAt") and pr["mergedAt"] > tag_date]
    else:
        # Fallback to 24 hours
        cutoff = datetime.now(timezone.utc) - timedelta(hours=24)
        prs = [pr for pr in prs if pr.get("mergedAt") and 
               datetime.fromisoformat(pr["mergedAt"].replace("Z", "+00:00")) > cutoff]
    
    return prs


def parse_icon_stats(title: str) -> tuple[int, int, int]:
    icons = sum(int(x) for x in re.findall(r"\+?(\d+)\s*icons?", title, re.IGNORECASE))
    links = sum(int(x) for x in re.findall(r"\+?(\d+)\s*links?", title, re.IGNORECASE))
    updates = sum(int(x) for x in re.findall(r"\+?(\d+)\s*updates?", title, re.IGNORECASE))
    return icons, links, updates



def is_first_timer_from_labels(pr: dict) -> bool:
    labels = [l["name"] for l in pr.get("labels", [])]
    return "first timer" in labels


def get_icon_contributors(prs: list[dict]) -> list[dict]:
    contributors = defaultdict(lambda: {"icons": 0, "links": 0, "updates": 0, "first_time": False})

    for pr in prs:
        title = pr.get("title", "")
        author = pr.get("author", {}).get("login", "unknown")

        if any(word in title.lower() for word in ["icon", "link", "update", "qa"]):
            icons, links, updates = parse_icon_stats(title)

            if icons > 0 or links > 0 or updates > 0:
                contributors[author]["icons"] += icons
                contributors[author]["links"] += links
                contributors[author]["updates"] += updates

                pr_labels = [l["name"] for l in pr.get("labels", [])]
                if "first timer" in pr_labels:
                    contributors[author]["first_time"] = True

    def sort_key(item):
        stats = item[1]
        return -(stats["icons"] + stats["links"] + stats["updates"])

    return [
        {
            "author": author,
            "icons": stats["icons"],
            "links": stats["links"],
            "updates": stats["updates"],
            "first_time": stats["first_time"],
        }
        for author, stats in sorted(contributors.items(), key=sort_key)
    ]


def generate_notes() -> str:
    prs = get_merged_prs()

    if not prs:
        return "No changes in this nightly build."

    total_prs = len(prs)

    icon_prs = [
        p for p in prs
        if any(w in p.get("title", "").lower() for w in ["icon", "link", "update", "qa"])
    ]
    dep_prs = [
        p for p in prs
        if any(w in p.get("title", "").lower() for w in ["dependenc", "update dependency", "bump"])
    ]
    code_prs = [
        p for p in prs
        if "code" in [l["name"] for l in p.get("labels", [])]
    ]

    all_authors = set()
    for pr in prs:
        author = pr.get("author", {}).get("login", "")
        if author:
            all_authors.add(author)

    total_icons = 0
    total_links = 0
    for pr in icon_prs:
        i, l, u = parse_icon_stats(pr.get("title", ""))
        total_icons += i
        total_links += l

    sha = os.getenv("GITHUB_SHA", "unknown")[:7]
    branch = os.getenv("GITHUB_REF_NAME", "develop")
    repo = os.getenv("GH_REPO", "Lawnicons/Lawnicons")
    latest_tag = run("git tag --list 'v*' --sort=-version:refname | grep -v 'nightly' | head -1") or "v2.17.1"

    icon_contributors = get_icon_contributors(icon_prs)

    lines = []
    lines.append(f"Build: `{sha}` \u2022 Branch: `{branch}`\n")
    lines.append("### Summary")
    lines.append(f"- **{total_prs} pull requests** merged")
    lines.append(f"- **{len(code_prs)} code improvements**")
    lines.append(f"- **~{total_icons} icons** and **~{total_links} links** added")
    lines.append(f"- **{len(dep_prs)} dependency updates** applied")
    lines.append(f"- **{len(all_authors)} contributors** participated")

    if icon_contributors:
        lines.append(f"\n### Top icon contributors")
        first_timers_list = []
        for c in icon_contributors:
            parts = []
            if c["icons"] > 0:
                label = "icon" if c["icons"] == 1 else "icons"
                parts.append(f"{c['icons']} {label}")
            if c["links"] > 0:
                label = "link" if c["links"] == 1 else "links"
                parts.append(f"{c['links']} {label}")
            if c["updates"] > 0:
                label = "update" if c["updates"] == 1 else "updates"
                parts.append(f"{c['updates']} {label}")

            if c["first_time"]:
                first_timers_list.append(f"@{c['author']}: {' + '.join(parts)}")
            
            lines.append(f"@{c['author']}: {' + '.join(parts)}")

        if first_timers_list:
            lines.append(f"\n#### First timers")
            lines.extend(first_timers_list)

    if code_prs:
        lines.append(f"\n### Code")
        for pr in code_prs:
            author = pr.get("author", {}).get("login", "unknown")
            title = pr.get("title", "")
            number = pr.get("number", "")
            lines.append(f"- {title} by @{author} in #{number}")            

    lines.append(
        f"\nFull Changelog: [{latest_tag}...nightly](https://github.com/{repo}/compare/{latest_tag}...nightly)"
    )

    return "\n".join(lines)


if __name__ == "__main__":
    print(generate_notes())