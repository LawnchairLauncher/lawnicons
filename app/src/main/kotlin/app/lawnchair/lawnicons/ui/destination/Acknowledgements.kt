package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.viewmodel.AcknowledgementViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Acknowledgements

fun NavGraphBuilder.acknowledgementsDestination(
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
) {
    composable<Acknowledgements> {
        Acknowledgements(
            onBack = onBack,
            onNavigate = onNavigate,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@Composable
private fun Acknowledgements(
    onBack: () -> Unit,
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    acknowledgementsViewModel: AcknowledgementViewModel = hiltViewModel(),
) {
    val ossLibraries by acknowledgementsViewModel.ossLibraries.collectAsStateWithLifecycle()

    LawniconsScaffold(
        modifier = modifier,
        title = stringResource(id = R.string.acknowledgements),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
    ) { paddingValues ->
        Crossfade(
            targetState = ossLibraries,
            label = "",
        ) { libraries ->
            val layoutDirection = LocalLayoutDirection.current
            val verticalListPadding = 8.dp
            LazyColumn(
                contentPadding = PaddingValues(
                    start = paddingValues.calculateStartPadding(layoutDirection),
                    top = paddingValues.calculateTopPadding() + verticalListPadding,
                    end = paddingValues.calculateEndPadding(layoutDirection),
                    bottom = paddingValues.calculateBottomPadding() + verticalListPadding,
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
                            onNavigate(it.name)
                        },
                    )
                }
            }
        }
    }
}
