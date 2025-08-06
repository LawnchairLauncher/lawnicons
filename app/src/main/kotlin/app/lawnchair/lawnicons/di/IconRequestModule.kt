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

import app.lawnchair.lawnicons.data.repository.iconrequest.ArcticonsDashboardHandler
import app.lawnchair.lawnicons.data.repository.iconrequest.IconRequestHandler
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent // Or another component like ViewModelComponent if appropriate
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class IconRequestModule {

    @Binds
    @Singleton
    abstract fun bindIconRequestHandler(
        arcticonsDashboardHandler: ArcticonsDashboardHandler,
    ): IconRequestHandler
}
