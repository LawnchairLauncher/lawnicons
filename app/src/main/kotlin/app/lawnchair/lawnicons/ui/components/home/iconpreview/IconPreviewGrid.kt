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

package app.lawnchair.lawnicons.ui.components.home.iconpreview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.repository.preferenceManager
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import app.lawnchair.lawnicons.util.appIcon
import my.nanihadesuka.compose.InternalLazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

data class IconPreviewGridPadding(
    val topPadding: Dp,
    val bottomPadding: Dp,
    val horizontalPadding: Dp,
) {

    companion object {
        val Defaults = IconPreviewGridPadding(
            topPadding = 0.dp,
            bottomPadding = 80.dp,
            horizontalPadding = 8.dp,
        )

        val ExpandedSize = IconPreviewGridPadding(
            topPadding = 72.dp,
            bottomPadding = 0.dp,
            horizontalPadding = 32.dp,
        )
    }
}

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: List<IconInfo>,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    containerModifier: Modifier = Modifier
        .applyGridInsets(),
    contentPadding: IconPreviewGridPadding = IconPreviewGridPadding.Defaults,
    isIconPicker: Boolean = false,
    gridState: LazyGridState = rememberLazyGridState(),
    otherContent: (LazyGridScope.() -> Unit) = {},
) {
    val indexOfFirstItem by remember { derivedStateOf { gridState.firstVisibleItemIndex } }
    val letter = iconInfo[indexOfFirstItem].label[0].uppercase()
    var thumbSelected by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = containerModifier
                .padding(bottom = contentPadding.bottomPadding),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                contentPadding = WindowInsets.navigationBars.toPaddingValues(
                    additionalStart = contentPadding.horizontalPadding,
                    additionalTop = contentPadding.topPadding,
                    additionalEnd = contentPadding.horizontalPadding,
                ),
                state = gridState,
            ) {
                otherContent()
                items(
                    items = iconInfo,
                    contentType = { "icon_preview" },
                ) { iconInfo ->
                    val scale by animateFloatAsState(
                        if (thumbSelected && iconInfo.label.first().toString() == letter) {
                            1.1f
                        } else {
                            1f
                        },
                        label = "",
                    )
                    IconPreview(
                        modifier = Modifier
                            .scale(scale),
                        iconInfo = iconInfo,
                        isIconPicker = isIconPicker,
                        onSendResult = onSendResult,
                    )
                }
            }
            ScrollbarLayout(
                gridState,
                { thumbSelected = it },
                letter,
                contentPadding.topPadding,
            )
        }
    }
}

private fun Modifier.applyGridInsets() = this
    .widthIn(max = 640.dp)
    .fillMaxWidth()
    .statusBarsPadding()

@Composable
private fun ScrollbarLayout(
    gridState: LazyGridState,
    onSelectedChange: (Boolean) -> Unit,
    currentLetter: String,
    topPadding: Dp = 0.dp,
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.padding(top = topPadding),
    ) {
        Spacer(
            Modifier
                .fillMaxHeight()
                .width(8.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .clip(CircleShape),
        )
        InternalLazyVerticalGridScrollbar(
            modifier = Modifier.offset(7.dp),
            state = gridState,
            settings = ScrollbarSettings(
                alwaysShowScrollbar = true,
                thumbUnselectedColor = MaterialTheme.colorScheme.primary,
                thumbSelectedColor = MaterialTheme.colorScheme.primary,
                selectionMode = ScrollbarSelectionMode.Thumb,
            ),
            indicatorContent = { _, isThumbSelected ->
                onSelectedChange(isThumbSelected)
                ScrollbarIndicator(currentLetter, isThumbSelected)
            },
        )
    }
}

@Composable
private fun ScrollbarIndicator(
    label: String,
    isThumbSelected: Boolean,
) {
    AnimatedVisibility(
        visible = isThumbSelected,
        enter = fadeIn(),
        exit = fadeOut(),
    ) {
        Box(
            modifier = Modifier
                .padding(end = 16.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large,
                ),
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = label,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AppBarListItem(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val prefs = preferenceManager(context)
    CenterAlignedTopAppBar(
        modifier = modifier,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (!LocalInspectionMode.current) {
                    Image(
                        bitmap = context.appIcon().asImageBitmap(),
                        contentDescription = stringResource(id = R.string.app_name),
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .combinedClickable(
                                onClick = {},
                                onLongClick = {
                                    prefs.showDebugMenu.toggle()
                                },
                            ),
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    stringResource(id = R.string.app_name),
                )
            }
        },
    )
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun IconGridPreview() {
    LawniconsTheme {
        Surface {
            IconPreviewGrid(
                iconInfo = SampleData.iconInfoList,
                onSendResult = {},
                modifier = Modifier,
                contentPadding = IconPreviewGridPadding.Defaults,
                isIconPicker = false,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun IconGridExpandedPreview() {
    LawniconsTheme {
        Surface {
            IconPreviewGrid(
                iconInfo = SampleData.iconInfoList,
                onSendResult = {},
                modifier = Modifier,
                contentPadding = IconPreviewGridPadding.ExpandedSize,
                isIconPicker = false,
            )
        }
    }
}
