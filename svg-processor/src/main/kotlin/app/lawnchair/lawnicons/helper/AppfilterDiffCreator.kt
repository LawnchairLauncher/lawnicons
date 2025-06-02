/*
 * Copyright 2024 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.helper

import java.io.File

object AppfilterDiffCreator {
    private const val OUTPUT_FILE = "/xml/appfilter_diff.xml"

    private fun writePreviousRelease(
        previousAppFilterFile: String,
        customTag: String = "",
    ) {
        try {
            runGitCommand(listOf("fetch", "--tags"))
        } catch (_: Exception) {
            // assume that we have fetched the tags already
        }

        var lines = listOf<String>()

        try {
            val tags = runGitCommand(listOf("tag", "--sort=-creatordate"))
            val latestTag =
                customTag.ifEmpty {
                    tags.firstOrNull { it != "nightly" } ?: {
                        // fallback to `main` branch
                        val fallbackTags = runGitCommand(listOf("show", "main"))

                        fallbackTags.firstOrNull() ?: throw RuntimeException("No tags found")
                    }
                }

            // use relative file path, as `git show` does not work with absolute paths
            lines = runGitCommand(listOf("show", "$latestTag:app/assets/appfilter.xml"))
        } catch (e: Exception) {
            println(e)
        }

        val outputFile = File(previousAppFilterFile)
        outputFile.writeText(lines.joinToString(separator = "\n"))
    }

    private fun runGitCommand(
        args: List<String>,
    ): List<String> {
        return try {
            val command = listOf("git") + args

            val process = ProcessBuilder(command)
                .redirectErrorStream(true)
                .start()

            val result = process.inputStream.bufferedReader().readLines()
            if (process.waitFor() != 0) {
                throw RuntimeException("Failed to execute $command: $result")
            }
            println("Task `git $args` completed")

            result
        } catch (e: Exception) {
            println(e)
            listOf()
        }
    }

    private fun readFileContents(filePath: String): List<String> {
        return File(filePath).readLines().filterNot { it.trim().startsWith("<?xml") || it.trim().startsWith("<resources") }
    }

    private fun getLineDiff(
        mainLines: List<String>,
        developLines: List<String>,
    ): List<String> {
        val drawablesInMain = mainLines.mapNotNull { extractDrawableItem(it) }.toSet()

        return developLines.filter { item ->
            val drawable = extractDrawableItem(item)
            drawable != null && drawable !in drawablesInMain
        }.filterNot { it.trim().startsWith("<?xml") || it.trim().startsWith("<resources") }
    }

    private fun extractDrawableItem(item: String): String? {
        val regex = """drawable="([^"]+)"""".toRegex()
        return regex.find(item)?.groups?.get(1)?.value
    }

    private fun writeDiffToFile(
        diff: List<String>,
        resDir: String,
    ) {
        val outputFile = File(resDir + OUTPUT_FILE)
        val schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

        // Filter out any lines that could conflict with the XML structureAdd commentMore actions
        val filteredDiff = diff.filterNot {
            it.trim().startsWith("<?xml") || it.trim().startsWith("<resources>") || it.trim().startsWith("</resources>")
        }

        // Handle empty diff case
        if (filteredDiff.isEmpty()) {
            outputFile.writeText("$schema\n<resources />")
            return
        }

        val xmlContent = buildString {
            appendLine(schema)
            appendLine("<resources>")
            filteredDiff.forEach { line ->
                appendLine("    $line")
            }
            appendLine("</resources>")
        }

        outputFile.writeText(xmlContent)
    }

    fun createAppfilterDiff(
        resDir: String,
        customTag: String,
        appFilterFile: String,
        previousAppFilterFile: String,
    ) {
        writePreviousRelease(previousAppFilterFile, customTag)

        val diff = getLineDiff(
            readFileContents(previousAppFilterFile),
            readFileContents(appFilterFile),
        )

        writeDiffToFile(diff, resDir)
    }
}
