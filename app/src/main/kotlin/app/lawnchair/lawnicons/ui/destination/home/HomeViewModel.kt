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

package app.lawnchair.lawnicons.ui.destination.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.model.Announcement
import app.lawnchair.lawnicons.data.model.AnnouncementLocation
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.model.SearchMode
import app.lawnchair.lawnicons.data.repository.NewIconsRepository
import app.lawnchair.lawnicons.data.repository.home.AnnouncementsRepository
import app.lawnchair.lawnicons.data.repository.home.IconRepository
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepository
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val iconInfoModel: IconInfoModel,
        val searchedIconInfoModel: IconInfoModel,
        val announcements: List<Announcement>,
        val hasNewIcons: Boolean,
        val hasIconRequests: Boolean,
        val searchMode: SearchMode,
    ) : HomeUiState
}

interface HomeViewModel {
    val uiState: StateFlow<HomeUiState>
    val searchTermTextState: TextFieldState

    fun searchIcons(query: String)
    fun changeMode(mode: SearchMode)
    fun clearSearch()
}

@ViewModelKey(HomeViewModelImpl::class)
@ContributesIntoMap(LawniconsScope::class, binding = binding<ViewModel>())
class HomeViewModelImpl(
    private val iconRepository: IconRepository,
    private val newIconsRepository: NewIconsRepository,
    private val iconRequestRepository: IconRequestRepository,
    private val announcementsRepository: AnnouncementsRepository,
) : ViewModel(),
    HomeViewModel {

    private val searchMode = MutableStateFlow(SearchMode.LABEL)

    private val announcementsFlow = flow {
        val result = runCatching {
            announcementsRepository.getAnnouncements()
                .filter { it.location == AnnouncementLocation.Home }
        }.getOrDefault(emptyList())
        emit(result)
    }

    override val searchTermTextState = TextFieldState()

    override val uiState: StateFlow<HomeUiState> = combine(
        iconRepository.iconInfoModel,
        iconRepository.searchedIconInfoModel,
        newIconsRepository.newIconsInfoModel,
        iconRequestRepository.iconRequestList,
        announcementsFlow,
        searchMode,
    ) { iconInfo, searched, newIcons, requests, announcements, mode ->
        if (iconInfo.iconInfo.isEmpty()) {
            HomeUiState.Loading
        } else {
            HomeUiState.Success(
                iconInfoModel = iconInfo,
                searchedIconInfoModel = searched,
                announcements = announcements,
                hasNewIcons = newIcons.iconCount > 0,
                hasIconRequests = !requests?.list.isNullOrEmpty(),
                searchMode = mode,
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState.Loading,
    )

    override fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(searchMode.value, searchTermTextState.text.toString())
        }
    }

    override fun changeMode(mode: SearchMode) {
        searchMode.value = mode
        viewModelScope.launch {
            iconRepository.search(searchMode.value, searchTermTextState.text.toString())
        }
    }

    override fun clearSearch() {
        viewModelScope.launch {
            iconRepository.clearSearch()
        }
    }
}

private fun <T1, T2, T3, T4, T5, T6, R> combine(
    flow: Flow<T1>,
    flow2: Flow<T2>,
    flow3: Flow<T3>,
    flow4: Flow<T4>,
    flow5: Flow<T5>,
    flow6: Flow<T6>,
    transform: suspend (T1, T2, T3, T4, T5, T6) -> R,
): Flow<R> = combine(flow, flow2, flow3, flow4, flow5, flow6) { args: Array<*> ->
    @Suppress("UNCHECKED_CAST")
    transform(
        args[0] as T1,
        args[1] as T2,
        args[2] as T3,
        args[3] as T4,
        args[4] as T5,
        args[5] as T6,
    )
}
