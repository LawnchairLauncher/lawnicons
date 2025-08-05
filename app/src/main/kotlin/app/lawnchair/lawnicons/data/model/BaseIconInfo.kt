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
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

interface BaseIconInfo {
    val componentNames: List<LabelAndComponentV2>

    /**
     * The user-facing label associated with the icon, derived from the first available
     * [LabelAndComponent] object.
     */
    val label: String
        get() = componentNames.firstOrNull()?.label ?: ""
}

fun BaseIconInfo.getFirstLabelAndComponent(): LabelAndComponentV2 {
    val firstLabel = componentNames.firstOrNull()?.label ?: ""
    val firstComponent = componentNames.firstOrNull()?.componentName ?: ComponentName("", "")
    return LabelAndComponentV2(firstLabel, firstComponent)
}

/**
 * Data class representing a label and component name pair.
 *
 * @property label The user-facing label associated with the component.
 * @property componentName The name of the component, typically a fully qualified class name.
 */
@Serializable
data class LabelAndComponentV2(
    val label: String,
    @Serializable(with = ComponentNameSerializer::class) val componentName: ComponentName,
) {
    constructor(
        label: String,
        componentName: String,
    ) : this(label, ComponentName.unflattenFromString(componentName)!!)
}

object ComponentNameSerializer : KSerializer<ComponentName> {
    override val descriptor = PrimitiveSerialDescriptor("ComponentName", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: ComponentName) {
        encoder.encodeString(value.flattenToString())
    }

    override fun deserialize(decoder: Decoder): ComponentName {
        return ComponentName.unflattenFromString(decoder.decodeString())!!
    }
}
