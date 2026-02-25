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
        val announcements: List<Announcement>,
        val hasNewIcons: Boolean,
        val hasIconRequests: Boolean,
    ) : HomeUiState
}

interface HomeViewModel {
    val uiState: StateFlow<HomeUiState>

    val searchResults: StateFlow<IconInfoModel>
    val searchMode: StateFlow<SearchMode>
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

    private val _searchMode = MutableStateFlow(SearchMode.LABEL)
    override val searchMode = _searchMode.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        SearchMode.LABEL,
    )

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
        newIconsRepository.newIconsInfoModel,
        iconRequestRepository.iconRequestList,
        announcementsFlow,
    ) { iconInfo, newIcons, requests, announcements ->
        if (iconInfo.iconInfo.isEmpty()) {
            HomeUiState.Loading
        } else {
            HomeUiState.Success(
                iconInfoModel = iconInfo,
                announcements = announcements,
                hasNewIcons = newIcons.iconCount > 0,
                hasIconRequests = !requests?.list.isNullOrEmpty(),
            )
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        HomeUiState.Loading,
    )

    override val searchResults: StateFlow<IconInfoModel> = iconRepository
        .searchedIconInfoModel
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            IconInfoModel(emptyList(), 0),
        )

    override fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(_searchMode.value, searchTermTextState.text.toString())
        }
    }

    override fun changeMode(mode: SearchMode) {
        _searchMode.value = mode
        viewModelScope.launch {
            iconRepository.search(_searchMode.value, searchTermTextState.text.toString())
        }
    }

    override fun clearSearch() {
        viewModelScope.launch {
            iconRepository.clearSearch()
        }
    }
}
