"""Tests for the review bot's handling of SVG additions and removals.

Run from the repository root with:
    python -m unittest discover -s .github/scripts -p "test_*.py"
"""
import os
import subprocess
import tempfile
import unittest
from pathlib import Path

import bot_orchestrator

SVG = '<svg xmlns="http://www.w3.org/2000/svg" width="192" height="192"/>'
APPFILTER = """<?xml version="1.0" encoding="UTF-8"?>
<resources>
{items}</resources>
"""
ITEM = '  <item component="ComponentInfo{{{component}}}" drawable="{drawable}" name="{name}" />\n'


def git(cwd: Path, *args: str) -> None:
    env = {
        **os.environ,
        "GIT_AUTHOR_NAME": "test", "GIT_AUTHOR_EMAIL": "test@example.com",
        "GIT_COMMITTER_NAME": "test", "GIT_COMMITTER_EMAIL": "test@example.com",
    }
    subprocess.run(["git", *args], cwd=cwd, check=True, env=env, capture_output=True)


class UnlinkedIconReportTest(unittest.TestCase):
    def setUp(self):
        self._tmp = tempfile.TemporaryDirectory()
        self.addCleanup(self._tmp.cleanup)
        self.repo = Path(self._tmp.name)
        (self.repo / "svgs").mkdir()
        (self.repo / "app/assets").mkdir(parents=True)
        self.write_appfilter({"alpha": "Alpha"})
        (self.repo / "svgs/alpha.svg").write_text(SVG)

        git(self.repo, "init", "-q", "-b", "main")
        git(self.repo, "add", "-A")
        git(self.repo, "commit", "-q", "-m", "base")
        # Mimic the remote-tracking ref the bot diffs against.
        git(self.repo, "update-ref", "refs/remotes/origin/develop", "HEAD")
        git(self.repo, "checkout", "-q", "-b", "pr")

        self._saved = (
            bot_orchestrator.SCRIPT_REPO_ROOT,
            bot_orchestrator.DATA_REPO_ROOT,
            bot_orchestrator.APPFILTER_PATH,
            bot_orchestrator.DRAWABLES_DIR,
            bot_orchestrator.SVG_LINTER_PATH,
            bot_orchestrator.NAME_CHECKER_PATH,
        )
        self.addCleanup(self._restore_paths)
        # Scripts come from this checkout, data from the temporary repo.
        bot_orchestrator.configure_paths(str(self.repo), str(Path(__file__).resolve().parents[2]))

    def _restore_paths(self):
        (
            bot_orchestrator.SCRIPT_REPO_ROOT,
            bot_orchestrator.DATA_REPO_ROOT,
            bot_orchestrator.APPFILTER_PATH,
            bot_orchestrator.DRAWABLES_DIR,
            bot_orchestrator.SVG_LINTER_PATH,
            bot_orchestrator.NAME_CHECKER_PATH,
        ) = self._saved

    def write_appfilter(self, drawables: dict[str, str]):
        items = "".join(
            ITEM.format(component=f"com.{d}/com.{d}.Main", drawable=d, name=n)
            for d, n in drawables.items()
        )
        (self.repo / "app/assets/appfilter.xml").write_text(APPFILTER.format(items=items))

    def commit(self, message="pr"):
        git(self.repo, "add", "-A")
        git(self.repo, "commit", "-q", "-m", message)

    def test_added_svg_without_appfilter_entry_is_reported(self):
        (self.repo / "svgs/beta.svg").write_text(SVG)
        self.commit()

        self.assertEqual(bot_orchestrator.get_changed_drawables("develop"), [])
        report = bot_orchestrator.collect_final_report("develop")

        self.assertEqual(list(report), ["beta.svg"])
        self.assertEqual(report["beta.svg"][0]["outcome_id"], "ORPHAN")
        self.assertIn("missing `appfilter.xml` entry", report["beta.svg"][0]["message"])

    def test_removed_svg_still_linked_is_reported(self):
        (self.repo / "svgs/alpha.svg").unlink()
        self.commit()

        report = bot_orchestrator.collect_final_report("develop")

        self.assertEqual(list(report), ["alpha.svg"])
        self.assertEqual(report["alpha.svg"][0]["outcome_id"], "MISSING")

    def test_modified_svg_is_not_name_checked(self):
        (self.repo / "svgs/alpha.svg").write_text(SVG + "\n")
        self.commit()

        self.assertEqual(bot_orchestrator.get_added_or_removed_svg_names("develop"), [])

    def test_linked_addition_has_no_findings_and_names_are_deduplicated(self):
        (self.repo / "svgs/beta.svg").write_text(SVG)
        self.write_appfilter({"alpha": "Alpha", "beta": "Beta"})
        self.commit()

        self.assertEqual(bot_orchestrator.get_added_or_removed_svg_names("develop"), ["beta"])
        self.assertEqual(bot_orchestrator.collect_final_report("develop"), {})


if __name__ == "__main__":
    unittest.main()
