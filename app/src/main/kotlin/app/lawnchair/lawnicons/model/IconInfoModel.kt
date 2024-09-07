package app.lawnchair.lawnicons.model

/**
 * Data class to hold information about icons.
 *
 * @property iconInfo A list of `IconInfo` objects.
 * @property iconCount The total number of icons.
 */
data class IconInfoModel(
    val iconInfo: List<IconInfo> = emptyList(),
    val iconCount: Int = 0,
)
