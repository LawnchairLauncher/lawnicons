package app.lawnchair.lawnicons.model

@Deprecated(
    message = "Use appfilter implementation instead.",
    replaceWith = ReplaceWith("IconInfoAppfilter", "app.lawnchair.lawnicons.model.IconInfoAppfilter")
)
data class IconInfo(
    val name: String,
    val drawableName: String,
    val packageName: String,
    val id: Int,
)

data class IconInfoAppfilter(
    val name: String,
    val drawableName: String,
    val componentName: String,
    val id: Int,
)
