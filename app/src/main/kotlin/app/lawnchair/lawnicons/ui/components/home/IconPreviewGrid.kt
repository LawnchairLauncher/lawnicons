package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.getFirstLabelAndComponent
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import kotlinx.collections.immutable.ImmutableList
import my.nanihadesuka.compose.LazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: ImmutableList<IconInfo>,
    isExpandedScreen: Boolean,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    contentPadding: PaddingValues? = null,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val groupedIcons = iconInfo.groupBy {
        val label = it.getFirstLabelAndComponent().label
        if (label.isEmpty()) {
            "1-9"
        } else {
            val firstChar = label.firstOrNull()?.uppercase() ?: ""
            if (firstChar in "![](){}#0123456789") {
                "1-9"
            } else {
                firstChar
            }
        }
    }

    val headerIndices = remember { mutableStateOf(mutableMapOf<String, Int>()) }
    var currentIndex = 0

    val currentHeader = remember { mutableStateOf<String?>(null) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        LazyVerticalGridScrollbar(
            modifier = Modifier
                .then(
                    if (isExpandedScreen) Modifier.width(640.dp) else Modifier,
                )
                .statusBarsPadding()
                .padding(top = 26.dp),
            state = gridState,
            settings = ScrollbarSettings(
                alwaysShowScrollbar = true,
                thumbUnselectedColor = MaterialTheme.colorScheme.primaryContainer,
                thumbSelectedColor = MaterialTheme.colorScheme.primary,
                selectionMode = ScrollbarSelectionMode.Thumb,
            ),
            indicatorContent = { _, isThumbSelected ->
                AnimatedVisibility(visible = isThumbSelected) {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium,
                            ),
                    ) {
                        Text(
                            modifier = Modifier.padding(16.dp),
                            text = currentHeader.value ?: "#",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            },
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                contentPadding = contentPadding ?: if (!isExpandedScreen) {
                    WindowInsets.navigationBars.toPaddingValues(
                        additionalStart = 8.dp,
                        additionalTop = 42.dp,
                        additionalEnd = 8.dp,
                    )
                } else {
                    WindowInsets.navigationBars.toPaddingValues(
                        additionalStart = 32.dp,
                        additionalTop = 42.dp,
                        additionalEnd = 32.dp,
                    )
                },
                state = gridState,
            ) {
                groupedIcons.forEach { (header, icons) ->
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                        contentType = { "header" },
                        key = header,
                    ) {
                        Row {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                text = header,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                        headerIndices.value[header] = currentIndex
                        // ... Content for header item
                        currentIndex++
                    }
                    items(
                        items = icons,
                        contentType = { "icon_preview" },
                    ) { iconInfo ->
                        IconPreview(
                            iconInfo = iconInfo,
                            isIconPicker = isIconPicker,
                            onSendResult = onSendResult,
                        )
                        currentIndex++
                    }
                }
            }
        }
    }

    val firstVisibleItemIndex = remember { derivedStateOf { gridState.firstVisibleItemIndex } }

    LaunchedEffect(firstVisibleItemIndex.value) {
        val lastVisibleHeader = headerIndices.value.entries.lastOrNull {
            it.value < gridState.firstVisibleItemIndex
        }?.key
        currentHeader.value = lastVisibleHeader
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun IconGridPreview() {
    LawniconsTheme {
        IconPreviewGrid(
            SampleData.iconInfoList,
            true,
            {},
            Modifier,
            false,
        )
    }
}
