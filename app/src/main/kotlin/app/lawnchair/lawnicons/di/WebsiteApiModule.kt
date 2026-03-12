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

package app.lawnchair.lawnicons.di

import android.app.Application
import app.lawnchair.lawnicons.LawniconsScope
import app.lawnchair.lawnicons.data.api.AnnouncementsAPI
import app.lawnchair.lawnicons.data.api.IconRequestSettingsAPI
import app.lawnchair.lawnicons.data.kotlinxJson
import app.lawnchair.lawnicons.ui.util.Constants
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.io.File
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create

@ContributesTo(LawniconsScope::class)
interface WebsiteApiModule {

    // Inside WebsiteApiModule
    @Provides
    @SingleIn(LawniconsScope::class)
    fun providesOkHttpClient(application: Application): OkHttpClient {
        val cacheSize = 5L * 1024 * 1024 // 5 MB
        val cache = Cache(File(application.cacheDir, "http_cache"), cacheSize)

        return OkHttpClient.Builder()
            .cache(cache)
            .build()
    }

    @Provides
    @SingleIn(LawniconsScope::class)
    fun providesWebsiteIconRequestApi(client: OkHttpClient): IconRequestSettingsAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.WEBSITE)
            .client(client)
            .addConverterFactory(kotlinxJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }

    @Provides
    @SingleIn(LawniconsScope::class)
    fun providesWebsiteAnnouncementsApi(client: OkHttpClient): AnnouncementsAPI {
        return Retrofit.Builder()
            .baseUrl(Constants.WEBSITE)
            .client(client)
            .addConverterFactory(kotlinxJson.asConverterFactory("application/json".toMediaType()))
            .build()
            .create()
    }
}
