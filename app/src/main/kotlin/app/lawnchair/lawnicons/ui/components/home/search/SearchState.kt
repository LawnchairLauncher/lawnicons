/*
 * Copyright 2026 Lawnchair Launcher
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

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SearchBarState
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lawnchair.lawnicons.data.model.SearchMode

@OptIn(ExperimentalMaterial3Api::class)
class SearchState(
    val textFieldState: TextFieldState,
    val searchBarState: SearchBarState,
    val mode: SearchMode,
    private val onModeChange: (SearchMode) -> Unit,
    private val onExpandChanged: (Boolean) -> Unit,
) {
    var isSheetVisible by mutableStateOf(false)

    val searchTerm: String get() = textFieldState.text.toString()

    fun setMode(newMode: SearchMode) {
        onModeChange(newMode)
    }

    fun setExpanded(expanded: Boolean) {
        onExpandChanged(expanded)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberSearchState(
    textFieldState: TextFieldState = remember { TextFieldState() },
    searchBarState: SearchBarState = rememberSearchBarState(),
    mode: SearchMode = SearchMode.LABEL,
    onModeChange: (SearchMode) -> Unit = {},
    onExpandChange: (Boolean) -> Unit = {},
): SearchState {
    return remember(textFieldState, searchBarState, mode) {
        SearchState(
            textFieldState = textFieldState,
            searchBarState = searchBarState,
            mode = mode,
            onModeChange = onModeChange,
            onExpandChanged = onExpandChange,
        )
    }
}
