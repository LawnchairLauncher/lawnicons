package app.lawnchair.lawnicons.util

import android.content.Context
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import org.xmlpull.v1.XmlPullParser

fun Context.getIconInfo(): List<IconInfo> {
    val iconInfo = mutableListOf<IconInfo>()

    try {
        val xmlId = R.xml.grayscale_icon_map
        if (xmlId != 0) {
            val parser = resources.getXml(xmlId)
            val depth = parser.depth
            var type: Int
            while (
                (parser.next().also { type = it } != XmlPullParser.END_TAG || parser.depth > depth) &&
                type != XmlPullParser.END_DOCUMENT
            ) {
                if (type != XmlPullParser.START_TAG) continue
                if ("icon" == parser.name) {
                    val pkg = parser.getAttributeValue(null, "package")
                    val iconName = parser.getAttributeValue(null, "name")
                    val iconId = parser.getAttributeResourceValue(null, "drawable", 0)
                    if (iconId != 0 && pkg.isNotEmpty()) {
                        iconInfo += IconInfo(iconName, iconId)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return iconInfo
}
