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
year = now.year
quarter = (now.month - 1) // 3 + 1
quarter_label = f"Q{quarter} {year}"
marker = f"<!-- quarter: {quarter_label} -->"
issue_title = f"{quarter_label} review stats"

# Collect stats for all completed months in the quarter
quarter_start_month = (quarter - 1) * 3 + 1
all_month_rows = []

for m in range(quarter_start_month, now.month):
    m_start = f"{year}-{m:02d}-01"
    if m == 12:
        m_end = f"{year+1}-01-01"
    else:
        m_end = f"{year}-{m+1:02d}-01"
    
    cmd = f'gh pr list --repo LawnchairLauncher/lawnicons --state merged --json title,author,mergedAt,baseRefName --limit 1000 --search "base:develop merged:{m_start}..{m_end}"'
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
    
    m_name = datetime(year, m, 1).strftime("%B")
    m_total = stats["icons"] + stats["updates"] + stats["link_only"]
    all_month_rows.append((m_name, stats, m_total))
    print(f"Stats for {m_name}: {stats['icons']} icons, {stats['updates']} updates, {stats['link_only']} link-only")

if not all_month_rows:
    print("No completed months in this quarter yet. Skipping.")
    exit(0)

# Build table
table_header = "| Month | Icons | Updates | Link-only | Total |\n|-------|-------|---------|-----------|-------|"
table_rows = "\n".join(f"| {name} | {s['icons']} | {s['updates']} | {s['link_only']} | {total} |" for name, s, total in all_month_rows)

is_quarter_end = now.month == quarter_start_month + 2  # March, June, September, December

if is_quarter_end:
    total_icons = sum(s["icons"] for _, s, _ in all_month_rows)
    total_updates = sum(s["updates"] for _, s, _ in all_month_rows)
    total_link_only = sum(s["link_only"] for _, s, _ in all_month_rows)
    total_all = total_icons + total_updates + total_link_only
    quarter_row = f"| {quarter_label} | {total_icons} | {total_updates} | {total_link_only} | {total_all} |"

body = f"{marker}\n\n{table_header}\n{table_rows}"
if is_quarter_end:
    body += f"\n{quarter_row}"

body = body.replace('"', '\\"')

# Find or create issue
issues_json = run(f'gh issue list --repo LawnchairLauncher/lawnicons --search "{marker}" --state open --json number,body --limit 1')
issues = json.loads(issues_json) if issues_json else []
issue_number = issues[0]["number"] if issues else None

if issue_number:
    run(f'gh issue edit {issue_number} --repo LawnchairLauncher/lawnicons --body "{body}"')
    print(f"Updated issue #{issue_number}")
else:
    result = run(f'gh issue create --repo LawnchairLauncher/lawnicons --title "{issue_title}" --body "{body}" --label icons')
    print(f"Created issue: {result}")