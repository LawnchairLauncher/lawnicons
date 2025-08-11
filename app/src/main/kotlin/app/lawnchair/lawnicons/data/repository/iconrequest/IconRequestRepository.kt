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

package app.lawnchair.lawnicons.data.repository.iconrequest

import android.app.Application
import android.util.Log
import app.lawnchair.lawnicons.data.api.IconRequestSettingsAPI
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.IconRequestModel
import app.lawnchair.lawnicons.data.model.SystemIconInfo
import app.lawnchair.lawnicons.data.repository.home.getIconInfo
import java.io.File
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface IconRequestRepository {
    val iconRequestList: StateFlow<IconRequestModel?>
    suspend fun getEnabledState(): Boolean

    suspend fun createIconRequestZip(currentIconRequests: List<SystemIconInfo>?): File?
}

class IconRequestRepositoryImpl @Inject constructor(
    val application: Application,
    private val api: IconRequestSettingsAPI,
) : IconRequestRepository {
    private val coroutineScope = CoroutineScope(Dispatchers.IO)

    private val _iconRequestList = MutableStateFlow<IconRequestModel?>(null)
    override val iconRequestList = _iconRequestList.asStateFlow()

    override suspend fun getEnabledState() = api.getIconRequestSettings().enabled

    init {
        coroutineScope.launch {
            val iconList = application.getIconInfo()
                .sortedBy { it.label.lowercase() }

            val systemPackageList = application.getSystemIconInfo()
                .sortedBy { it.label.lowercase() }

            getIconRequestList(iconList, systemPackageList)
        }
    }

    private suspend fun getIconRequestList(
        lawniconsIconList: List<IconInfo>,
        systemPackageList: List<SystemIconInfo>,
    ) = withContext(Dispatchers.Default) {
        val themedComponentStrings = lawniconsIconList
            .flatMap { it.componentNames }
            .map { it.componentName.flattenToString() }
            .toSet()

        val unthemedApps = systemPackageList
            .filter { systemApp ->
                systemApp.componentName.flattenToString() !in themedComponentStrings
            }

        _iconRequestList.value = IconRequestModel(
            list = unthemedApps,
            iconCount = unthemedApps.size,
        )
    }

    override suspend fun createIconRequestZip(currentIconRequests: List<SystemIconInfo>?): File? {
        if (currentIconRequests.isNullOrEmpty()) {
            Log.d("IconRequestRepo", "No icon requests to bundle.")
            return null
        }
        return bundleIconRequestsToZip(application, currentIconRequests)
    }
}
