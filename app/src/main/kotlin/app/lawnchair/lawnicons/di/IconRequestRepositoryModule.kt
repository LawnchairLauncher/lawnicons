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
import app.lawnchair.lawnicons.data.api.IconRequestSettingsAPI
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepository
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object IconRequestRepositoryModule {

    @Provides
    @Singleton
    fun provideIconRequestRepository(
        application: Application,
        api: IconRequestSettingsAPI,
    ): IconRequestRepository = IconRequestRepositoryImpl(application, api)
}
