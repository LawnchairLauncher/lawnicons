/*
 * Copyright 2026 Lawnchair Launcher
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
import org.dom4j.DocumentHelper
import org.dom4j.Element

object ClockProcessor {
    private const val DYNAMIC_CLOCK = "dynamic-clock"
    private const val DRAWABLE = "drawable"
    private const val HOUR_LAYER_INDEX = "hourLayerIndex"
    private const val MINUTE_LAYER_INDEX = "minuteLayerIndex"
    private const val SECOND_LAYER_INDEX = "secondLayerIndex"

    // Launchers drive the hands through the drawable level, which RotateDrawable maps onto
    // 0..10000. The hour layer gets 720 levels per turn, the minute layer 60 and the second
    // layer 600, so these are the degrees a hand would cover over the full level range.
    private const val HOUR_DEGREES = "5000"
    private const val MINUTE_DEGREES = "60000"
    private const val SECOND_DEGREES = "6000"

    /**
     * Rewrites the adaptive icon of every `<dynamic-clock>` drawable so that its hands are
     * separate, rotatable layers built from `<drawable>_dial.svg`, `<drawable>_hour.svg`,
     * `<drawable>_minute.svg` and, if a second layer is declared, `<drawable>_second.svg`.
     */
    fun createClockDrawables(appFilterFile: String, resDir: String) {
        val appFilterDocument = XmlUtil.getDocument(appFilterFile)
        for (element in XmlUtil.getElements(appFilterDocument, DYNAMIC_CLOCK)) {
            val drawable = element.attributeValue(DRAWABLE)
            val layers = buildMap {
                put(0, "dial" to null)
                put(element.layerIndex(HOUR_LAYER_INDEX), "hour" to HOUR_DEGREES)
                put(element.layerIndex(MINUTE_LAYER_INDEX), "minute" to MINUTE_DEGREES)
                put(element.layerIndex(SECOND_LAYER_INDEX), "second" to SECOND_DEGREES)
            }.filterKeys { it >= 0 }.toSortedMap()

            require(layers.keys.toList() == layers.keys.indices.toList()) {
                "Layer indices of dynamic clock $drawable must be consecutive, with 0 left for the dial"
            }
            layers.values.forEach { (part, _) ->
                require(File("$resDir/drawable/${drawable}_${part}_foreground.xml").exists()) {
                    "Dynamic clock $drawable is missing ${drawable}_$part.svg"
                }
            }

            val document = DocumentHelper.createDocument()
            val root = document.addElement("adaptive-icon")
                .addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
            root.addElement("background")
                .addAttribute("android:drawable", "@color/primaryBackground")
            root.addElement("foreground").addLayers(drawable, layers.values, "32%")
            root.addElement("monochrome").addLayers(drawable, layers.values, "28%")
            XmlUtil.writeDocumentToFile(document, "$resDir/drawable/$drawable.xml")
            println("Created dynamic clock $drawable")
        }
    }

    private fun Element.layerIndex(attribute: String): Int = attributeValue(attribute)?.toIntOrNull() ?: -1

    private fun Element.addLayers(
        drawable: String,
        layers: Collection<Pair<String, String?>>,
        inset: String,
    ) {
        val layerList = addElement("layer-list")
        layers.forEach { (part, degrees) ->
            var parent = layerList.addElement("item")
            if (degrees != null) {
                parent = parent.addElement("rotate")
                    .addAttribute("android:fromDegrees", "0")
                    .addAttribute("android:toDegrees", degrees)
                    .addAttribute("android:pivotX", "50%")
                    .addAttribute("android:pivotY", "50%")
            }
            parent.addElement("inset")
                .addAttribute("android:inset", inset)
                .addAttribute("android:drawable", "@drawable/${drawable}_${part}_foreground")
        }
    }
}
