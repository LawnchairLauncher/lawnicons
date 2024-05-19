package app.lawnchair.lawnicons.ui.components.home.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfoAppfilter
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.IconInfoPopup
import app.lawnchair.lawnicons.ui.components.home.IconPreview
import kotlinx.collections.immutable.ImmutableList

@Composable
fun SearchContents(
    searchTerm: String,
    searchMode: SearchMode,
    onModeChange: (SearchMode) -> Unit,
    iconInfo: ImmutableList<IconInfoAppfilter>,
    modifier: Modifier = Modifier,
    onSendResult: (IconInfoAppfilter) -> Unit = {},
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(start = 16.dp),
        ) {
            FilterChip(
                leadingIcon = {
                    AnimatedVisibility(searchMode == SearchMode.NAME) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                },
                selected = searchMode == SearchMode.NAME,
                onClick = {
                    onModeChange(SearchMode.NAME)
                },
                label = {
                    Text(text = stringResource(R.string.name))
                },
            )
            FilterChip(
                leadingIcon = {
                    AnimatedVisibility(searchMode == SearchMode.PACKAGE_NAME) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = null,
                        )
                    }
                },
                selected = searchMode == SearchMode.PACKAGE_NAME,
                onClick = {
                    onModeChange(SearchMode.PACKAGE_NAME)
                },
                label = {
                    Text(text = stringResource(id = R.string.package_prefix))
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
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(PaddingValues(16.dp)),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        val it = iconInfo[0]
                        val isIconInfoAppfilterShown = remember { mutableStateOf(false) }

                        ListItem(
                            headlineContent = { Text(it.name) },
                            supportingContent = { Text(it.componentName) },
                            leadingContent = {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .padding(all = 8.dp)
                                        .clip(shape = CircleShape)
                                        .size(48.dp),
                                ) {
                                    Icon(
                                        painter = painterResource(id = it.id),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(0.6f),
                                    )
                                }
                            },
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .clickable(onClick = { isIconInfoAppfilterShown.value = true }),
                        )
                        if (isIconInfoAppfilterShown.value) {
                            IconInfoPopup(
                                iconInfo = it,
                            ) {
                                isIconInfoAppfilterShown.value = it
                            }
                        }
                    }
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
                    Crossfade(
                        targetState = iconInfo,
                        label = "Item changed",
                        animationSpec = tween(50),
                    ) { iconInfo ->
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 80.dp),
                            contentPadding = PaddingValues(16.dp),
                        ) {
                            items(items = iconInfo) {
                                IconPreview(
                                    iconInfo = it,
                                    iconBackground = Color.Transparent,
                                    onSendResult = onSendResult,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
