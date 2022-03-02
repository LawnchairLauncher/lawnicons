package app.lawnchair.lawnicons.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.model.GitHubContributor
import app.lawnchair.lawnicons.repository.GitHubContributorsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ContributorsUiState {

    data class Success(
        val contributors: List<GitHubContributor>
    ) : ContributorsUiState

    object Loading : ContributorsUiState
    object Error : ContributorsUiState
}

private data class ContributorsViewModelState(
    val isRefreshing: Boolean,
    val contributors: List<GitHubContributor>? = null,
    val hasError: Boolean = false
) {
    fun toUiState(): ContributorsUiState = when {
        hasError -> ContributorsUiState.Error
        contributors != null -> ContributorsUiState.Success(contributors)
        else -> ContributorsUiState.Loading
    }
}

@HiltViewModel
class ContributorsViewModel @Inject constructor(
    private val repository: GitHubContributorsRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(ContributorsViewModelState(isRefreshing = true))
    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    init {
        viewModelState.update { it.copy(isRefreshing = true) }

        viewModelScope.launch {
            val result = runCatching {
                repository.getTopContributors()
            }
            viewModelState.update {
                when {
                    result.isSuccess -> it.copy(
                        isRefreshing = false,
                        contributors = result.getOrThrow(),
                        hasError = false
                    )
                    else -> {
                        Log.e(
                            "ContributorsViewModel",
                            "Failed to load contributors",
                            result.exceptionOrNull()
                        )
                        it.copy(
                            isRefreshing = false,
                            hasError = true
                        )
                    }
                }
            }
        }
    }
}
