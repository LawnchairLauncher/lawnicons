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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import app.lawnchair.lawnicons.ui.LocalLawniconsActions
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconInfoSheet
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreview
import app.lawnchair.lawnicons.ui.theme.icon.Check
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons

@Composable
fun SearchContents(
    state: SearchState,
    iconInfo: List<IconInfo>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        SearchModeSelector(
            currentMode = state.mode,
            onModeChange = { state.setMode(it) },
        )

        val actions = LocalLawniconsActions.current

        Crossfade(
            targetState = iconInfo.size,
            label = "On item count modified",
        ) { count ->
            when (count) {
                0 -> EmptyState(state.searchTerm)

                1 -> IconInfoListItem(
                    iconInfo = iconInfo.firstOrNull(),
                    showSheet = state.isSheetVisible,
                    onToggleSheet = { state.isSheetVisible = it },
                    isIconPicker = actions.isIconPicker,
                    onSendResult = actions.onSendResult,
                )

                else -> IconGrid(
                    iconInfo = iconInfo,
                    searchTerm = state.searchTerm,
                    showSheet = state.isSheetVisible,
                    onToggleSheet = { state.isSheetVisible = it },
                    isIconPicker = actions.isIconPicker,
                    onSendResult = actions.onSendResult,
                )
            }
        }
    }
}

@Composable
private fun EmptyState(searchTerm: String) {
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

@Composable
private fun IconGrid(
    iconInfo: List<IconInfo>,
    searchTerm: String,
    showSheet: Boolean,
    onToggleSheet: (Boolean) -> Unit,
    isIconPicker: Boolean,
    onSendResult: (IconInfo) -> Unit,
) {
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
                    iconBackground = MaterialTheme.colorScheme.surface,
                    showSheet = showSheet,
                    onToggleSheet = onToggleSheet,
                    isIconPicker = isIconPicker,
                    onSendResult = onSendResult,
                )
            } else {
                val isIconInfoShown = rememberSaveable { mutableStateOf(false) }
                IconPreview(
                    iconInfo = it,
                    iconBackground = MaterialTheme.colorScheme.surfaceContainerLow,
                    showSheet = isIconInfoShown.value,
                    onToggleSheet = { isIconInfoShown.value = it },
                    isIconPicker = isIconPicker,
                    onSendResult = onSendResult,
                )
            }
        }
    }
}

@Composable
private fun SearchModeSelector(
    currentMode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
    ) {
        FilterChip(
            leadingIcon = {
                AnimatedVisibility(currentMode == SearchMode.LABEL) {
                    Icon(
                        imageVector = LawnIcons.Check,
                        contentDescription = null,
                    )
                }
            },
            selected = currentMode == SearchMode.LABEL,
            onClick = {
                onModeChange(SearchMode.LABEL)
            },
            label = {
                Text(text = stringResource(R.string.name))
            },
        )
        FilterChip(
            leadingIcon = {
                AnimatedVisibility(currentMode == SearchMode.COMPONENT) {
                    Icon(
                        imageVector = LawnIcons.Check,
                        contentDescription = null,
                    )
                }
            },
            selected = currentMode == SearchMode.COMPONENT,
            onClick = {
                onModeChange(SearchMode.COMPONENT)
            },
            label = {
                Text(text = stringResource(id = R.string.component))
            },
        )
        FilterChip(
            leadingIcon = {
                AnimatedVisibility(currentMode == SearchMode.DRAWABLE) {
                    Icon(
                        imageVector = LawnIcons.Check,
                        contentDescription = null,
                    )
                }
            },
            selected = currentMode == SearchMode.DRAWABLE,
            onClick = {
                onModeChange(SearchMode.DRAWABLE)
            },
            label = {
                Text(text = stringResource(id = R.string.drawable))
            },
        )
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
            supportingContent = {
                Text(
                    iconInfo.getFirstLabelAndComponent().componentName.flattenToString(),
                )
            },
            leadingContent = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .padding(all = 8.dp)
                        .clip(shape = CircleShape)
                        .size(48.dp),
                ) {
                    Icon(
                        painter = painterResource(id = iconInfo.drawableId),
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
