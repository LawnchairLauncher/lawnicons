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
import androidx.annotation.XmlRes
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.LabelAndComponent
import app.lawnchair.lawnicons.data.model.mergeByDrawableName
import org.xmlpull.v1.XmlPullParser

@SuppressLint("DiscouragedApi")
fun Context.getIconInfo(
    @XmlRes xmlId: Int = R.xml.appfilter,
): List<IconInfo> {
    val iconInfo = mutableListOf<IconInfo>()

    val componentInfoPrefixLength = "ComponentInfo{".length

    try {
        if (xmlId != 0) {
            val parser = resources.getXml(xmlId)
            val depth = parser.depth
            var type: Int
            while (
                (
                    parser.next()
                        .also { type = it } != XmlPullParser.END_TAG ||
                        parser.depth > depth
                    ) &&
                type != XmlPullParser.END_DOCUMENT
            ) {
                if (type != XmlPullParser.START_TAG) continue
                if ("item" == parser.name) {
                    val component = parser.getAttributeValue(null, "component")
                    val iconName = parser.getAttributeValue(null, "name")

                    val initialIconId = parser.getAttributeValue(null, "drawable")
                    val iconId = "${initialIconId}_foreground"
                    val iconDrawable = resources.getIdentifier(iconId, "drawable", packageName)

                    var actualComponent = ""

                    val parsedComponent =
                        component.substring(componentInfoPrefixLength, component.length - 1)

                    if (parsedComponent.isNotEmpty() &&
                        !parsedComponent.startsWith("/") &&
                        !parsedComponent.endsWith("/")
                    ) {
                        actualComponent = parsedComponent
                    }

                    iconInfo.add(
                        IconInfo(
                            iconId,
                            listOf(LabelAndComponent(iconName, actualComponent)),
                            iconDrawable,
                        ),
                    )
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return iconInfo.mergeByDrawableName()
}
