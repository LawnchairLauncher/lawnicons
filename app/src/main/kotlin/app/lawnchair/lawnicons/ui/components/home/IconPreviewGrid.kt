package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.grid.GridItemSpan
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
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import app.lawnchair.lawnicons.util.appIcon
import my.nanihadesuka.compose.InternalLazyVerticalGridScrollbar
import my.nanihadesuka.compose.ScrollbarSelectionMode
import my.nanihadesuka.compose.ScrollbarSettings

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: List<IconInfo>,
    isExpandedScreen: Boolean,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    contentPadding: PaddingValues? = null,
    gridState: LazyGridState = rememberLazyGridState(),
) {
    val indexOfFirstItem by remember { derivedStateOf { gridState.firstVisibleItemIndex } }
    val letter = iconInfo[indexOfFirstItem].label[0].uppercase()
    var thumbSelected by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxWidth(),
    ) {
        val horizontalGridPadding = if (isExpandedScreen) 32.dp else 8.dp
        Box(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxWidth()
                .statusBarsPadding()
                .then(
                    if (isExpandedScreen) {
                        Modifier.padding(top = 26.dp)
                    } else {
                        Modifier.padding(
                            bottom = 80.dp,
                        )
                    },
                ),
        ) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                contentPadding = contentPadding ?: WindowInsets.navigationBars.toPaddingValues(
                    additionalStart = horizontalGridPadding,
                    additionalTop = if (isExpandedScreen) 42.dp else 0.dp,
                    additionalEnd = horizontalGridPadding,
                ),
                state = gridState,
            ) {
                if (!isExpandedScreen) {
                    item(
                        span = {
                            GridItemSpan(maxLineSpan)
                        },
                    ) {
                        AppBarListItem()
                    }
                }
                items(
                    items = iconInfo,
                    contentType = { "icon_preview" },
                ) { iconInfo ->
                    val scale by animateFloatAsState(
                        if (thumbSelected && iconInfo.label.first()
                                .toString() == letter
                        ) {
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
            Box(
                contentAlignment = Alignment.CenterEnd,
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
                        thumbSelected = isThumbSelected
                        ScrollbarIndicator(letter, isThumbSelected)
                    },
                )
            }
        }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBarListItem(modifier: Modifier = Modifier) {
    val context = LocalContext.current
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
                        modifier = Modifier.size(36.dp),
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
                isExpandedScreen = false,
                onSendResult = {},
                modifier = Modifier,
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
                isExpandedScreen = true,
                onSendResult = {},
                modifier = Modifier,
                isIconPicker = false,
            )
        }
    }
}
