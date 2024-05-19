package app.lawnchair.lawnicons.model

import kotlinx.collections.immutable.ImmutableList

data class IconInfoModel(
    val iconInfo: ImmutableList<IconInfoAppfilter>,
    val iconCount: Int,
)
