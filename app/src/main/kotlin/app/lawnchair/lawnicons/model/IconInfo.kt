package app.lawnchair.lawnicons.model

/**
 * Data class to hold information about an icon.
 *
 * @property name The name of the icon.
 * @property drawableName The name of the drawable resource for the icon.
 * @property packageName The package name of the app that owns the icon.
 * @property id A unique identifier for the icon.
 */
@Deprecated(
    message = "Use appfilter implementation instead.",
    replaceWith = ReplaceWith("IconInfoAppfilter", "app.lawnchair.lawnicons.model.IconInfoAppfilter"),
)
data class IconInfo(
    val name: String,
    val drawableName: String,
    val packageName: String,
    val id: Int,
)

/**
 * Data class to hold information about an icon.
 *
 * @property name The name of the icon.
 * @property drawableName The name of the drawable resource for the icon.
 * @property componentName The component name of the app that owns the icon.
 * @property id A unique identifier for the icon.
 */
data class IconInfoAppfilter(
    val name: String,
    val drawableName: String,
    val componentName: String,
    val id: Int,
)
