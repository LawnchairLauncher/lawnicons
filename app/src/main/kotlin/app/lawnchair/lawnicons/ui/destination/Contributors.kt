package app.lawnchair.lawnicons.ui.destination

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.GitHubContributor
import app.lawnchair.lawnicons.ui.component.ClickableIcon
import app.lawnchair.lawnicons.ui.component.ContributorRow
import app.lawnchair.lawnicons.ui.component.ContributorRowPlaceholder
import app.lawnchair.lawnicons.ui.component.TopBarWithInsets
import app.lawnchair.lawnicons.viewmodel.ContributorsUiState
import app.lawnchair.lawnicons.viewmodel.ContributorsViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun Contributors(
    contributorsViewModel: ContributorsViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by contributorsViewModel.uiState.collectAsState()
    Contributors(
        uiState = uiState,
        onBack = navController::popBackStack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Contributors(
    uiState: ContributorsUiState,
    onBack: () -> Unit
) {
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarWithInsets(
                scrollBehavior = scrollBehavior,
                title = stringResource(id = R.string.contributors),
                navigationIcon = {
                    ClickableIcon(
                        onClick = onBack,
                        imageVector = Icons.Rounded.ArrowBack,
                        size = 40.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
            )
        }
    ) {
        when (uiState) {
            is ContributorsUiState.Success -> ContributorList(contributors = uiState.contributors)
            is ContributorsUiState.Loading -> ContributorListPlaceholder()
            is ContributorsUiState.Error -> ContributorListError(onBack = onBack)
        }
    }
}

@Composable
fun ContributorList(contributors: List<GitHubContributor>) {
    LazyColumn(
        contentPadding = rememberInsetsPaddingValues(
            LocalWindowInsets.current.navigationBars
        )
    ) {
        items(contributors) {
            ContributorRow(
                name = it.login,
                photoUrl = it.avatarUrl,
                profileUrl = it.htmlUrl
            )
        }
    }
}

@Composable
fun ContributorListPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        for (i in 0 until 20) {
            ContributorRowPlaceholder()
        }
    }
}

@Composable
fun ContributorListError(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    SideEffect {
        onBack()
        // we might be rate-limited, open the web ui instead
        val website = Uri.parse("https://github.com/LawnchairLauncher/lawnicons/graphs/contributors")
        val intent = Intent(Intent.ACTION_VIEW, website)
        context.startActivity(intent)
    }
}
