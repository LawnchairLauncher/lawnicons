package app.lawnchair.lawnicons.data.model

import kotlinx.serialization.Serializable

/**
 * Data class to hold information about an icon.
 *
 * @property drawableName The name of the drawable resource for the icon.
 * @property componentNames A list of [LabelAndComponentV2] objects representing the component names and
 * labels of the apps that have the icon.
 * @property drawableId The drawable id of the icon.
 */
@Serializable
data class IconInfo(
    val drawableName: String,
    override val componentNames: List<LabelAndComponentV2>,
    val drawableId: Int,
) : BaseIconInfo

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
                drawableId = icons.first().drawableId,
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
                    drawableId = iconInfo.drawableId,
                ),
            )
        }
    }
    return splitList
}

/**
 * Data class representing a label and component name pair.
 *
 * @property label The user-facing label associated with the component.
 * @property componentName The name of the component, typically a fully qualified class name.
 */
@Deprecated(
    message = "Use LabelAndComponentV2 instead",
    replaceWith = ReplaceWith("LabelAndComponentV2"),
    level = DeprecationLevel.WARNING,
)
@Serializable
data class LabelAndComponent(
    val label: String,
    val componentName: String,
)
