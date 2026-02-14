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

import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.SampleData
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

interface HomeViewModel {
    val iconInfoModel: StateFlow<IconInfoModel>
    val searchedIconInfoModel: StateFlow<IconInfoModel>

    val hasNewIcons: StateFlow<Boolean>
    var hasIconRequests: StateFlow<Boolean>
    val announcements: StateFlow<List<Announcement>>

    var expandSearch: Boolean

    val searchMode: SearchMode
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
    override val iconInfoModel = iconRepository.iconInfoModel
    override val searchedIconInfoModel = iconRepository.searchedIconInfoModel

    override val hasNewIcons = newIconsRepository.newIconsInfoModel.map {
        it.iconCount > 0
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false,
    )

    override var hasIconRequests = iconRequestRepository.iconRequestList
        .map { !it?.list.isNullOrEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false,
        )

    private val _announcements = MutableStateFlow<List<Announcement>>(emptyList())
    override val announcements = _announcements.asStateFlow()

    override var expandSearch by mutableStateOf(false)

    private var _searchMode by mutableStateOf(SearchMode.LABEL)

    override val searchMode: SearchMode
        get() = _searchMode

    override val searchTermTextState = TextFieldState()

    init {
        viewModelScope.launch {
            runCatching {
                announcementsRepository.getAnnouncements().filter {
                    it.location == AnnouncementLocation.Home
                }
            }.onSuccess {
                _announcements.value = it
            }.onFailure {
                Log.e(
                    "LawniconsViewModel",
                    "Failed to load announcements",
                    it,
                )
            }
        }
    }

    override fun searchIcons(query: String) {
        viewModelScope.launch {
            iconRepository.search(searchMode, searchTermTextState.text.toString())
        }
    }

    override fun changeMode(mode: SearchMode) {
        _searchMode = mode
        viewModelScope.launch {
            iconRepository.search(searchMode, searchTermTextState.text.toString())
        }
    }

    override fun clearSearch() {
        viewModelScope.launch {
            iconRepository.clearSearch()
        }
    }
}

class DummyLawniconsViewModel : HomeViewModel {
    private val list = SampleData.iconInfoList

    override val iconInfoModel = MutableStateFlow(IconInfoModel(iconInfo = list, iconCount = list.size)).asStateFlow()
    override val searchedIconInfoModel = MutableStateFlow(IconInfoModel(iconInfo = list, iconCount = list.size)).asStateFlow()

    override val hasNewIcons = MutableStateFlow(true).asStateFlow()
    override var hasIconRequests = MutableStateFlow(true).asStateFlow()
    override val announcements = MutableStateFlow(
        listOf(
            Announcement(
                title = "Announcement 1",
                description = "This is the first announcement",
                icon = "ic_award",
                url = Constants.WEBSITE,
                location = AnnouncementLocation.Home,
            ),
        ),
    ).asStateFlow()

    override var expandSearch by mutableStateOf(false)

    override val searchTermTextState = TextFieldState()

    override val searchMode = SearchMode.LABEL

    override fun searchIcons(query: String) {}

    override fun changeMode(mode: SearchMode) {}

    override fun clearSearch() {}
}
