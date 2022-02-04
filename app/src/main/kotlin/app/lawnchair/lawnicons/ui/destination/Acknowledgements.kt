package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.component.AcknowledgementRowPlaceholder
import app.lawnchair.lawnicons.ui.component.ClickableIcon
import app.lawnchair.lawnicons.ui.component.SimpleListRow
import app.lawnchair.lawnicons.ui.component.TopBarWithInsets
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.viewmodel.AcknowledgementsViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
fun Acknowledgements(
    acknowledgementsViewModel: AcknowledgementsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val ossLibraries by acknowledgementsViewModel.ossLibraries.collectAsState()
    val scrollBehavior = remember { TopAppBarDefaults.pinnedScrollBehavior() }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopBarWithInsets(
                scrollBehavior = scrollBehavior,
                title = stringResource(id = R.string.acknowledgements),
                navigationIcon = {
                    ClickableIcon(
                        onClick = { navController.popBackStack() },
                        imageVector = Icons.Rounded.ArrowBack,
                        size = 40.dp,
                        modifier = Modifier.padding(horizontal = 4.dp),
                    )
                },
            )
        }
    ) { innerPadding ->
        Crossfade(
            targetState = ossLibraries,
            modifier = Modifier.padding(innerPadding)
        ) { libraries ->
            LazyColumn(
                contentPadding = rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                    additionalTop = 8.dp,
                    additionalBottom = 8.dp,
                )
            ) {
                if (libraries != null) {
                    itemsIndexed(libraries) { index, it ->
                        SimpleListRow(
                            label = it.name,
                            first = index == 0,
                            background = true,
                            last = index == libraries.lastIndex,
                            divider = index != libraries.lastIndex,
                            onClick = {
                                navController.navigate("${Destinations.ACKNOWLEDGEMENT}/${it.name}")
                            }
                        )
                    }
                } else {
                    val itemCount = 20
                    items(itemCount) {
                        AcknowledgementRowPlaceholder(
                            first = it == 0,
                            last = it == itemCount - 1,
                            divider = it < itemCount - 1,
                        )
                    }
                }
            }
        }
    }
}
