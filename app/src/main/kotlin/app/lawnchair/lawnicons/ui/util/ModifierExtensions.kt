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

package app.lawnchair.lawnicons.ui.util

import androidx.compose.ui.Modifier

/**
 * Conditionally apply a [Modifier] if the [condition] is true.
 *
 * @param condition the condition to check
 * @param other the modifier to apply if the condition is true
 * @return the original modifier if the condition is false, otherwise the result of applying the [other] modifier
 */
inline fun Modifier.thenIf(
    condition: Boolean,
    crossinline other: Modifier.() -> Modifier,
) = if (condition) other() else this

/**
 * Conditionally apply a [Modifier] if the [value] is not null.
 *
 * @param value the value to check for null
 * @param other the modifier to apply if the value is not null
 * @return the original modifier if the value is null, otherwise the result of applying the [other] modifier with the value
 */
inline fun <T> Modifier.thenIfNotNull(
    value: T?,
    crossinline other: Modifier.(T) -> Modifier,
) = if (value != null) other(value) else this
