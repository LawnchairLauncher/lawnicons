package app.lawnchair.lawnicons.data.api

import app.lawnchair.lawnicons.data.model.GitHubContributor
import retrofit2.http.GET

interface GitHubContributorsAPI {

    @GET("repos/LawnchairLauncher/lawnicons/contributors")
    suspend fun getContributors(): List<GitHubContributor>
}
