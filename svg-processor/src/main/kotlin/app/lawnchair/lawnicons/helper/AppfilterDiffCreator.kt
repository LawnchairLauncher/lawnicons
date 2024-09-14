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

    private fun getPreviousReleaseLines(
        appFilterFile: String,
    ): List<String> {
        return try {
            runGitCommand(listOf("fetch", "--tags"))

            val tagCommand =
                listOf("/usr/bin/bash", "-c", "git tag --sort=-creatordate | head -n 1")
            val tagProcess = ProcessBuilder(tagCommand)
                .redirectErrorStream(true)
                .start()

            val latestTag = tagProcess.inputStream.bufferedReader().readLine()
            if (tagProcess.waitFor() != 0) {
                throw RuntimeException("Failed to get latest tag")
            }

            runGitCommand(listOf("show", "$latestTag:$appFilterFile"))
        } catch (e: Exception) {
            println(e)
            listOf()
        }
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
            println("task git $args completed")

            result
        } catch (e: Exception) {
            println(e)
            listOf()
        }
    }

    private fun readFileContents(filePath: String): List<String> {
        return File(filePath).readLines()
    }

    private fun getLineDiff(
        mainLines: List<String>,
        developLines: List<String>,
    ): List<String> {
        return developLines.filterNot { it in mainLines }
    }

    private fun writeDiffToFile(
        diff: List<String>,
        resDir: String,
    ) {
        val outputFile = File(resDir + OUTPUT_FILE)
        val schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

        if (diff.isEmpty()) {
            outputFile.writeText("$schema\n<resources></resources>")
            return
        }

        val xmlContent = buildString {
            appendLine(schema)
            appendLine("<resources>")
            diff.forEach { line ->
                appendLine("    $line")
            }
            appendLine("</resources>")
        }

        outputFile.writeText(xmlContent)
    }

    fun createAppfilterDiff(
        resDir: String,
        appFilterFile: String,
    ) {
        val diff = getLineDiff(
            getPreviousReleaseLines(appFilterFile),
            readFileContents(appFilterFile),
        )

        writeDiffToFile(diff, resDir)
    }
}
