package app.lawnchair.lawnicons.model

import androidx.annotation.Keep

@Keep
data class OssLibrary(
    val groupId: String,
    val artifactId: String,
    val version: String,
    val name: String,
    val scm: Scm? = null,
    val spdxLicenses: List<License>? = null,
    val unknownLicenses: List<License>? = null,
) {
    @Keep
    data class License(
        val identifier: String? = null,
        val name: String,
        val url: String,
    )

    @Keep
    data class Scm(val url: String)
}
