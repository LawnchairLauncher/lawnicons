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

package app.lawnchair.lawnicons.repository

import android.app.Application
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.util.getIconInfo
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

interface NewIconsRepository {
    val newIconsInfoModel: StateFlow<IconInfoModel>
}

class NewIconsRepositoryImpl @Inject constructor(application: Application) : NewIconsRepository {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private val prefs = PreferenceManager.getInstance(application)

    private val _newIconsInfoModel = MutableStateFlow(IconInfoModel())
    override val newIconsInfoModel = _newIconsInfoModel.asStateFlow()

    init {
        val currentVersionCode = prefs.currentLawniconsVersion.get()
        val newVersionCode = BuildConfig.VERSION_CODE

        if (currentVersionCode != newVersionCode) {
            prefs.currentLawniconsVersion.set(newVersionCode)
            prefs.showNewIconsCard.set(true)
        }

        coroutineScope.launch {
            val iconInfo = application.getIconInfo(R.xml.appfilter_diff).sortedBy { it.label.lowercase() }
            _newIconsInfoModel.value = IconInfoModel(
                iconInfo,
                iconInfo.size,
            )
        }
    }
}
