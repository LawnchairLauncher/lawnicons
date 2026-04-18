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
import java.io.FileWriter
import org.dom4j.Document
import org.dom4j.io.OutputFormat
import org.dom4j.io.SAXReader
import org.dom4j.io.XMLWriter
import org.dom4j.tree.DefaultDocument

object AppfilterDiffCreator {
    private const val OUTPUT_FILE = "/xml/appfilter_diff.xml"

    private const val RESOURCES = "resources"
    private const val ITEM = "item"
    private const val DRAWABLE = "drawable"
    private const val COMPONENT = "component"
    private const val NAME = "name"

    fun createAppfilterDiff(
        resDir: String,
        currentAppFilterFile: String,
        previousAppFilterFile: String,
    ) {
        val currentAppFilterDocument = File(currentAppFilterFile).asXMLDocument()
        val previousAppFilterDocument = File(previousAppFilterFile).asXMLDocument()

        val currentAppFilterItems = currentAppFilterDocument.rootElement.elements(ITEM)
        val previousAppFilterItems = previousAppFilterDocument.rootElement.elements(ITEM)

        val previousDrawables = previousAppFilterItems
            .map {
                it.attribute(DRAWABLE).value
            }

        val filteredSvgs = currentAppFilterItems
            // step 1: filter svgs from the previous version
            .filterNot { previousDrawables.contains(it.attributeValue(DRAWABLE)) }

        val filteredComponents = previousAppFilterItems
            .map {
                getPackageName(it.attribute(COMPONENT).value)
            }
            .toSet()

        val filteredElements = filteredSvgs
            // step 2: filter component names that exist from the previous version
            .filterNot { items ->
                filteredComponents.contains(getPackageName(items.attributeValue(COMPONENT)))
            }

        val outputFile = File(resDir, OUTPUT_FILE)

        val iconsDocument = DefaultDocument().apply { addElement(RESOURCES) }

        filteredElements.forEach {
            iconsDocument.rootElement.addElement(ITEM)
                .apply {
                    addAttribute(DRAWABLE, it.attributeValue(DRAWABLE))
                    addAttribute(COMPONENT, it.attributeValue(COMPONENT))
                    addAttribute(NAME, it.attributeValue(NAME))
                }
        }

        outputFile.parentFile.mkdirs()
        FileWriter(outputFile).use { fw ->
            XMLWriter(fw, OutputFormat.createPrettyPrint()).apply {
                write(iconsDocument)
                close()
            }
        }
    }

    private fun File.asXMLDocument(): Document {
        return SAXReader().apply { encoding = Charsets.UTF_8.name() }.read(this.inputStream())
    }
}

private fun getPackageName(componentString: String): String {
    val componentInfoPrefixLength = "ComponentInfo{".length
    val component = componentString.substring(componentInfoPrefixLength, componentString.length - 1)
    return component.split("/").first()
}
