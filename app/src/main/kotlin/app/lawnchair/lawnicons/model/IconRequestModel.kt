package app.lawnchair.lawnicons.model

import kotlinx.collections.immutable.ImmutableList

data class IconRequest(
    val label: String,
    val componentName: String,
)

data class IconRequestModel(
    val list: ImmutableList<IconRequest>,
    val iconCount: Int,
)
