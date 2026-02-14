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

package app.lawnchair.lawnicons.ui.components.home.search

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.ExpandedDockedSearchBar
import androidx.compose.material3.ExpandedFullScreenSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.SearchBarValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.lawnchair.lawnicons.ui.components.home.NavigationIconButton
import app.lawnchair.lawnicons.ui.theme.icon.Back
import app.lawnchair.lawnicons.ui.theme.icon.Close
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.theme.icon.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarInputField(
    state: SearchState,
    placeholder: @Composable () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SearchBarDefaults.InputField(
        textFieldState = state.textFieldState,
        searchBarState = state.searchBarState,
        onSearch = { state.isSheetVisible = true },
        placeholder = placeholder,
        leadingIcon = {
            if (state.searchBarState.targetValue == SearchBarValue.Expanded) {
                NavigationIconButton(
                    imageVector = LawnIcons.Back,
                    onClick = onBack,
                )
            } else {
                Icon(LawnIcons.Search, contentDescription = null)
            }
        },
        trailingIcon = {
            Crossfade(state.textFieldState.text.isNotEmpty(), label = "") {
                if (it) {
                    NavigationIconButton(
                        onClick = { state.textFieldState.clearText() },
                        imageVector = LawnIcons.Close,
                    )
                }
            }
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResponsiveSearchBarContents(
    isExpandedScreen: Boolean,
    state: SearchBarState,
    inputField: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (isExpandedScreen) {
        ExpandedDockedSearchBar(
            state = state,
            inputField = inputField,
            modifier = modifier,
            content = content,
        )
    } else {
        ExpandedFullScreenSearchBar(
            state = state,
            inputField = inputField,
            modifier = modifier,
            content = content,
        )
    }
}
