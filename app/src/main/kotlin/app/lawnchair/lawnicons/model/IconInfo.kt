package app.lawnchair.lawnicons.model

/**
 * Data class to hold information about an icon.
 *
 * @property name The name of the icon.
 * @property drawableName The name of the drawable resource for the icon.
 * @property componentName The component name of the app that owns the icon.
 * @property id A unique identifier for the icon.
 */
data class IconInfo(
    val name: String,
    val drawableName: String,
    val componentName: String,
    val id: Int,
) {
    companion object {
        fun convert(groupedList: List<IconInfoGrouped>): List<IconInfo> {
            val result = mutableListOf<IconInfo>()
            for (grouped in groupedList) {
                for (i in grouped.names.indices) {
                    result.add(
                        IconInfo(
                            name = grouped.names[i],
                            drawableName = grouped.drawableName,
                            componentName = grouped.componentNames[i],
                            id = grouped.id
                        )
                    )
                }
            }
            return result
        }
    }
}

data class IconInfoGrouped(
    val names: List<String>,
    val drawableName: String,
    val componentNames: List<String>,
    val id: Int
) {
    companion object {
        fun convert(iconInfoList: List<IconInfo>): List<IconInfoGrouped> {
            // Group by drawableName
            return iconInfoList
                .groupBy { it.drawableName }
                .map { (drawableName, icons) ->
                    require(icons.all { it.id == icons[0].id }) { "Icon IDs in a group must be the same" }

                    IconInfoGrouped(
                        names = icons.map { it.name },
                        drawableName = drawableName,
                        componentNames = icons.map { it.componentName },
                        id = icons[0].id // Assuming IDs in the group are the same
                    )
            }
        }
    }
}
