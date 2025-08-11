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

package app.lawnchair.lawnicons.data.repository.home

import android.app.Application
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.model.SearchInfo
import app.lawnchair.lawnicons.data.model.SearchMode
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IconRepository {
    val iconInfoModel: StateFlow<IconInfoModel>
    val searchedIconInfoModel: StateFlow<IconInfoModel>

    suspend fun search(mode: SearchMode, query: String)
    fun clearSearch()
}

class IconRepositoryImpl @Inject constructor(application: Application) : IconRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _iconInfoModel = MutableStateFlow(IconInfoModel())
    override val iconInfoModel = _iconInfoModel.asStateFlow()

    private val _searchedIconInfoModel = MutableStateFlow(IconInfoModel())
    override val searchedIconInfoModel = _searchedIconInfoModel.asStateFlow()

    init {
        coroutineScope.launch {
            val iconList = application.getIconInfo().sortedBy { it.label.lowercase() }
            val groupedIcons = iconList.associateBy { it.label }.values
            val iconCount = groupedIcons.size

            _iconInfoModel.value = IconInfoModel(
                iconInfo = iconList,
                iconCount = iconCount,
            )
            _searchedIconInfoModel.value = _iconInfoModel.value
        }
    }

    override suspend fun search(
        mode: SearchMode,
        query: String,
    ) = withContext(Dispatchers.Default) {
        val filteredIcons = _iconInfoModel.value.iconInfo.mapNotNull { candidate ->
            val searchIn = when (mode) {
                SearchMode.LABEL -> candidate.componentNames.map { it.label }
                SearchMode.COMPONENT -> candidate.componentNames.map { it.componentName.flattenToString() }
                SearchMode.DRAWABLE -> listOf(candidate.drawableName)
            }
            val indexOfMatch = searchIn.map {
                it.indexOf(string = query, ignoreCase = true)
            }.filter { it != -1 }.minOrNull() ?: return@mapNotNull null
            val matchAtWordStart = searchIn.any {
                it.indexOf(string = query, ignoreCase = true) == 0 ||
                    it.getOrNull(it.indexOf(string = query, ignoreCase = true) - 1) == ' '
            }
            SearchInfo(
                iconInfo = candidate,
                indexOfMatch = indexOfMatch,
                matchAtWordStart = matchAtWordStart,
            )
        }.sortedWith(
            compareBy(
                { searchInfo -> !searchInfo.matchAtWordStart },
                { searchInfo -> searchInfo.indexOfMatch },
            ),
        ).map { searchInfo ->
            searchInfo.iconInfo
        }

        _searchedIconInfoModel.value = IconInfoModel(
            iconCount = _searchedIconInfoModel.value.iconCount,
            iconInfo = filteredIcons,
        )
    }

    override fun clearSearch() {
        _searchedIconInfoModel.value = _iconInfoModel.value
    }
}
