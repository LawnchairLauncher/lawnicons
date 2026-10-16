/*
 * Copyright 2026 Lawnchair Launcher
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
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.repository.home.getIconInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Qualifier
import dev.zacsweers.metro.SingleIn

@Qualifier
annotation class AppFilter

@Qualifier
annotation class AppFilterDiff

interface IconDataSource {
    fun getIconInfo(): List<IconInfo>
}

@AppFilter
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AppFilterIconDataSource(private val application: Application) : IconDataSource {
    private val cachedIconInfo: List<IconInfo> by lazy {
        application.getIconInfo(R.xml.appfilter)
    }

    override fun getIconInfo(): List<IconInfo> = cachedIconInfo
}

@AppFilterDiff
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class AppFilterDiffIconDataSource(private val application: Application) : IconDataSource {
    private val cachedIconInfo: List<IconInfo> by lazy {
        application.getIconInfo(R.xml.appfilter_diff)
    }

    override fun getIconInfo(): List<IconInfo> = cachedIconInfo
}
