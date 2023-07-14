package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.components.core.TopBarWithInsets
import app.lawnchair.lawnicons.ui.components.home.ClickableIcon
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import app.lawnchair.lawnicons.viewmodel.AcknowledgementsViewModel

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun Acknowledgements(
    acknowledgementsViewModel: AcknowledgementsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val ossLibraries by acknowledgementsViewModel.ossLibraries.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

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
        },
    ) { innerPadding ->
        Crossfade(
            targetState = ossLibraries,
            modifier = Modifier.padding(innerPadding),
        ) { libraries ->
            LazyColumn(
                contentPadding = WindowInsets.navigationBars.toPaddingValues(
                    additionalTop = 8.dp,
                    additionalBottom = 8.dp,
                ),
            ) {
                itemsIndexed(libraries) { index, it ->
                    SimpleListRow(
                        label = it.name,
                        first = index == 0,
                        background = true,
                        last = index == libraries.lastIndex,
                        divider = index != libraries.lastIndex,
                        onClick = {
                            navController.navigate("${Destinations.ACKNOWLEDGEMENT}/${it.name}")
                        },
                    )
                }
            }
        }
    }
}
