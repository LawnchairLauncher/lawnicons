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

package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.data.model.Announcement
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults.basePadding
import app.lawnchair.lawnicons.ui.components.core.ListRowDescription
import app.lawnchair.lawnicons.ui.components.core.ListRowLabel
import app.lawnchair.lawnicons.ui.components.core.rememberAnimatedShape
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceContainerColor
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.visitUrl
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    modifier: Modifier = Modifier,
) {
    AnnouncementCard(
        label = announcement.title,
        description = announcement.description,
        icon = "${Constants.WEBSITE}/lawnicons/${announcement.icon}.svg",
        url = announcement.url,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AnnouncementCard(
    label: String,
    description: String,
    icon: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    val shape = rememberAnimatedShape(
        currentShape = when {
            isFocused || isPressed -> ListRowDefaults.singleItemShapes.pressedShape
            else -> ListRowDefaults.singleItemShapes.shape
        } as RoundedCornerShape,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
    )

    Surface(
        modifier = modifier,
        shape = shape,
        color = adaptiveSurfaceContainerColor,
        onClick = {
            context.visitUrl(url)
        },
        interactionSource = interactionSource,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = basePadding, vertical = 12.dp),
        ) {
            AsyncImage(
                contentDescription = null,
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = icon)
                    .crossfade(enable = true)
                    .build(),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurfaceVariant),
                modifier = Modifier
                    .size(24.dp),
            )
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                ListRowLabel(label)
                ListRowDescription(description)
            }
        }
    }
}
