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
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.model.IconRequestModel
import app.lawnchair.lawnicons.data.model.SearchMode
import app.lawnchair.lawnicons.data.repository.DummySharedPreferences
import app.lawnchair.lawnicons.data.repository.NewIconsRepository
import app.lawnchair.lawnicons.data.repository.PreferenceManager
import app.lawnchair.lawnicons.data.repository.home.AnnouncementsRepository
import app.lawnchair.lawnicons.data.repository.home.IconRepository
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepository
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.SampleData
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface HomeViewModel {
    val iconInfoModel: StateFlow<IconInfoModel>
    val searchedIconInfoModel: StateFlow<IconInfoModel>
    val iconRequestModel: StateFlow<IconRequestModel?>
    val newIconsInfoModel: StateFlow<IconInfoModel>

    val preferenceManager: PreferenceManager

    var iconRequestsEnabled: Boolean
    var announcements: List<Announcement>

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
    override val preferenceManager: PreferenceManager,
) : ViewModel(),
    HomeViewModel {
    override val iconInfoModel = iconRepository.iconInfoModel
    override val searchedIconInfoModel = iconRepository.searchedIconInfoModel
    override val iconRequestModel = iconRequestRepository.iconRequestList
    override val newIconsInfoModel = newIconsRepository.newIconsInfoModel

    override var iconRequestsEnabled = false
    override var announcements = listOf<Announcement>()

    override var expandSearch by mutableStateOf(false)

    private var _searchMode by mutableStateOf(SearchMode.LABEL)

    override val searchMode: SearchMode
        get() = _searchMode

    override val searchTermTextState = TextFieldState()

    init {
        viewModelScope.launch {
            runCatching {
                iconRequestRepository.getEnabledState()
            }.onSuccess {
                iconRequestsEnabled = it
            }.onFailure {
                Log.e(
                    "LawniconsViewModel",
                    "Failed to load icon request settings",
                    it,
                )
            }
        }
        viewModelScope.launch {
            runCatching {
                announcementsRepository.getAnnouncements()
            }.onSuccess {
                announcements = it
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
    override val iconRequestModel = MutableStateFlow(IconRequestModel(list = listOf(), iconCount = 0)).asStateFlow()
    override val newIconsInfoModel = MutableStateFlow(IconInfoModel(iconInfo = list, iconCount = list.size)).asStateFlow()

    override val preferenceManager = PreferenceManager(DummySharedPreferences())

    override var iconRequestsEnabled = true
    override var announcements = listOf(
        Announcement(
            title = "Announcement 1",
            description = "This is the first announcement",
            icon = "ic_award",
            url = Constants.WEBSITE,
        ),
    )

    override var expandSearch by mutableStateOf(false)

    override val searchTermTextState = TextFieldState()

    override val searchMode = SearchMode.LABEL

    override fun searchIcons(query: String) {}

    override fun changeMode(mode: SearchMode) {}

    override fun clearSearch() {}
}
