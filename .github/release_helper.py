from contextlib import contextmanager
from functools import lru_cache
import json
import os
import re
import git
import datetime
import xml.etree.ElementTree as ET
from typing import Optional


IS_CI = os.getenv("CI") or os.getenv("CI_PIPELINE_SOURCE")


LINK_THRESHOLD = os.getenv("RELEASE_LINK_THRESHOLD") or 20
NEW_THRESHOLD = os.getenv("RELEASE_NEW_THRESHOLD") or 100
DAY_THRESHOLD = os.getenv("RELEASE_DAY_THRESHOLD") or 1


REPOSITORY = os.getenv("REPOSITORY") or "."
SVG_PATH = os.getenv("PATH_TO_SVG") or os.path.join(REPOSITORY, "svgs")
APPFILTER_PATH = os.getenv("PATH_TO_APPFILTER") or os.path.join(REPOSITORY, "app", "assets", "appfilter.xml")


INCREMENT_TYPE = os.getenv("INCREMENT") or "default"


# Get the third most recent tag, which is the last release
# Don't use positive number (e.g., 0, 1, 2, etc) as it will be sorted in reverse
# [-1]: Nightly
# [-2]: v2.12.0 - last release
# [-3]: v2.11.0 - second last release
last_tag = sorted(
    git.Repo(REPOSITORY).tags, 
    key=lambda t: t.commit.committed_datetime
)[-2].name


@lru_cache()
def is_workflow_dispatch() -> bool:
    """
    Check if the event is manually dispatched, support GitHub/GitLab/Forgejo

    GITHUB_EVENT_NAME: GitHub Actions, Forgejo

    CI_PIPELINE_SOURCE: GitLab CI

    Returns:
        bool: True if the event is manually dispatched, False otherwise
    """

    if (
        os.getenv("GITHUB_EVENT_NAME") == "workflow_dispatch"
        or os.getenv("CI_PIPELINE_SOURCE") == "web"
    ):
        print("🔎 Manually triggered workflow detected!")
        return True
    return False


@contextmanager
def git_checkout(repo: git.Repo, ref: str):
    """
    Temporarily check out a specific git reference.

    Allow to check out a specific git reference temporarily and return to the
    original reference after the context manager exits.

    Args:
        repo (git.Repo): The git repository.
        ref (str): The reference to check out.

    Usage:
    ```py
      with git_checkout(repo, "v2.12.0"):
          # Do something
    ```
    """

    original_ref = (
        repo.active_branch.name if repo.head.is_detached else repo.head.ref.name
    )
    repo.git.checkout(ref)
    try:
        yield
    finally:
        repo.git.checkout(original_ref)


def is_greenlight(
    result: tuple, manually_triggered: bool, dry_run: bool, day_threshold=1, link_threshold=20, new_threshold=100
) -> bool:
    """Check if the new icons meet the threshold for release

    Args:
        result (tuple): Tuple of new icons and linked icons
        manually_triggered (bool): True if the event is manually dispatched, False otherwise
        dry_run (bool): Dry run mode (Behaves like manually_triggered but verbose and skip all checks)
        day_threshold (int, optional): Day threshold for the release. Defaults to 1.
        link_threshold (int, optional): Link threshold for the release. Defaults to 20.
        new_threshold (int, optional): New threshold for the release. Defaults to 100.

    Returns:
        bool: True if the new icons is eligible for release, False otherwise, will skip all checks if manually triggered.
    """

    if manually_triggered:
        print("🟢 Manually triggered workflow, skipped all check, greenlighting!")
        return True

    today_day = datetime.datetime.now().day
    if today_day != day_threshold:
        if dry_run:
            print(
                f"🟢 Dry run mode enabled, skipping the day check. Today is {today_day}, which isn't the target release day {day_threshold}."
            )
        else:
            print(
                f"🔴 Today is {today_day}, which isn't the target release day {day_threshold}."
            )
            return False

    if len(result[0]) < new_threshold:
        if dry_run:
            print(
                f"🟢 Dry run mode enabled, skipping the new icons check. Only {len(result[0])} new icons found since the last release, below the threshold of {new_threshold}."
            )
        else:
            print(
                f"🔴 Only {len(result[0])} new icons found since the last release, below the threshold of {new_threshold}."
            )
            return False
    if len(result[1]) < link_threshold:
        if dry_run:
            print(
                f"🟢 Dry run mode enabled, skipping the linked icons check. Only {len(result[1])} icons linked to a new component found since the last release, below the threshold of {link_threshold}."
            )
        else:
            print(
                f"🔴 Only {len(result[1])} icons linked to a new component found since the last release, below the threshold of {link_threshold}."
            )
            return False

    print("🟢 Greenlight!")
    return True


def next_release_predictor(result: tuple, last_version: str, increment_type: str = "default") -> str:
    """
    Predict the next release version by incrementing the MAJOR, MINOR, or 
    PATCH component based on Semantic Versioning 2.0.0.

    **NOTE**: Doesn't support predicting the MAJOR component.

    If the number of new icons is more than the threshold, 
    it will increment the MINOR component otherwise PATCH component.

    Args:
        result (tuple): Tuple of new icons and linked icons
        last_version (str): Current version of the current.
        increment_type (str, optional): Component to increments.

    Raises:
        ValueError: If increment type is incorrect

    Returns:
        str: Next version
    """
    # Additional Note:
    # every MAJOR will increment the MAJOR and reset MINOR and PATCH component,
    # every MINOR will increment the MINOR and reset PATCH component,
    # every PATCH will only increment the PATCH component.

    increment_type = increment_type.lower()
    match = re.match(r"v(\d+)\.(\d+)\.(\d+)", last_version)
    if not match:
        raise ValueError(f"Invalid version format: {last_version}")

    major, minor, patch = map(int, match.groups())

    if len(result[0]) < NEW_THRESHOLD or len(result[1]) < LINK_THRESHOLD:
        increment_type = "patch"
    else:
        increment_type = "minor"

    if increment_type == "major":
        major += 1
        minor = 0
        patch = 0
    elif increment_type == "minor":
        minor += 1
        patch = 0
    elif increment_type == "patch":
        patch += 1
    else:
        raise ValueError(
            f"Invalid increment type: {increment_type}. Choose 'major', 'minor', or 'patch'."
        )

    return f"v{major}.{minor}.{patch}"


def release_generation(
    markdownfile: str,
    version: str,
    future_version: str,
    total_icons: int,
    total_links: int,
    additions: int,
    linked: Optional[int] = None,
    dry_run: Optional[bool] = False, # Set this to true to prevent writing to the file
):
    """
    Generate the release note and return the version number.

    Args:
        markdownfile (str): Path to the markdown file.
        version (str): Current version of the current.
        future_version (str): Next version of the release.
        total_icons (int): Total icons in the repository.
        total_links (int): Total links in the repository.
        additions (int): Number of new icons.
        linked (int, optional): Number of linked icons. Defaults to None.
        dry_run (bool, optional): Dry run mode. Defaults to True.

    Returns:
        Markdown (str): Changelog in markdown format.
    """
    # Format the release note, release note might change, please check
    template = f"""# Lawnicons {future_version}
Lawnicons {future_version} is here! Compared to the previous one, this release includes:
"""

    if additions > 50:
        additions_rounded = round(additions / 50) * 50
        template += (
            f"* {additions_rounded} unique icons ({total_icons} icons in total)\n"
        )
    elif additions > 0:
        template += f"* {additions} unique icons ({total_icons} icons in total)\n"

    if linked > 50:
        linked_rounded = round(linked / 50) * 50
        template += f"* {linked_rounded} new links ({total_links} links in total)\n"
    elif linked > 0:
        template += f"* {linked} new links ({total_links} links in total)\n"

    if additions == 0 and linked == 0:
        template += "Nothing much was added for now.\n"

    template += f"Full changelog: https://github.com/LawnchairLauncher/lawnicons/compare/{version}...{future_version}\n"

    print(template)

    if not dry_run:
        with open(markdownfile, "a") as file:
            file.write(template)

    return template


def new_icon_since() -> tuple:
    cmd = "gradlew :svg-processor:run"
    os.system(cmd)

    appfilter_tree = ET.parse("app/src/runtime/res/xml/appfilter.xml")
    appfilter_root = appfilter_tree.getroot()

    appfilter_diff_tree = ET.parse("app/src/runtime/res/xml/appfilter_diff.xml")
    appfilter_diff_root = appfilter_diff_tree.getroot()

    existing_drawables = set()
    all_links = []

    for item in appfilter_root.findall("item"):
        existing_drawables.add(item.get("drawable"))
        all_links.append({"component": item.get("component"), "drawable": item.get("drawable"), "name": item.get("name")})

    new_icons = []
    linked_icons = []

    for item in appfilter_diff_root.findall("item"):
        component = item.get("component")
        drawable = item.get("drawable")
        name = item.get("name")

        if drawable in existing_drawables:
            linked_icons.append(
                {"component": component, "drawable": drawable, "name": name}
            )
        else:
            new_icons.append(
                {"component": component, "drawable": drawable, "name": name}
            )
            existing_drawables.add(drawable)

        all_links.append({"component": component, "drawable": drawable, "name": name})

    total_icons = len(existing_drawables)
    total_links = len(all_links)

    print(f"📊 Total new icons: {len(new_icons)}")
    print(f"📊 Total linked icons: {len(linked_icons)}")
    print(f"📊 Total icons: {total_icons}")
    print(f"📊 Total links: {total_links}")

    if IS_CI:
        print(f"::set-output name=new_icons::{json.dumps(new_icons)}")
        print(f"::set-output name=linked_icons::{json.dumps(linked_icons)}")
        print(f"::set-output name=total_icons::{total_icons}")
        print(f"::set-output name=total_links::{total_links}")

    return new_icons, linked_icons, total_icons, total_links


result = new_icon_since()


print(f"🎉 There have been {len(result[0])} new icons since release!")
print(f"🔗 {len(result[1])} icons have been linked to a new component since release!")

greenlight = is_greenlight(
    result,
    is_workflow_dispatch(),
    True,
    DAY_THRESHOLD,
    LINK_THRESHOLD,
    NEW_THRESHOLD,
)
print(
    f"🚦 {'Not eligible for release!' if not greenlight else 'Eligible for release! Greenlight away!'}"
)


next_version = next_release_predictor(result, last_tag, INCREMENT_TYPE)
print(f"{next_version}")
print(f"{str(greenlight).lower()}")

if IS_CI:
    print(f"::set-output name=next_version::{next_version}")
    print(f"::set-output name=greenlight::{str(greenlight).lower()}")
release_generation(
    "CHANGELOG.md",
    last_tag,
    next_version,
    result[2],
    result[3],
    len(result[0]),
    len(result[1]),
    True,
)


exit(1 if not greenlight else 0)
