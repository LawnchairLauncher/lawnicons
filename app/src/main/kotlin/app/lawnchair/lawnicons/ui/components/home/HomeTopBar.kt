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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.TopSearchBar
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.search.ResponsiveSearchBarContents
import app.lawnchair.lawnicons.ui.components.home.search.SearchBarInputField
import app.lawnchair.lawnicons.ui.components.home.search.SearchContents
import app.lawnchair.lawnicons.ui.theme.icon.About
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    textFieldState: TextFieldState,
    mode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    onBack: () -> Unit,
    onNavigate: () -> Unit,
    expandSearch: Boolean,
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    onSendResult: (IconInfo) -> Unit,
    iconInfoModel: IconInfoModel?,
    modifier: Modifier = Modifier,
) {
    Box(modifier) {
        iconInfoModel?.let {
            val searchBarState = rememberSearchBarState()
            val isIconInfoShown = rememberSaveable { mutableStateOf(false) }

            val inputField = @Composable {
                SearchBarInputField(
                    searchBarState = searchBarState,
                    textFieldState = textFieldState,
                    iconCount = iconInfoModel.iconCount,
                    isIconPicker = isIconPicker,
                    navigateContent = {
                        if (isExpandedScreen) {
                            IconButton(
                                onClick = onNavigate,
                            ) {
                                Icon(
                                    imageVector = LawnIcons.About,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .size(24.dp),
                                )
                            }
                        }
                    },
                    onBack = onBack,
                    onSearch = {
                        if (textFieldState.text.isNotEmpty()) {
                            isIconInfoShown.value = true
                        }
                    },
                )
            }

            val offset = (-120).dp
            val offsetState =
                animateDpAsState(if (searchBarState.currentValue == SearchBarValue.Expanded) 0.dp else offset)

            TopSearchBar(
                inputField = inputField,
                state = searchBarState,
                modifier = Modifier.then(
                    if (isExpandedScreen) {
                        Modifier.offset {
                            IntOffset(0, offsetState.value.roundToPx())
                        }
                    } else {
                        Modifier.offset(y = offset)
                    },
                ),
            )

            ResponsiveSearchBarContents(
                isExpandedScreen = isExpandedScreen,
                state = searchBarState,
                inputField = inputField,
            ) {
                SearchContents(
                    searchTerm = textFieldState.text.toString(),
                    searchMode = mode,
                    onModeChange = onModeChange,
                    iconInfo = iconInfoModel.iconInfo,
                    isIconPicker = isIconPicker,
                    onSendResult = onSendResult,
                    showSheet = isIconInfoShown.value,
                    onToggleSheet = { isIconInfoShown.value = it },
                )
            }

            LaunchedEffect(expandSearch) {
                if (expandSearch) {
                    searchBarState.animateToExpanded()
                } else {
                    searchBarState.animateToCollapsed()
                }
            }

            val latestOnBack by rememberUpdatedState(onBack)
            LaunchedEffect(searchBarState.currentValue) {
                if (searchBarState.currentValue == SearchBarValue.Collapsed) {
                    latestOnBack()
                }
            }
        }
    }
}
