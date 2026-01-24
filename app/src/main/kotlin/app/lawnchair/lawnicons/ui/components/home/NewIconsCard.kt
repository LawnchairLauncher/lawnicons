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

package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.ui.components.core.ListRowLabel
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceContainerColor
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.theme.icon.NewReleases
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewIconsCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SegmentedListItem(
        onClick = onClick,
        shapes = ListItemDefaults.shapes(
            shape = MaterialTheme.shapes.extraLarge,
            pressedShape = MaterialTheme.shapes.large,
            focusedShape = MaterialTheme.shapes.large,
        ),
        colors = ListItemDefaults.colors(
            containerColor = adaptiveSurfaceContainerColor,
        ),
        modifier = modifier
            .padding(horizontal = 8.dp)
            .widthIn(max = 400.dp),
        content = {
            ListRowLabel(
                label = stringResource(
                    R.string.new_icons_in_version,
                    BuildConfig.VERSION_NAME,
                ),
            )
        },
        leadingContent = {
            Icon(
                imageVector = LawnIcons.NewReleases,
                contentDescription = null,
            )
        },
    )
}

@PreviewLawnicons
@Composable
private fun NewIconsCardPreview() {
    LawniconsTheme {
        Surface {
            NewIconsCard({})
        }
    }
}
