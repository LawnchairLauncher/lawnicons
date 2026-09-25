#!/usr/bin/env python3
"""Collect weekly review stats and update/create quarterly issue in Lawnicons repo."""
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

now = datetime.now(timezone.utc)
year = now.year
quarter = (now.month - 1) // 3 + 1
quarter_label = f"Q{quarter} {year}"
marker = f"<!-- quarter: {quarter_label} -->"
issue_title = f"{quarter_label} review stats"

quarter_start_month = (quarter - 1) * 3 + 1

total_icons = 0
total_updates = 0
total_link_only = 0

for m in range(quarter_start_month, now.month + 1):
    m_start = f"{year}-{m:02d}-01"
    if m == 12:
        m_end = f"{year+1}-01-01"
    else:
        m_end = f"{year}-{m+1:02d}-01"
    
    # For current month, use today as end date
    if m == now.month:
        m_end = now.strftime("%Y-%m-%d")
    
    cmd = f'gh pr list --repo LawnchairLauncher/lawnicons --state merged --json title,author,mergedAt,baseRefName --limit 1000 --search "base:develop merged:{m_start}..{m_end}"'
    output = run(cmd)
    prs = json.loads(output) if output else []
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
    print(f"Stats for {m_name}: {stats['icons']} icons, {stats['updates']} updates, {stats['link_only']} link-only")
    
    total_icons += stats["icons"]
    total_updates += stats["updates"]
    total_link_only += stats["link_only"]

total_all = total_icons + total_updates + total_link_only

if total_all == 0:
    print("No PRs merged this quarter. Skipping.")
    exit(0)

table_header = "| Quarter | Icons | Updates | Link-only | Total |\n|-------|-------|---------|-----------|-------|"
quarter_row = f"| {quarter_label} | {total_icons} | {total_updates} | {total_link_only} | {total_all} |"

body = f"{marker}\n\n{table_header}\n{quarter_row}"
body = body.replace('"', '\\"')

issues_json = run(f'gh issue list --repo LawnchairLauncher/lawnicons --search "{marker}" --state all --json number,body --limit 1')
issues = json.loads(issues_json) if issues_json else []
issue_number = issues[0]["number"] if issues else None

if issue_number:
    run(f'gh issue edit {issue_number} --repo LawnchairLauncher/lawnicons --body "{body}"')
    print(f"Updated issue #{issue_number}")
else:
    result = run(f'gh issue create --repo LawnchairLauncher/lawnicons --title "{issue_title}" --body "{body}" --label icons')
    print(f"Created issue: {result}")