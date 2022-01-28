package app.lawnchair.lawnicons

data class SearchInfo(
    val iconInfo: IconInfo,
    val indexOfMatch: Int,
    val matchAtWordStart: Boolean,
)
