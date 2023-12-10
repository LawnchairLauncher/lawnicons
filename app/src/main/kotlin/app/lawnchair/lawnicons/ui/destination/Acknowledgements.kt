package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import app.lawnchair.lawnicons.viewmodel.AcknowledgementsViewModel

@Composable
fun Acknowledgements(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean,
    acknowledgementsViewModel: AcknowledgementsViewModel = hiltViewModel(),
) {
    val ossLibraries by acknowledgementsViewModel.ossLibraries.collectAsState()

    LawniconsScaffold(
        title = stringResource(id = R.string.acknowledgements),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { innerPadding ->
        Crossfade(
            targetState = ossLibraries,
            modifier = Modifier.padding(innerPadding),
            label = "",
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
                            onNavigate("${Destinations.ACKNOWLEDGEMENT}/${it.name}")
                        },
                    )
                }
            }
        }
    }
}
