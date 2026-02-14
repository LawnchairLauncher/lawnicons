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

package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.ui.LocalLawniconsActions
import app.lawnchair.lawnicons.ui.components.home.search.ResponsiveSearchBarContents
import app.lawnchair.lawnicons.ui.components.home.search.SearchBarInputField
import app.lawnchair.lawnicons.ui.components.home.search.SearchContents
import app.lawnchair.lawnicons.ui.components.home.search.SearchState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    searchState: SearchState,
    iconInfoModel: IconInfoModel?,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    val actions = LocalLawniconsActions.current
    val coroutineScope = rememberCoroutineScope()

    val targetOffsetY = if (searchState.searchBarState.targetValue == SearchBarValue.Expanded) {
        0
    } else {
        (-100)
    }.dp

    val offsetY by animateDpAsState(
        targetValue = targetOffsetY,
        label = "SearchBarOffsetYAnimation",
    )

    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = modifier.fillMaxWidth(),
    ) {
        iconInfoModel?.let {
            val inputField = @Composable {
                SearchBarInputField(
                    state = searchState,
                    placeholder = {
                        Text(
                            stringResource(
                                id = if (actions.isIconPicker) {
                                    R.string.search_bar_icon_picker
                                } else {
                                    R.string.search_bar_hint
                                },
                                iconInfoModel.iconCount,
                            ),
                        )
                    },
                    onBack = {
                        coroutineScope.launch {
                            searchState.searchBarState.animateToCollapsed()
                        }
                    },
                )
            }

            AppBarWithSearch(
                state = searchState.searchBarState,
                inputField = inputField,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
                    .offset(y = offsetY),
            )

            ResponsiveSearchBarContents(
                isExpandedScreen = isExpandedScreen,
                state = searchState.searchBarState,
                inputField = inputField,
            ) {
                SearchContents(
                    state = searchState,
                    iconInfo = iconInfoModel.iconInfo,
                    onSendResult = actions.onSendResult,
                )
            }
        }
    }
}
