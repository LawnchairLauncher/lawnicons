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

package app.lawnchair.lawnicons.ui.destination.contributors

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.data.model.GitHubContributor
import app.lawnchair.lawnicons.data.repository.GitHubContributorsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface ContributorsUiState {

    data class Success(
        val contributors: List<GitHubContributor>,
    ) : ContributorsUiState

    data object Loading : ContributorsUiState
    data object Error : ContributorsUiState
}

private data class ContributorsViewModelState(
    val isRefreshing: Boolean,
    val contributors: List<GitHubContributor> = emptyList(),
    val hasError: Boolean = false,
) {
    fun toUiState(): ContributorsUiState = when {
        hasError -> ContributorsUiState.Error
        contributors.isNotEmpty() -> ContributorsUiState.Success(contributors)
        else -> ContributorsUiState.Loading
    }
}

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val repository: GitHubContributorsRepository,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ContributorsViewModelState(isRefreshing = true))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState(),
        )

    init {
        viewModelScope.launch {
            viewModelState.update { it.copy(isRefreshing = true) }

            runCatching {
                repository.getTopContributors()
            }.onSuccess { list ->
                viewModelState.update {
                    it.copy(
                        isRefreshing = false,
                        contributors = list,
                        hasError = false,
                    )
                }
            }.onFailure { t ->
                Log.e(
                    "ContributorsViewModel",
                    "Failed to load contributors",
                    t,
                )
                viewModelState.update {
                    it.copy(
                        isRefreshing = false,
                        hasError = true,
                    )
                }
            }
        }
    }
}
