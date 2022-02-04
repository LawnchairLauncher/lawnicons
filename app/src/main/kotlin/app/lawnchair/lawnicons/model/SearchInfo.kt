package app.lawnchair.lawnicons.model

data class SearchInfo(
    val iconInfo: IconInfo,
    val indexOfMatch: Int,
    val matchAtWordStart: Boolean,
)
