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
    private const val DIAL = "dial"

    /**
     * A rotating layer of a dynamic clock. Launchers move the hands by setting the layer's
     * level, which RotateDrawable maps onto 0..10000. One level is a minute for the hour and
     * minute layers and a tenth of a second for the second layer, so [degrees] is the rotation
     * that the full level range would cover.
     */
    private enum class Hand(val part: String, val indexAttribute: String, val degrees: String) {
        HOUR("hour", "hourLayerIndex", "5000"),
        MINUTE("minute", "minuteLayerIndex", "60000"),
        SECOND("second", "secondLayerIndex", "6000"),
    }

    /**
     * Rewrites the adaptive icon of every `<dynamic-clock>` drawable so that its hands are
     * separate, rotatable layers. Layer 0 is `<drawable>_dial.svg`, the layers above it are
     * `<drawable>_hour.svg`, `<drawable>_minute.svg` and `<drawable>_second.svg`, in the order
     * given by the layer indices. A hand without a layer index is left out.
     */
    fun createClockDrawables(appFilterFile: String, resDir: String) {
        val appFilterDocument = XmlUtil.getDocument(appFilterFile)
        for (element in XmlUtil.getElements(appFilterDocument, DYNAMIC_CLOCK)) {
            val drawable = element.attributeValue(DRAWABLE)
            val hands = Hand.entries
                .map { (element.attributeValue(it.indexAttribute)?.toIntOrNull() ?: -1) to it }
                .filter { (index, _) -> index >= 0 }
                .sortedBy { (index, _) -> index }

            require(hands.map { (index, _) -> index } == (1..hands.size).toList()) {
                "Layer indices of dynamic clock $drawable must count up from 1, layer 0 is the dial"
            }
            val parts = listOf(DIAL) + hands.map { (_, hand) -> hand.part }
            parts.forEach {
                require(File("$resDir/drawable/${drawable}_${it}_foreground.xml").exists()) {
                    "Dynamic clock $drawable is missing ${drawable}_$it.svg"
                }
                // The parts are only used as layers, they don't need an adaptive icon of their own
                File("$resDir/drawable/${drawable}_$it.xml").delete()
            }

            val document = DocumentHelper.createDocument()
            val root = document.addElement("adaptive-icon")
                .addAttribute("xmlns:android", "http://schemas.android.com/apk/res/android")
            root.addElement("background")
                .addAttribute("android:drawable", "@color/primaryBackground")
            root.addElement("foreground").addLayers(drawable, hands.map { (_, hand) -> hand }, "32%")
            root.addElement("monochrome").addLayers(drawable, hands.map { (_, hand) -> hand }, "28%")
            XmlUtil.writeDocumentToFile(document, "$resDir/drawable/$drawable.xml")
            println("Created dynamic clock $drawable")
        }
    }

    private fun Element.addLayers(drawable: String, hands: List<Hand>, inset: String) {
        val layerList = addElement("layer-list")
        layerList.addElement("item").addInset(inset, "${drawable}_$DIAL")
        hands.forEach {
            layerList.addElement("item").addElement("rotate")
                .addAttribute("android:fromDegrees", "0")
                .addAttribute("android:toDegrees", it.degrees)
                .addAttribute("android:pivotX", "50%")
                .addAttribute("android:pivotY", "50%")
                .addInset(inset, "${drawable}_${it.part}")
        }
    }

    private fun Element.addInset(inset: String, drawable: String) {
        addElement("inset")
            .addAttribute("android:inset", inset)
            .addAttribute("android:drawable", "@drawable/${drawable}_foreground")
    }
}
