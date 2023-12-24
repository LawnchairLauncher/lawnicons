package app.lawnchair.lawnicons.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubContributor(
    val id: Int,
    val login: String,
    @SerialName("avatar_url") val avatarUrl: String,
    @SerialName("html_url") val htmlUrl: String,
    val contributions: Int,
)
