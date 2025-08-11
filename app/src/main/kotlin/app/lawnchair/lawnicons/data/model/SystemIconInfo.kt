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

package app.lawnchair.lawnicons.data.model

import android.content.ComponentName
import android.graphics.drawable.Drawable

/**
 * Represents information about a system icon.
 *
 * @property drawable The [Drawable] for the icon.
 * @property label The label or name of the icon. Defaults to an empty string.
 * @property componentName The [ComponentName] associated with the icon.
 */
data class SystemIconInfo(
    val drawable: Drawable,
    override val label: String = "",
    val componentName: ComponentName,
) : BaseIconInfo {

    override val componentNames: List<LabelAndComponent> = listOf(
        LabelAndComponent(label, componentName),
    )
}
