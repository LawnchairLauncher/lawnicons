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

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.placeholder.PlaceholderHighlight
import app.lawnchair.lawnicons.ui.components.core.placeholder.fade
import app.lawnchair.lawnicons.ui.components.core.placeholder.placeholder
import app.lawnchair.lawnicons.ui.components.core.placeholder.shimmer
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGridPadding
import app.lawnchair.lawnicons.ui.components.home.iconpreview.iconColor
import app.lawnchair.lawnicons.ui.util.toPaddingValues

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceholderUI(
    showDummyCard: Boolean,
    modifier: Modifier = Modifier,
) {
    val contentPadding = IconPreviewGridPadding.Defaults
    Scaffold(
        modifier = modifier,
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.placeholder(
                    visible = true,
                    color = BottomAppBarDefaults.containerColor,
                    highlight = PlaceholderHighlight.shimmer(
                        MaterialTheme.colorScheme.surfaceContainer,
                    ),
                ),
            ) {}
        },
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(80.dp),
            userScrollEnabled = false,
            contentPadding = WindowInsets.navigationBars.toPaddingValues(
                additionalStart = contentPadding.horizontalPadding,
                additionalTop = contentPadding.topPadding,
                additionalEnd = contentPadding.horizontalPadding,
            ),
        ) {
            item(
                span = { GridItemSpan(maxLineSpan) },
            ) {
                CenterAlignedTopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .placeholder(
                                        visible = true,
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        highlight = PlaceholderHighlight.fade(),
                                    ),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .width(96.dp)
                                    .height(16.dp)
                                    .clip(MaterialTheme.shapes.small)
                                    .placeholder(
                                        visible = true,
                                        color = MaterialTheme.colorScheme.surfaceContainer,
                                        highlight = PlaceholderHighlight.fade(),
                                    ),
                            )
                        }
                    },
                )
            }
            if (showDummyCard) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 12.dp)
                            .height(48.dp)
                            .fillMaxWidth()
                            .clip(CircleShape)
                            .placeholder(
                                visible = true,
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                highlight = PlaceholderHighlight.shimmer(
                                    MaterialTheme.colorScheme.surfaceContainerHigh,
                                ),
                            ),
                    ) {}
                }
            }

            items(100) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .aspectRatio(ratio = 1F)
                        .clip(shape = CircleShape)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.iconColor,
                            highlight = PlaceholderHighlight.shimmer(
                                MaterialTheme.colorScheme.surfaceContainer,
                            ),
                        ),
                ) {}
            }
        }
    }
}
