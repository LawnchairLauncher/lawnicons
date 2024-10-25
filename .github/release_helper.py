from contextlib import contextmanager
import os
import re
import git
import datetime


NEW_THRESHOLD = 100
DAY_THRESHOLD = 1


REPOSITORY = "."
INCREMENT_TYPE = os.getenv("INCREMENT") or "default"


# Get the third most recent tag, which is the last release
# Don't use positive number (e.g., 0, 1, 2, etc) as it will be sorted in reverse
# [-1]: Nightly
# [-2]: v2.12.0 - last release
# [-3]: v2.11.0 - second last release
last_release = sorted(
    git.Repo(REPOSITORY).tags, 
    key=lambda t: t.commit.committed_datetime
)[-2].name


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


def get_new_icon_since(last_version: str) -> list:
    """
    Get the new icons since the last release.

    Args:
        last_version (str): The last release version.

    Returns:
        list: List of new icons.
    """

    icons_dir = "svgs"

    current_icons = set(os.listdir(icons_dir))

    print(f"Checking out version {last_version}")
    with git_checkout(git.Repo(REPOSITORY), last_version):
        previous_icons = set(os.listdir(icons_dir))

    return list(current_icons - previous_icons)


def is_greenlight(
    result: list, manually_triggered: bool, day_threshold=1, new_threshold=100
) -> bool:
    """Check if the new icons meet the threshold for release

    Args:
        result (list): List of new icons
        manually_triggered (bool): Check if the workflow is manually dispatched
        day_threshold (int, optional): Number of days to check. Defaults to 1.
        new_threshold (int, optional): Number of new icons to check. Defaults to 100.

    Returns:
        bool: True if the new icons is eligible for release, False otherwise, will skip all checks if manually triggered.
    """

    if manually_triggered:
        print("🟢 Manually triggered workflow, skipped all check, greenlighting!")
        return True

    today_day = datetime.datetime.now().day
    if today_day != day_threshold:
        print(
            f"🔴 Today is {today_day}, which isn't the target release day {day_threshold}."
        )
        return False

    if len(result) < new_threshold:
        print(
            f"🔴 Only {len(result)} new icons found since the last release, below the threshold of {new_threshold}."
        )
        return False

    print("🟢 Greenlight!")
    return True


def next_release_predictor(last_version: str, increment_type: str = "default") -> str:
    """
    Predict the next release version by incrementing the MAJOR, MINOR, or 
    PATCH component based on Semantic Versioning 2.0.0.

    **NOTE**: Doesn't support predicting the MAJOR component.

    If the number of new icons is more than the threshold, 
    it will increment the MINOR component otherwise PATCH component.

    Args:
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

    if get_new_icon_since(last_version) < NEW_THRESHOLD:
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


result = get_new_icon_since(last_release)
print(f"🎉 There have been {len(result)} new icons since release!")

greenlight = is_greenlight(result, is_workflow_dispatch(), DAY_THRESHOLD, NEW_THRESHOLD)
print(
    f"🚦 {'Not eligible for release!' if not greenlight else 'Eligible for release! Greenlight away!'}"
)


next_version = next_release_predictor(last_release, INCREMENT_TYPE)
print(f"::set-output name=next_version::{next_version}")
print(f"::set-output name=greenlight::{str(greenlight).lower()}")


exit(1 if not greenlight else 0)
