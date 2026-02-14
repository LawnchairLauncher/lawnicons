/*
 * Copyright 2025 Lawnchair Launcher
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

package app.lawnchair.lawnicons.data.repository.iconrequest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.AdaptiveIconDrawable
import android.util.Log
import android.util.Xml
import androidx.core.graphics.drawable.toBitmap
import app.lawnchair.lawnicons.data.model.SystemIconInfo
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.Writer
import java.text.Normalizer
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlSerializer

internal object IconRequestBundler {
    suspend fun createIconRequestZip(
        context: Context,
        iconRequestList: List<SystemIconInfo>,
        zipFileName: String = "lawnicons_request.zip",
    ): File? {
        if (iconRequestList.isEmpty()) {
            Log.d(TAG, "Icon request list is empty. No ZIP file created.")
            return null
        }

        val uniqueIconInfoList = createUniqueIconInfoList(iconRequestList)
        val outputDir = context.cacheDir
        val outputFile = File(outputDir, zipFileName)
        var listFile: File? = null
        var appfilterFile: File? = null

        return withContext(Dispatchers.IO) {
            try {
                // ensure that all metadata files goes to the top of the list
                listFile = File.createTempFile("list", ".txt", outputDir)
                appfilterFile = File.createTempFile("appfilter", ".xml", outputDir)

                runCatching {
                    FileWriter(listFile).use { writer ->
                        writer.write(formatIconRequestList(iconRequestList))
                    }
                    Log.d(TAG, "Successfully created list file at ${listFile!!.absolutePath}")

                    FileWriter(appfilterFile).use { writer ->
                        uniqueIconInfoList.toAppFilterXML(writer)
                    }
                    Log.d(
                        TAG,
                        "Successfully created appfilter file at ${appfilterFile!!.absolutePath}",
                    )

                    ZipOutputStream(FileOutputStream(outputFile)).use { zos ->
                        uniqueIconInfoList.forEach { uniqueInfo ->
                            val (iconInfo, drawableName) = uniqueInfo
                            val fileName = "$drawableName.png"

                            val entry = ZipEntry(fileName)
                            zos.putNextEntry(entry)

                            val bitmap = if (iconInfo.drawable is AdaptiveIconDrawable) {
                                try {
                                    AdaptiveIconBitmap.toBitmap(iconInfo.drawable)
                                } catch (e: Exception) {
                                    Log.e(
                                        TAG,
                                        "Failed to convert adaptive icon to bitmap for ${iconInfo.label}, using fallback",
                                        e,
                                    )
                                    iconInfo.drawable.toBitmap()
                                }
                            } else {
                                iconInfo.drawable.toBitmap()
                            }

                            val pngByteArray = bitmap.toByteArray()
                            zos.write(pngByteArray)
                            zos.closeEntry()
                            Log.d(TAG, "Added $fileName to ZIP.")
                        }

                        addFileToZip(zos, listFile, "!icon_request_list.txt")
                        addFileToZip(zos, appfilterFile, "!appfilter.xml")

                        outputFile
                    }
                }.getOrElse { e ->
                    Log.e(TAG, "Error creating ZIP file", e)
                    outputFile.delete()
                    listFile?.delete()
                    appfilterFile?.delete()
                    null
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error initializing metadata files", e)
                null
            } finally {
                listFile?.delete()
                appfilterFile?.delete()
            }
        }
    }

    private fun addFileToZip(zos: ZipOutputStream, file: File, name: String) {
        val entry = ZipEntry(name)
        zos.putNextEntry(entry)
        file.inputStream().use { input ->
            input.copyTo(zos)
        }
        zos.closeEntry()
        Log.d(TAG, "Added ${file.name} to ZIP.")
    }

    /**
     * Normalizes a string to be suitable for use as a filename.
     *
     * This function performs the following steps:
     * 1. Normalizes Unicode characters to their decomposed form (NFD).
     * 2. Removes diacritics (marks) to approximate ASCII characters.
     * 3. Replaces spaces with underscores.
     * 4. Converts the string to lowercase.
     * 5. Replaces any remaining non-alphanumeric characters with underscores.
     * 6. If the resulting string is blank, it returns a default filename "unnamed_icon".
     *
     * @param label The input string to normalize.
     * @return The normalized string suitable for use as a filename.
     */
    private fun normalizeFileName(label: String): String {
        val nfdNormalizedString = Normalizer.normalize(label, Normalizer.Form.NFD)
        val asciiApproximation = Regex("\\p{Mn}+").replace(nfdNormalizedString, "")

        val normalized = asciiApproximation
            .lowercase()
            .replace(Regex("[^a-z0-9_]"), "_")
            .trim('_')

        if (normalized.isBlank()) {
            return "unnamed_icon"
        }
        return normalized
    }

    private fun Bitmap.toByteArray(): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.PNG, 100, stream)
        return stream.toByteArray()
    }

    /**
     * Converts a list of SystemIconInfo objects into an XML string representation
     * suitable for an appfilter.
     *
     * @return An XML string representing the appfilter items within a <resources> tag.
     */
    private fun List<UniqueIconInfo>.toAppFilterXML(writer: Writer) {
        val serializer: XmlSerializer = Xml.newSerializer()

        serializer.setOutput(writer)
        serializer.startDocument("UTF-8", true)

        try {
            serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true)
        } catch (e: IllegalStateException) {
            // Feature not supported or can't be set at this point
            Log.w(TAG, "XML indentation feature not supported by this serializer.", e)
        }

        serializer.startTag(null, "resources")

        this.forEach { (iconInfo, drawableName) ->
            val componentNameString = iconInfo.componentName.flattenToString()

            serializer.startTag(null, "item")
            serializer.attribute(null, "component", "ComponentInfo{$componentNameString}")
            serializer.attribute(null, "drawable", drawableName)
            serializer.attribute(null, "name", iconInfo.label)
            serializer.endTag(null, "item")
        }

        serializer.endTag(null, "resources")
        serializer.endDocument()
    }

    private fun createUniqueIconInfoList(iconRequestList: List<SystemIconInfo>): List<UniqueIconInfo> {
        val usedFileNames = mutableMapOf<String, Int>()

        return iconRequestList.map { iconInfo ->
            val baseName = normalizeFileName(iconInfo.label)
            val count = usedFileNames.getOrDefault(baseName, 0)
            val drawableName = if (count > 0) "${baseName}_$count" else baseName
            usedFileNames[baseName] = count + 1
            UniqueIconInfo(iconInfo, drawableName)
        }
    }

    private data class UniqueIconInfo(
        val iconInfo: SystemIconInfo,
        val drawableName: String,
    )
}

fun formatIconRequestList(iconRequestList: List<SystemIconInfo>): String = iconRequestList
    .joinToString(separator = "\n\n") { "${it.label}\n${it.componentName.flattenToString()}" }

private const val TAG = "IconRequestBundler"
