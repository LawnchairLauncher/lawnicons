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

package app.lawnchair.lawnicons.ui.destination.debugmenu

import androidx.lifecycle.ViewModel
import app.lawnchair.lawnicons.data.repository.NewIconsRepository
import app.lawnchair.lawnicons.data.repository.home.IconRepository
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DebugMenuViewModel @Inject constructor(
    iconRepository: IconRepository,
    newIconsRepository: NewIconsRepository,
    iconRequestRepository: IconRequestRepository,
) : ViewModel() {
    val iconInfoModel = iconRepository.iconInfoModel
    val newIconsModel = newIconsRepository.newIconsInfoModel
    val iconRequestList = iconRequestRepository.iconRequestList
}
