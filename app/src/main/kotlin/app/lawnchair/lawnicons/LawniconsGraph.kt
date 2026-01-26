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

package app.lawnchair.lawnicons

import android.app.Application
import app.lawnchair.lawnicons.di.MetroViewModelFactoryImpl
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.Scope
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

@Scope
annotation class LawniconsScope

@SingleIn(LawniconsScope::class)
@DependencyGraph(LawniconsScope::class)
interface LawniconsGraph : ViewModelGraph {
    val viewModelFactory: MetroViewModelFactoryImpl

    @DependencyGraph.Factory
    fun interface Factory {
        fun create(@Provides app: Application): LawniconsGraph
    }
}
