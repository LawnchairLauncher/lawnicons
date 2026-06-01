#!/usr/bin/env python3
"""Collect monthly review stats and update/create quarterly issue in Lawnicons repo."""
import subprocess, json, re, os
from datetime import datetime, timezone, timedelta

def run(cmd):
    result = subprocess.run(cmd, shell=True, capture_output=True, text=True)
    return result.stdout.strip() if result.returncode == 0 else ""

def parse_icon_stats(title):
    icons = sum(int(x) for x in re.findall(r"\+?(\d+)\s*icons?", title, re.IGNORECASE))
    links = sum(int(x) for x in re.findall(r"\+?(\d+)\s*links?", title, re.IGNORECASE))
    updates = sum(int(x) for x in re.findall(r"\+?(\d+)\s*updates?", title, re.IGNORECASE))
    return icons, links, updates

def is_last_day_of_month():
    tomorrow = datetime.now(timezone.utc).replace(day=1).replace(month=datetime.now(timezone.utc).month % 12 + 1)
    if datetime.now(timezone.utc).month == 12:
        tomorrow = tomorrow.replace(year=datetime.now(timezone.utc).year + 1, month=1)
    last_day = (tomorrow - timedelta(days=1)).day
    return datetime.now(timezone.utc).day == last_day

# Only run on last day of month (unless dispatch)
if not os.getenv("GITHUB_EVENT_NAME") == "workflow_dispatch" and not is_last_day_of_month():
    print("Not the last day of month. Skipping.")
    exit(0)

now = datetime.now(timezone.utc)
if now.month == 1:
    year, month = now.year - 1, 12
else:
    year, month = now.year, now.month - 1

start = f"{year}-{month:02d}-01"
if month == 12:
    end = f"{year+1}-01-01"
else:
    end = f"{year}-{month+1:02d}-01"

cmd = f'gh pr list --repo LawnchairLauncher/lawnicons --state merged --json title,author,mergedAt,baseRefName --limit 1000 --search "base:develop merged:{start}..{end}"'
prs = json.loads(run(cmd)) if run(cmd) else []
prs = [p for p in prs if p.get("baseRefName") == "develop"]

stats = {"icons": 0, "updates": 0, "link_only": 0}
for pr in prs:
    author = pr.get("author", {}).get("login", "unknown")
    if author == "x9136":
        continue
    title = pr.get("title", "")
    if any(w in title.lower() for w in ["icon", "link", "update"]):
        i, l, u = parse_icon_stats(title)
        if i > 0 or u > 0:
            stats["icons"] += i
            stats["updates"] += u
        elif l > 0:
            stats["link_only"] += 1

quarter = (month - 1) // 3 + 1
quarter_label = f"Q{quarter} {year}"
marker = f"<!-- quarter: {quarter_label} -->"
issue_title = f"{quarter_label} review stats"
month_name = datetime(year, month, 1).strftime("%B")
month_total = stats["icons"] + stats["updates"] + stats["link_only"]

table_header = "| Month | Icons | Updates | Link-only | Total |\n|-------|-------|---------|-----------|-------|"
month_row = f"| {month_name} | {stats['icons']} | {stats['updates']} | {stats['link_only']} | {month_total} |"

# Find or create issue
issues_json = run(f'gh issue list --repo LawnchairLauncher/lawnicons --search "{marker}" --state open --json number,body --limit 1')
issues = json.loads(issues_json) if issues_json else []
issue_number = issues[0]["number"] if issues else None
is_quarter_end = (quarter == 1 and month == 3) or (quarter == 2 and month == 6) or \
                 (quarter == 3 and month == 9) or (quarter == 4 and month == 12)

if issue_number:
    body = issues[0]["body"]
    if f"| {month_name} |" in body:
        lines = body.split("\n")
        new_lines = []
        for line in lines:
            if line.startswith(f"| {month_name} |"):
                new_lines.append(month_row)
            else:
                new_lines.append(line)
        body = "\n".join(new_lines)
    else:
        if f"| {quarter_label} |" in body:
            body = body.replace(f"| {quarter_label} |", f"{month_row}\n| {quarter_label} |")
        else:
            body = body.rstrip() + f"\n{month_row}"
    
    if is_quarter_end:
        total_icons = 0
        total_updates = 0
        total_link_only = 0
        for line in body.split("\n"):
            if line.startswith("|") and not line.startswith("| Month") and not line.startswith("| Q"):
                parts = [p.strip() for p in line.split("|") if p.strip()]
                if len(parts) >= 4:
                    total_icons += int(parts[1])
                    total_updates += int(parts[2])
                    total_link_only += int(parts[3])
        
        total_all = total_icons + total_updates + total_link_only
        quarter_row = f"| {quarter_label} | {total_icons} | {total_updates} | {total_link_only} | {total_all} |"
        
        lines = [l for l in body.split("\n") if not l.startswith(f"| {quarter_label} |")]
        body = "\n".join(lines).rstrip() + f"\n{quarter_row}"
    
    body = body.replace('"', '\\"')
    run(f'gh issue edit {issue_number} --repo LawnchairLauncher/lawnicons --body "{body}"')
    print(f"Updated issue #{issue_number}")
else:
    body = f"{marker}\n\n## {issue_title}\n\n{table_header}\n{month_row}"
    body = body.replace('"', '\\"')
    result = run(f'gh issue create --repo LawnchairLauncher/lawnicons --title "{issue_title}" --body "{body}" --label icons')
    print(f"Created issue: {result}")

print(f"Stats for {month_name}: {stats['icons']} icons, {stats['updates']} updates, {stats['link_only']} link-only")