package app.lawnchair.lawnicons.model

import kotlinx.serialization.Serializable

/**
 * Data class to hold information about an icon.
 *
 * @property drawableName The name of the drawable resource for the icon.
 * @property componentNames A list of [LabelAndComponent] objects representing the component names and
 * labels of the apps that have the icon.
 * @property id A unique identifier for the icon.
 */
data class IconInfo(
    val drawableName: String,
    val componentNames: List<LabelAndComponent>,
    val id: Int,
) {
    /**
     * The user-facing label associated with the icon, derived from the first available
     * [LabelAndComponent] object.
     */
    val label = componentNames.firstOrNull()?.label ?: ""
}

/**
 * Merges a list of [IconInfo] objects, grouping them by their `drawableName` and
 * combining the `componentNames` of icons with the same drawable name.
 *
 * @return A new list of [IconInfo] objects with merged component names for icons
 *         sharing the same drawable name.
 */
fun List<IconInfo>.mergeByDrawableName(): List<IconInfo> {
    return groupBy { it.drawableName }
        .map { (drawableName, icons) ->
            val mergedComponentNames = icons.flatMap { it.componentNames }
            IconInfo(
                componentNames = mergedComponentNames,
                drawableName = drawableName,
                id = icons.first().id,
            )
        }
}

/**
 * Splits [IconInfo] objects with multiple component names into a list where each
 * [IconInfo] object has a single component name.
 *
 * @return A new list of [IconInfo] objects, each with a single component name.
 */
fun List<IconInfo>.splitByComponentName(): List<IconInfo> {
    val splitList = mutableListOf<IconInfo>()
    for (iconInfo in this) {
        for (nameAndComponent in iconInfo.componentNames) {
            splitList.add(
                IconInfo(
                    componentNames = listOf(nameAndComponent),
                    drawableName = iconInfo.drawableName,
                    id = iconInfo.id,
                ),
            )
        }
    }
    return splitList
}

fun IconInfo.getFirstLabelAndComponent(): LabelAndComponent {
    val firstLabel = componentNames.firstOrNull()?.label ?: ""
    val firstComponent = componentNames.firstOrNull()?.componentName ?: ""
    return LabelAndComponent(firstLabel, firstComponent)
}

/**
 * Data class representing a label and component name pair.
 *
 * @property label The user-facing label associated with the component.
 * @property componentName The name of the component, typically a fully qualified class name.
 */
@Serializable
data class LabelAndComponent(
    val label: String,
    val componentName: String,
)
