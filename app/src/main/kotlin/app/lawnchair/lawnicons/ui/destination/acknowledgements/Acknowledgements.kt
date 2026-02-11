/*
 * Copyright 2025 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.ui.destination.acknowledgements

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.util.visitUrl
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.serialization.Serializable

@Serializable
data object Acknowledgements : NavKey

fun EntryProviderScope<NavKey>.acknowledgementsDestination(
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
) {
    entry<Acknowledgements> {
        Acknowledgements(
            onBack = onBack,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Acknowledgements(
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    acknowledgementsViewModel: AcknowledgementsViewModel = metroViewModel(),
) {
    val context = LocalContext.current
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
                        description = it.spdxLicenses.first().name,
                        divider = index != libraries.lastIndex,
                        background = true,
                        shapes = ListItemDefaults.segmentedShapes(index, libraries.size),
                    ) {
                        context.visitUrl(it.scm.url)
                    }
                }
            }
        }
    }
}
