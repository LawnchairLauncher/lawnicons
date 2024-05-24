package app.lawnchair.lawnicons.model

import kotlinx.collections.immutable.ImmutableList

/**
 * Data class to hold information about icons.
 *
 * @property iconInfo A list of `IconInfoAppfilter` objects.
 * @property iconCount The total number of icons.
 */
data class IconInfoModel(
    val iconInfo: ImmutableList<IconInfoAppfilter>,
    val iconCount: Int,
)
