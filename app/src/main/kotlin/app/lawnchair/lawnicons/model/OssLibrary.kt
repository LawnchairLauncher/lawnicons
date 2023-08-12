package app.lawnchair.lawnicons.model

import kotlinx.serialization.Serializable

@Serializable
data class OssLibrary(
    val groupId: String,
    val artifactId: String,
    val name: String,
    val spdxLicenses: List<License>? = null,
    val unknownLicenses: List<License>? = null,
) {
    @Serializable
    data class License(
        val url: String,
    )
}
