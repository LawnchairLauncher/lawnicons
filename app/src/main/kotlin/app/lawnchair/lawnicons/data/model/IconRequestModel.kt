package app.lawnchair.lawnicons.data.model

import java.io.File

/**
 * Represents a request for a list of system icons.
 *
 * @property list A list of [SystemIconInfo] objects representing the icons.
 * @property iconCount The total number of icons in the list.
 */
data class IconRequestModel(
    val list: List<SystemIconInfo>,
    val iconCount: Int,
)

data class IconRequestData(
    val zipFile: File,
    val componentListString: String,
)
