#  Copyright 2025 Lawnchair Launcher
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

import os
import subprocess

# Config options
OUTPUT_DIRECTORY = "app/assets/"
OUTPUT_FILE = "appfilter_previous.xml"
CUSTOM_GIT_TAG = ""


def run_git_command(args):
    command = ["git"] + args
    try:
        process = subprocess.run(command, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True,
                                 check=True)
        print(f"Task `git {' '.join(args)}` completed")
        return process.stdout.strip().splitlines()
    except subprocess.CalledProcessError as e:
        print(f"Failed to execute {' '.join(command)}: {e.stderr.strip()}")
        return []


def write_previous_release(previous_app_filter_file, custom_tag=""):
    run_git_command(["fetch", "--tags"])  # Fetch tags

    tags_output = run_git_command(["tag", "--sort=-creatordate"])
    latest_tag = custom_tag or next((tag for tag in tags_output if tag != "nightly"), None)

    if not latest_tag:
        print("No suitable tag found, falling back to main branch.")
        lines = run_git_command(["show", "main:app/assets/appfilter.xml"])
        if not lines:
            raise RuntimeError("No tags found and could not retrieve from main.")
    else:
        lines = run_git_command(["show", f"{latest_tag}:app/assets/appfilter.xml"])

    if lines:
        try:
            with open(previous_app_filter_file, "w") as f:
                f.write("\n".join(lines))
            print(f"Successfully wrote previous release appfilter to {previous_app_filter_file}")
        except IOError as e:
            print(f"Error writing to file {previous_app_filter_file}: {e}")


if __name__ == '__main__':
    path = os.path.join(OUTPUT_DIRECTORY, OUTPUT_FILE)
    print(f"Attempting to write previous release appfilter to: {path}")
    write_previous_release(path, CUSTOM_GIT_TAG)
