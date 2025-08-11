package app.lawnchair.lawnicons.data.model

import kotlinx.serialization.Serializable

@Serializable
data class OssLibrary(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String,
    val spdxLicenses: List<License>,
) {
    @Serializable
    data class License(
        val name: String,
        val url: String,
    )
}
