package app.lawnchair.lawnicons.data.repository

import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.api.GitHubContributorsAPI
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

val coreContributorIds = listOf(
    // Remove Patryk from contributors list, as per https://t.me/lawnchairci/1557
    29139614,
    // Remove renovate-bot from contributors list, since we don't count bots as contributors
    56888459,
    // GitHub Actions bot
    41898282,
    // suphon-t
    8080853,
    // SuperDragonXD
    70206496,
    // Chefski
    100310118,
    // x9136
    60105060,
    // Goooler
    10363352,
    // Grabstertv
    49114212,
    // Bot
    198982749,
    // Bot
    175728472,
)

@SingleIn(LawniconsScope::class)
@Inject
class GitHubContributorsRepository(
    private val api: GitHubContributorsAPI,
) {
    suspend fun getTopContributors() = api.getContributors()
        .filterNot { coreContributorIds.contains(it.id) }
        .sortedByDescending { it.contributions }
}
