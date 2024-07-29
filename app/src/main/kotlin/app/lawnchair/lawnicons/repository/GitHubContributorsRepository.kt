package app.lawnchair.lawnicons.repository

import app.lawnchair.lawnicons.api.GitHubContributorsAPI
import javax.inject.Inject

val coreContributorIds = listOf(
    29139614, // Remove Patryk from contributors list, as per https://t.me/lawnchairci/1557
    56888459, // Remove renovate-bot from contributors list, since we don't count botss as contributors
    8080853, // suphon-t
    70206496, // SuperDragonXD
    60105060, // x9136
    10363352, // Goooler
    49114212, // Grabstertv
)

class GitHubContributorsRepository @Inject constructor(
    private val api: GitHubContributorsAPI,
) {
    suspend fun getTopContributors() = api.getContributors()
        .filterNot { coreContributorIds.contains(it.id) }
        .sortedByDescending { it.contributions }
}
