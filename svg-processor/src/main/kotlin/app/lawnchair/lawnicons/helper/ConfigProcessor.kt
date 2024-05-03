package app.lawnchair.lawnicons.helper

import java.util.Locale
import org.dom4j.Document
import org.dom4j.tree.DefaultDocument

object ConfigProcessor {
    private const val ITEM = "item"
    private const val CATEGORY = "category"
    private const val COMPONENT = "component"
    private const val PACKAGE = "package"
    private const val DRAWABLE = "drawable"
    private const val DRAWABLEIGNORE = "drawableIgnore"
    private const val ICONS = "icons"
    private const val ICON = "icon"
    private const val RESOURCES = "resources"
    private const val TITLE = "title"
    private const val NAME = "name"
    private const val VERSION = "version"

    fun loadAndCreateConfigs(appFilterFile: String, vararg resDirs: String) {
        val (appFilterDocument, drawableMap, iconMap) = loadConfigFromXml(appFilterFile)
        val sortedDrawableMap = drawableMap.toList().sortedBy { (_, value) -> value }.toMap()

        resDirs.forEach {
            // Create Drawable files
            writeDrawableToFile(sortedDrawableMap, "$it/xml/drawable.xml")
            // Create Icon Map files
            writeIconMapToFile(sortedDrawableMap, iconMap, "$it/xml/grayscale_icon_map.xml")
            // Write AppFilter to resource directory
            XmlUtil.writeDocumentToFile(appFilterDocument, "$it/xml/appfilter.xml")
        }
    }

    private fun loadConfigFromXml(appFilterFile: String): Triple<Document, Map<String, String>, Map<String, String>> {
        val drawableMap = mutableMapOf<String, String>()
        val iconMap = mutableMapOf<String, String>()
        val componentStart = "ComponentInfo{"
        val componentEnd = "}"
        val appFilterDocument = XmlUtil.getDocument(appFilterFile)
        val appFilterElements = XmlUtil.getElements(appFilterDocument, ITEM)
        for (element in appFilterElements) {
            val componentInfo = element.attribute(COMPONENT).value
            val drawable = element.attribute(DRAWABLE).value
            val name = element.attribute(NAME).value
            val shouldIgnore: String? = element.attributeValue(DRAWABLEIGNORE)
            if (shouldIgnore != null) continue

            if (componentInfo.startsWith(componentStart) && componentInfo.endsWith(componentEnd)) {
                val component = componentInfo.substring(
                    componentStart.length,
                    componentInfo.length - componentEnd.length,
                )
                drawableMap[component] = drawable
                iconMap[component] = name
            }
        }
        return Triple(appFilterDocument, drawableMap.toMap(), iconMap.toMap())
    }

    private fun writeIconMapToFile(
        drawableMap: Map<String, String>,
        iconMap: Map<String, String>,
        filename: String,
    ) {
        val iconsDocument = DefaultDocument().apply { addElement(ICONS) }
        drawableMap.forEach { (componentInfo, drawable) ->
            val component = componentInfo.split("/").toTypedArray()
            val name = iconMap.getOrDefault(
                componentInfo,
                drawable.replace("_".toRegex(), " ").capitalize(),
            )
            iconsDocument.rootElement.addElement(ICON)
                .addAttribute(DRAWABLE, "@drawable/${drawable}_foreground")
                .addAttribute(PACKAGE, component[0])
                .addAttribute(NAME, name)
        }
        XmlUtil.writeDocumentToFile(iconsDocument, filename)
    }

    private fun writeDrawableToFile(drawableMap: Map<String, String>, filename: String) {
        val resourceDocument = DefaultDocument().apply {
            addElement(RESOURCES)
            rootElement.addElement(VERSION).addText("1")
        }
        val groupNames = mutableListOf<Char>()
        drawableMap.values.distinct().forEach { drawable: String ->
            val groupName = drawable[0].uppercaseChar()
            if (groupName !in groupNames) {
                resourceDocument.rootElement.addElement(CATEGORY)
                    .addAttribute(TITLE, groupName.toString())
                groupNames.add(groupName)
            }
            resourceDocument.rootElement.addElement(ITEM).addAttribute(DRAWABLE, drawable)
        }
        XmlUtil.writeDocumentToFile(resourceDocument, filename)
    }

    private fun String.capitalize(): String = replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}
