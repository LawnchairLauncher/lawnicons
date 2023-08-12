package app.lawnchair.lawnicons.model

import kotlinx.serialization.Serializable

@Serializable
data class OssLibrary(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String,
    val scm: Scm? = null,
    val spdxLicenses: List<License>? = null,
    val unknownLicenses: List<License>? = null,
) {
    @Serializable
    data class License(
        val identifier: String? = null,
        val name: String,
        val url: String,
    )

    @Serializable
    data class Scm(val url: String)
}
