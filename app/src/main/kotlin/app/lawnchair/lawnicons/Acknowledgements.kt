package app.lawnchair.lawnicons

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.ossnotices.OssLibrary
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsHeight

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
fun Acknowledgements(
    acknowledgementsViewModel: AcknowledgementsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val ossLibraries by acknowledgementsViewModel.ossLibraries.collectAsState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }
    val topBarColors = TopAppBarDefaults.smallTopAppBarColors()
    val scrollFraction = scrollBehavior.scrollFraction
    val statusBarColor by topBarColors.containerColor(scrollFraction)
    val density = LocalDensity.current
    val navigationBarHeight = with (density) { LocalWindowInsets.current.navigationBars.bottom.toDp() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            Column {
                Spacer(
                    modifier = Modifier
                        .statusBarsHeight()
                        .fillMaxWidth()
                        .background(statusBarColor)
                )
                SmallTopAppBar(
                    title = { Text(text = stringResource(id = R.string.acknowledgements)) },
                    scrollBehavior = scrollBehavior,
                    navigationIcon = {
                        ClickableIcon(
                            onClick = { navController.popBackStack() },
                            imageVector = Icons.Rounded.ArrowBack,
                            size = 40.dp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                )
            }
        }
    ) { innerPadding ->
        Crossfade(
            targetState = ossLibraries,
            modifier = Modifier.padding(innerPadding)
        ) {
            if (it != null) {
                LazyColumn(contentPadding = PaddingValues(bottom = navigationBarHeight + 16.dp)) {
                    items(it) {
                        ListItem(
                            text = {
                                Text(
                                    text = it.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
