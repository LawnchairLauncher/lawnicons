package app.lawnchair.lawnicons.data.repository

import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.api.GitHubContributorsAPI
import app.lawnchair.lawnicons.ui.destination.about.coreContributorIds
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@SingleIn(LawniconsScope::class)
@Inject
class GitHubContributorsRepository(
    private val api: GitHubContributorsAPI,
) {
    suspend fun getTopContributors() = api.getContributors()
        .filterNot { coreContributorIds.contains(it.id) }
        .sortedByDescending { it.contributions }
}
