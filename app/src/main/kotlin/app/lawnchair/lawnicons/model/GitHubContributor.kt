package app.lawnchair.lawnicons.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class GitHubContributor(
    val id: Int,
    val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("html_url") val htmlUrl: String,
    val contributions: Int
)
