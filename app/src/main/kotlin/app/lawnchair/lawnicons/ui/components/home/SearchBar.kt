package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Destinations
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import kotlinx.collections.immutable.ImmutableList

@Composable
fun LawniconsSearchBar(
    query: String,
    isQueryEmpty: Boolean,
    onClearAndBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    iconInfoModel: IconInfoModel,
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean = false,
) {
    LawniconsSearchBar(
        query,
        isQueryEmpty,
        onClearAndBackClick,
        onQueryChange,
        iconInfoModel.iconCount,
        iconInfoModel.iconInfo,
        onNavigate,
        isExpandedScreen,
    )
}

@Composable
fun LawniconsSearchBar(
    query: String,
    isQueryEmpty: Boolean,
    onClearAndBackClick: () -> Unit,
    onQueryChange: (String) -> Unit,
    iconCount: Int,
    iconInfo: ImmutableList<IconInfo>,
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean = false,
) {
    var active by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .then(
                if (!active || isExpandedScreen) {
                    Modifier
                        .padding(
                            WindowInsets.navigationBars.toPaddingValues(
                                additionalStart = 16.dp,
                                additionalEnd = 16.dp,
                            ),
                        )
                        .statusBarsPadding()
                } else {
                    Modifier
                },
            )
            .semantics {
                isTraversalGroup = true
            }
            .zIndex(1f)
            .fillMaxSize(),
    ) {
        ResponsiveSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { active = false },
            active = active,
            onActiveChange = { active = it },
            placeholder = {
                Text(
                    stringResource(
                        id = R.string.search_bar_hint,
                        iconCount,
                    ),
                )
            },
            leadingIcon = {
                SearchIcon(
                    active = active,
                    onButtonClick = {
                        onClearAndBackClick()
                        active = !active
                    },
                )
            },
            trailingIcon = {
                SearchMenu(
                    isQueryEmpty = isQueryEmpty,
                    onNavigate = onNavigate,
                    onClearAndBackClick = onClearAndBackClick,
                )
            },
            isExpandedScreen = isExpandedScreen,
        ) {
            SearchContents(iconInfo)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ResponsiveSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    active: Boolean,
    onActiveChange: (Boolean) -> Unit,
    placeholder: @Composable () -> Unit,
    leadingIcon: @Composable () -> Unit,
    trailingIcon: @Composable () -> Unit,
    isExpandedScreen: Boolean,
    content: @Composable () -> Unit,
) {
    if (isExpandedScreen) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DockedSearchBar(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                active = active,
                onActiveChange = onActiveChange,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            ) {
                content()
            }
        }
    } else {
        SearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = onSearch,
            active = active,
            onActiveChange = onActiveChange,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = trailingIcon,
            modifier = Modifier.fillMaxWidth(),
        ) {
            content()
        }
    }
}

@Composable
private fun SearchIcon(
    active: Boolean,
    onButtonClick: () -> Unit,
) {
    if (active) {
        ClickableIcon(
            imageVector = Icons.Rounded.ArrowBack,
            onClick = onButtonClick,
        )
    } else {
        Icon(Icons.Rounded.Search, contentDescription = null)
    }
}

@Composable
private fun SearchMenu(
    isQueryEmpty: Boolean,
    onNavigate: (String) -> Unit,
    onClearAndBackClick: () -> Unit,
) {
    Crossfade(isQueryEmpty, label = "") {
        if (it) {
            OverflowMenu {
                DropdownMenuItem(
                    onClick = {
                        hideMenu()
                        onNavigate(Destinations.ACKNOWLEDGEMENTS)
                    },
                    text = {
                        Text(text = stringResource(id = R.string.acknowledgements))
                    },
                )
                DropdownMenuItem(
                    onClick = {
                        hideMenu()
                        onNavigate(Destinations.ABOUT)
                    },
                    text = {
                        Text(text = stringResource(id = R.string.about))
                    },
                )
            }
        } else {
            ClickableIcon(
                onClick = onClearAndBackClick,
                imageVector = Icons.Rounded.Close,
            )
        }
    }
}

@Composable
private fun SearchContents(
    iconInfo: ImmutableList<IconInfo>,
) {
    when (iconInfo.size) {
        1 -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(16.dp)),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                val it = iconInfo[0]
                val isIconInfoShown = remember { mutableStateOf(false) }

                ListItem(
                    headlineContent = { Text(it.name) },
                    supportingContent = { Text(it.packageName) },
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
                        .clickable(onClick = { isIconInfoShown.value = true }),
                )
                if (isIconInfoShown.value) {
                    IconInfoPopup(
                        iconInfo = it,
                    ) {
                        isIconInfoShown.value = it
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
                    text = stringResource(R.string.no_items_found),
                )
            }
        }
        else -> {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 80.dp),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(items = iconInfo) {
                    IconPreview(
                        iconInfo = it,
                        iconBackground = Color.Transparent,
                    )
                }
            }
        }
    }
}

@PreviewLawnicons
@Composable
private fun SearchBarPreview() {
    var searchTerm by remember { mutableStateOf(value = "") }
    val iconInfo = SampleData.iconInfoList

    LawniconsTheme {
        LawniconsSearchBar(
            query = searchTerm,
            isQueryEmpty = false,
            onClearAndBackClick = {},
            onQueryChange = { newValue ->
                searchTerm = newValue
            },
            iconCount = 2,
            iconInfo = iconInfo,
            onNavigate = {},
            isExpandedScreen = true,
        )
    }
}

@PreviewLawnicons
@Composable
private fun SearchIconPreview() {
    LawniconsTheme {
        Column {
            SearchIcon(active = true) {}
            SearchIcon(active = false) {}
        }
    }
}

@PreviewLawnicons
@Composable
private fun SearchMenuPreview() {
    LawniconsTheme {
        Column {
            SearchMenu(isQueryEmpty = false, {}, {})
            SearchMenu(isQueryEmpty = true, {}, {})
        }
    }
}
