package app.lawnchair.lawnicons.repository

import app.lawnchair.lawnicons.api.GitHubContributorsAPI
import javax.inject.Inject

val coreContributorIds = listOf(
    // Remove Patryk from contributors list, as per https://t.me/lawnchairci/1557
    29139614,
    // Remove renovate-bot from contributors list, since we don't count bots as contributors
    56888459,
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
)

class GitHubContributorsRepository @Inject constructor(
    private val api: GitHubContributorsAPI,
) {
    suspend fun getTopContributors() = api.getContributors()
        .filterNot { coreContributorIds.contains(it.id) }
        .sortedByDescending { it.contributions }
}
