package app.lawnchair.lawnicons.ui.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.GitHubContributor
import app.lawnchair.lawnicons.ui.components.ContributorRow
import app.lawnchair.lawnicons.ui.components.ContributorRowPlaceholder
import app.lawnchair.lawnicons.ui.components.ExternalLinkRow
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import app.lawnchair.lawnicons.viewmodel.ContributorsUiState
import app.lawnchair.lawnicons.viewmodel.ContributorsViewModel

const val contributorsUrl = "https://github.com/LawnchairLauncher/lawnicons/graphs/contributors"

@Composable
fun Contributors(
    contributorsViewModel: ContributorsViewModel = hiltViewModel(),
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
) {
    val uiState by contributorsViewModel.uiState.collectAsState()
    Contributors(
        uiState = uiState,
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    )
}

@Composable
fun Contributors(
    uiState: ContributorsUiState,
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
) {
    LawniconsScaffold(
        title = stringResource(id = R.string.contributors),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { paddingValues ->
        Crossfade(
            targetState = uiState,
            modifier = Modifier.padding(paddingValues = paddingValues),
            label = "",
        ) {
            when (it) {
                is ContributorsUiState.Success -> ContributorList(contributors = it.contributors)
                is ContributorsUiState.Loading -> ContributorListPlaceholder()
                is ContributorsUiState.Error -> ContributorListError(onBack = onBack)
            }
        }
    }
}

@Composable
fun ContributorList(contributors: List<GitHubContributor>) {
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.toPaddingValues(
            additionalTop = 8.dp,
            additionalBottom = 8.dp,
        ),
    ) {
        item {
            ExternalLinkRow(
                name = stringResource(R.string.view_on_github),
                background = true,
                first = true,
                last = true,
                divider = false,
                url = contributorsUrl,
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
fun ContributorListPlaceholder() {
    val itemCount = 20
    LazyColumn(
        contentPadding = WindowInsets.navigationBars.toPaddingValues(
            additionalTop = 8.dp,
            additionalBottom = 8.dp,
        ),
    ) {
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
fun ContributorListError(
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    SideEffect {
        onBack()
        // we might be rate-limited, open the web ui instead
        val website =
            Uri.parse(contributorsUrl)
        val intent = Intent(Intent.ACTION_VIEW, website)
        context.startActivity(intent)
    }
}
