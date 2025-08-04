package app.lawnchair.lawnicons.ui.components.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.data.model.SearchMode
import app.lawnchair.lawnicons.data.model.getFirstLabelAndComponent
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconInfoSheet
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreview

@Composable
fun SearchContents(
    searchTerm: String,
    searchMode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    iconInfo: List<IconInfo>,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    onSendResult: (IconInfo) -> Unit = {},
    showSheet: Boolean = false,
    onToggleSheet: (Boolean) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
        ) {
            FilterChip(
                leadingIcon = {
                    AnimatedVisibility(searchMode == SearchMode.LABEL) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                },
                selected = searchMode == SearchMode.LABEL,
                onClick = {
                    onModeChange(SearchMode.LABEL)
                },
                label = {
                    Text(text = stringResource(R.string.name))
                },
            )
            FilterChip(
                leadingIcon = {
                    AnimatedVisibility(searchMode == SearchMode.COMPONENT) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                },
                selected = searchMode == SearchMode.COMPONENT,
                onClick = {
                    onModeChange(SearchMode.COMPONENT)
                },
                label = {
                    Text(text = stringResource(id = R.string.component))
                },
            )
            FilterChip(
                leadingIcon = {
                    AnimatedVisibility(searchMode == SearchMode.DRAWABLE) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                },
                selected = searchMode == SearchMode.DRAWABLE,
                onClick = {
                    onModeChange(SearchMode.DRAWABLE)
                },
                label = {
                    Text(text = stringResource(id = R.string.drawable))
                },
            )
        }
        Crossfade(
            targetState = iconInfo.size,
            label = "On item count modified",
        ) { count ->
            when (count) {
                1 -> {
                    IconInfoListItem(
                        iconInfo = iconInfo.firstOrNull(),
                        showSheet = showSheet,
                        onToggleSheet = onToggleSheet,
                        isIconPicker = isIconPicker,
                        onSendResult = onSendResult,
                    )
                }

                0 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(16.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            textAlign = TextAlign.Center,
                            text = stringResource(R.string.no_items_found, searchTerm),
                        )
                    }
                }

                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 80.dp),
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 80.dp),
                    ) {
                        itemsIndexed(
                            items = iconInfo,
                            contentType = { _, _ -> "icon_preview" },
                        ) { index, it ->
                            if (index == 0 && searchTerm != "") {
                                IconPreview(
                                    iconInfo = it,
                                    isIconPicker = isIconPicker,
                                    onSendResult = onSendResult,
                                    iconBackground = MaterialTheme.colorScheme.surface,
                                    showSheet = showSheet,
                                    onToggleSheet = onToggleSheet,
                                )
                            } else {
                                IconPreview(
                                    iconInfo = it,
                                    isIconPicker = isIconPicker,
                                    onSendResult = onSendResult,
                                    iconBackground = MaterialTheme.colorScheme.surfaceContainerLow,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconInfoListItem(
    iconInfo: IconInfo?,
    showSheet: Boolean,
    onToggleSheet: (Boolean) -> Unit,
    isIconPicker: Boolean = false,
    onSendResult: (IconInfo) -> Unit = {},
) {
    if (iconInfo == null) return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(PaddingValues(16.dp)),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        ListItem(
            headlineContent = { Text(iconInfo.getFirstLabelAndComponent().label) },
            supportingContent = { Text(iconInfo.getFirstLabelAndComponent().componentName) },
            leadingContent = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .clip(shape = CircleShape)
                        .size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(id = iconInfo.id),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(0.6f),
                    )
                }
            },
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .clickable(
                    onClick = {
                        if (isIconPicker) {
                            onSendResult(iconInfo)
                        } else {
                            onToggleSheet(true)
                        }
                    },
                ),
        )
        AnimatedVisibility(showSheet) {
            IconInfoSheet(
                iconInfo = iconInfo,
            ) {
                onToggleSheet(it)
            }
        }
    }
}
