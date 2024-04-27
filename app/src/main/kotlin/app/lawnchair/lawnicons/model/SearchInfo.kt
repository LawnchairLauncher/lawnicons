package app.lawnchair.lawnicons.model

data class SearchInfo(
    val iconInfo: IconInfo,
    val indexOfMatch: Int,
    val matchAtWordStart: Boolean,
)

enum class SearchMode {
    NAME,
    PACKAGE_NAME,
    DRAWABLE
}
