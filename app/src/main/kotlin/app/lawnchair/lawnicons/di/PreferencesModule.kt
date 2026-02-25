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
import android.content.Context
import android.content.SharedPreferences
import app.lawnchair.lawnicons.LawniconsScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@ContributesTo(LawniconsScope::class)
interface PreferencesModule {

    @Provides
    @SingleIn(LawniconsScope::class)
    fun provideSharedPreferences(app: Application): SharedPreferences {
        // Note: We request 'Application' because we bound it in the Graph Factory earlier.
        // Application is a Context.
        return app.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }
}
