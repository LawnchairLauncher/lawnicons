/*
 * Copyright 2024 Lawnchair Launcher
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

package app.lawnchair.lawnicons.data.repository

import android.app.Application
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.repository.home.getIconInfo
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface NewIconsRepository {
    val newIconsInfoModel: StateFlow<IconInfoModel>
}

@SingleIn(LawniconsScope::class)
@ContributesBinding(LawniconsScope::class)
@Inject
class NewIconsRepositoryImpl(application: Application) : NewIconsRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _newIconsInfoModel = MutableStateFlow(IconInfoModel())
    override val newIconsInfoModel = _newIconsInfoModel.asStateFlow()

    init {
        coroutineScope.launch {
            val iconInfo = application.getIconInfo(R.xml.appfilter_diff).sortedBy { it.label.lowercase() }
            _newIconsInfoModel.value = IconInfoModel(
                iconInfo,
                iconInfo.size,
            )
        }
    }
}
