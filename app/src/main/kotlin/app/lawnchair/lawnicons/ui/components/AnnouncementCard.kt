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

package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.data.model.Announcement
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults
import app.lawnchair.lawnicons.ui.components.core.ListRowDescription
import app.lawnchair.lawnicons.ui.components.core.ListRowLabel
import app.lawnchair.lawnicons.ui.components.core.rememberAnimatedShape
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.visitUrl
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Immutable
class AnnouncementColors(
    val containerColor: Color,
    val iconTint: Color,
)

object AnnouncementDefaults {
    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainer

    val iconTint: Color
        @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun colors(
        containerColor: Color = AnnouncementDefaults.containerColor,
        iconTint: Color = AnnouncementDefaults.iconTint,
    ): AnnouncementColors = AnnouncementColors(
        containerColor = containerColor,
        iconTint = iconTint,
    )

    @OptIn(ExperimentalMaterial3ExpressiveApi::class)
    @Composable
    fun shape(
        isPressed: Boolean,
    ) = rememberAnimatedShape(
        currentShape = when {
            isPressed -> ListRowDefaults.singleItemShapes.pressedShape
            else -> ListRowDefaults.singleItemShapes.shape
        } as RoundedCornerShape,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
    )

    val ContentPadding = PaddingValues(horizontal = ListRowDefaults.basePadding, vertical = 12.dp)
    val ListPadding = PaddingValues(8.dp)
    val IconSize = 24.dp
    val ItemSpacing = 8.dp
}

@Composable
fun AnnouncementList(
    announcements: List<Announcement>,
    modifier: Modifier = Modifier,
    colors: AnnouncementColors = AnnouncementDefaults.colors(),
) {
    if (announcements.isEmpty()) return

    if (announcements.size == 1) {
        AnnouncementCard(
            announcement = announcements.first(),
            modifier = modifier
                .padding(AnnouncementDefaults.ListPadding)
                .fillMaxWidth(),
            colors = colors,
        )
    } else {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AnnouncementDefaults.ItemSpacing),
            modifier = modifier
                .horizontalScroll(rememberScrollState())
                .padding(AnnouncementDefaults.ListPadding),
        ) {
            announcements.forEach { announcement ->
                AnnouncementCard(
                    announcement = announcement,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
fun AnnouncementCard(
    announcement: Announcement,
    modifier: Modifier = Modifier,
    colors: AnnouncementColors = AnnouncementDefaults.colors(),
) {
    AnnouncementCard(
        label = announcement.title,
        description = announcement.description,
        icon = "${Constants.WEBSITE}/lawnicons/${announcement.icon}.svg",
        url = announcement.url,
        modifier = modifier,
        colors = colors,
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
    colors: AnnouncementColors = AnnouncementDefaults.colors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val context = LocalContext.current
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isPressed by interactionSource.collectIsPressedAsState()

    Surface(
        modifier = modifier,
        shape = AnnouncementDefaults.shape(isFocused || isPressed),
        color = colors.containerColor,
        onClick = { context.visitUrl(url) },
        interactionSource = interactionSource,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AnnouncementDefaults.ItemSpacing),
            modifier = Modifier.padding(AnnouncementDefaults.ContentPadding),
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(icon)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                colorFilter = ColorFilter.tint(colors.iconTint),
                modifier = Modifier.size(AnnouncementDefaults.IconSize),
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                ListRowLabel(label)
                ListRowDescription(description)
            }
        }
    }
}
