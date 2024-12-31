package app.lawnchair.lawnicons.ui.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.GitHubContributor
import app.lawnchair.lawnicons.ui.components.ContributorRow
import app.lawnchair.lawnicons.ui.components.ContributorRowPlaceholder
import app.lawnchair.lawnicons.ui.components.ExternalLinkRow
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.viewmodel.ContributorsUiState
import app.lawnchair.lawnicons.viewmodel.ContributorsViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Contributors

fun NavGraphBuilder.contributorsDestination(
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
) {
    composable<Contributors> {
        Contributors(
            onBack = onBack,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@Composable
private fun Contributors(
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    contributorsViewModel: ContributorsViewModel = hiltViewModel(),
) {
    val uiState by contributorsViewModel.uiState.collectAsStateWithLifecycle()
    Contributors(
        uiState = uiState,
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
        modifier = modifier,
    )
}

@Composable
fun Contributors(
    uiState: ContributorsUiState,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    LawniconsScaffold(
        modifier = modifier,
        title = stringResource(id = R.string.contributors),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { paddingValues ->
        val layoutDirection = LocalLayoutDirection.current
        val verticalListPadding = 8.dp
        val innerPaddingValues = PaddingValues(
            start = paddingValues.calculateStartPadding(layoutDirection),
            top = paddingValues.calculateTopPadding() + verticalListPadding,
            end = paddingValues.calculateEndPadding(layoutDirection),
            bottom = paddingValues.calculateBottomPadding() + verticalListPadding,
        )
        Crossfade(
            targetState = uiState,
            label = "",
        ) {
            when (it) {
                is ContributorsUiState.Success -> ContributorList(
                    contributors = it.contributors,
                    contentPadding = innerPaddingValues,
                )

                is ContributorsUiState.Loading -> ContributorListPlaceholder(
                    contentPadding = innerPaddingValues,
                )

                is ContributorsUiState.Error -> ContributorListError(onBack = onBack)
            }
        }
    }
}

@Composable
private fun ContributorList(
    contributors: List<GitHubContributor>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
    ) {
        item {
            ExternalLinkRow(
                name = stringResource(R.string.view_on_github),
                background = true,
                first = true,
                last = true,
                divider = false,
                url = CONTRIBUTOR_URL,
                startIcon = {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.github_foreground),
                            contentDescription = null,
                            modifier = Modifier
                                .size(24.dp),
                        )
                    }
                },
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        itemsIndexed(contributors) { index, it ->
            ContributorRow(
                name = it.login,
                photoUrl = it.avatarUrl,
                profileUrl = it.htmlUrl,
                background = true,
                first = index == 0,
                last = index == contributors.lastIndex,
                divider = index != contributors.lastIndex,
            )
        }
    }
}

@Composable
private fun ContributorListPlaceholder(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val itemCount = 20
    LazyColumn(
        modifier = modifier,
        contentPadding = contentPadding,
        userScrollEnabled = false,
    ) {
        item {
            ContributorRowPlaceholder(
                first = true,
                last = true,
                divider = false,
            )
        }
        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
        items(itemCount) {
            ContributorRowPlaceholder(
                first = it == 0,
                last = it == itemCount - 1,
                divider = it < itemCount - 1,
            )
        }
    }
}

@Composable
private fun ContributorListError(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    SideEffect {
        onBack()
        // we might be rate-limited, open the web ui instead
        val website = Uri.parse(CONTRIBUTOR_URL)
        val intent = Intent(Intent.ACTION_VIEW, website)
        context.startActivity(intent)
    }
}

private const val CONTRIBUTOR_URL = "${Constants.GITHUB}/graphs/contributors"

@PreviewLawnicons
@Composable
private fun ContributorsScreenPreview() {
    val contributors = listOf(
        GitHubContributor(
            id = 1,
            login = "Example",
            avatarUrl = "https://google.com",
            htmlUrl = "https://google.com",
            contributions = 1,
        ),
    )

    LawniconsTheme {
        Contributors(
            ContributorsUiState.Success(contributors),
            {},
            false,
        )
    }
}

@PreviewLawnicons
@Composable
private fun ContributorsScreenLoadingPreview() {
    LawniconsTheme {
        Contributors(
            ContributorsUiState.Loading,
            {},
            false,
        )
    }
}

@PreviewLawnicons
@Composable
private fun ContributorListPreview() {
    val contributors = listOf(
        GitHubContributor(
            id = 1,
            login = "Example",
            avatarUrl = "https://google.com",
            htmlUrl = "https://google.com",
            contributions = 1,
        ),
    )

    LawniconsTheme {
        ContributorList(contributors)
    }
}

@PreviewLawnicons
@Composable
private fun ContributorListPlaceholderPreview() {
    LawniconsTheme {
        ContributorListPlaceholder()
    }
}
