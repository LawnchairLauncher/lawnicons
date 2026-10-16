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

package app.lawnchair.lawnicons.data.repository.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.annotation.XmlRes
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.Component
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.LabelAndComponent
import org.xmlpull.v1.XmlPullParser

private val drawableIdMap: Map<String, Int> by lazy {
    try {
        R.drawable::class.java.fields.associate { field ->
            field.name to field.getInt(null)
        }
    } catch (_: Exception) {
        emptyMap()
    }
}

private class IconInfoGroup(
    val drawableName: String,
    val drawableId: Int,
    val componentNames: MutableList<LabelAndComponent> = mutableListOf(),
)

@SuppressLint("DiscouragedApi")
fun Context.getIconInfo(
    @XmlRes xmlId: Int,
): List<IconInfo> {
    if (xmlId == 0) return emptyList()

    val iconInfoGroups = LinkedHashMap<String, IconInfoGroup>()
    val componentInfoPrefixLength = "ComponentInfo{".length

    try {
        val parser = resources.getXml(xmlId)
        var type: Int
        while (parser.next().also { type = it } != XmlPullParser.END_DOCUMENT) {
            if (type != XmlPullParser.START_TAG || parser.name != "item") {
                continue
            }

            val componentString = parser.getAttributeValue(null, "component")
            val iconName = parser.getAttributeValue(null, "name")
            val drawableName = parser.getAttributeValue(null, "drawable")

            // Defensive check: ensure all required attributes exist
            if (componentString == null || iconName == null || drawableName == null) {
                Log.w("IconInfoParser", "Skipping item with missing attributes.")
                continue
            }

            val parsedComponentString =
                componentString.substring(componentInfoPrefixLength, componentString.length - 1)

            Component.unflattenFromString(parsedComponentString)?.let { componentName ->
                val group = iconInfoGroups.getOrPut(drawableName) {
                    val iconId = "${drawableName}_foreground"
                    val iconDrawable = drawableIdMap[iconId]
                        ?: resources.getIdentifier(iconId, "drawable", packageName)
                    IconInfoGroup(drawableName, iconDrawable)
                }
                group.componentNames.add(
                    LabelAndComponent(
                        label = iconName,
                        component = componentName,
                    ),
                )
            } ?: run {
                Log.e(
                    "IconInfoParser",
                    "Failed to parse component string: '$parsedComponentString'",
                )
            }
        }
    } catch (e: Exception) {
        Log.e("IconInfoParser", "A critical error occurred during XML parsing.", e)
    }

    return iconInfoGroups.values.map { group ->
        IconInfo(
            drawableName = group.drawableName,
            componentNames = group.componentNames,
            drawableId = group.drawableId,
        )
    }
}
