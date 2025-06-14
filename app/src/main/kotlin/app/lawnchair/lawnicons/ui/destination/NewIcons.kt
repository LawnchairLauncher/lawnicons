/*
 * Copyright 2024 Lawnchair Launcher
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

package app.lawnchair.lawnicons.ui.destination

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGridPadding
import app.lawnchair.lawnicons.viewmodel.NewIconsViewModel
import kotlinx.serialization.Serializable

@Serializable
data object NewIcons

fun NavGraphBuilder.newIconsDestination(
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
) {
    composable<NewIcons> {
        NewIcons(
            onBack = onBack,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun NewIcons(
    onBack: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    newIconsViewModel: NewIconsViewModel = hiltViewModel(),
) {
    val iconInfoModel by newIconsViewModel.newIconsInfoModel.collectAsStateWithLifecycle()

    LawniconsScaffold(
        modifier = modifier,
        title = stringResource(R.string.new_icons, iconInfoModel.iconCount),
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
        containerColor = MaterialTheme.colorScheme.surface,
    ) { paddingValues ->
        IconPreviewGrid(
            iconInfo = iconInfoModel.iconInfo,
            onSendResult = {},
            contentPadding = IconPreviewGridPadding(
                topPadding = paddingValues.calculateTopPadding() - 12.dp,
                horizontalPadding = if (isExpandedScreen) IconPreviewGridPadding.ExpandedSize.horizontalPadding else IconPreviewGridPadding.Defaults.horizontalPadding,
            ),
        )
    }
}
