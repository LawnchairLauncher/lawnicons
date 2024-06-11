package app.lawnchair.lawnicons.model

import kotlinx.collections.immutable.ImmutableList

/**
 * Data class to hold information about icons.
 *
 * @property iconInfo A list of `IconInfo` objects.
 * @property iconCount The total number of icons.
 */
data class IconInfoModel(
    val iconInfo: ImmutableList<IconInfo>,
    val iconCount: Int,
)

data class IconInfoGroupedModel(
    val iconInfo: ImmutableList<IconInfoGrouped>,
    val iconCount: Int,
)
