package app.lawnchair.lawnicons.ui.components.home.search

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.NavigationIconButton
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.thenIf
import app.lawnchair.lawnicons.ui.util.toPaddingValues

/**
 * Composable function to create a search bar for the Lawnicons app.
 *
 * @param query The current search query entered by the user.
 * @param isQueryEmpty A boolean value indicating whether the search query is empty.
 * @param onClear A callback function that handles clearing the search query.
 * @param onBack A callback function that handles navigating back.
 * @param onSearch A callback function that handles searching based on the current search query.
 * @param onQueryChange A callback function that handles changes in the search query.
 * @param iconCount The number of icons available for selection.
 * @param onNavigate A callback function that handles navigation to different screens based on the search query.
 * @param modifier A modifier to customize the appearance or behavior of the search bar.
 * @param isExpandedScreen A boolean value indicating whether the search bar is displayed on an expanded screen.
 * @param isIconPicker A boolean value indicating whether the search bar is used for selecting icons.
 * @param content A composable function that represents the content to be displayed within the search bar.
 */
@Composable
fun LawniconsSearchBar(
    query: String,
    isQueryEmpty: Boolean,
    onClear: () -> Unit,
    onBack: () -> Unit,
    onSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    iconCount: Int,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    inputFieldModifier: Modifier = Modifier,
    isExpandedScreen: Boolean = false,
    isIconPicker: Boolean = false,
    content: @Composable (() -> Unit),
) {
    var active by rememberSaveable { mutableStateOf(false) }
    val padding = WindowInsets.navigationBars.toPaddingValues(
        additionalStart = 16.dp,
        additionalEnd = 16.dp,
    )

    Box(
        modifier = modifier
            .animateContentSize()
            .thenIf(isExpandedScreen) {
                padding(padding).statusBarsPadding()
            }
            .semantics {
                isTraversalGroup = true
            }
            .zIndex(1f)
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        ResponsiveSearchBar(
            query = query,
            onQueryChange = onQueryChange,
            onSearch = { if (query != "") onSearch() },
            active = active,
            onActiveChange = {
                active = it
                if (!active) {
                    onBack()
                }
            },
            placeholder = {
                Text(
                    stringResource(
                        id = if (isIconPicker) {
                            R.string.search_bar_icon_picker
                        } else {
                            R.string.search_bar_hint
                        },
                        iconCount,
                    ),
                )
            },
            leadingIcon = {
                SearchIcon(
                    active = active,
                    onButtonClick = {
                        onBack()
                        active = !active
                    },
                )
            },
            trailingIcon = {
                if (!isIconPicker) {
                    SearchActionButton(
                        isQueryEmpty = isQueryEmpty,
                        navigateContent = {
                            if (isExpandedScreen) {
                                IconButton(
                                    onClick = it,
                                ) {
                                    Icon(
                                        imageVector = ImageVector.vectorResource(id = R.drawable.lawnicons_foreground),
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier
                                            .size(24.dp),
                                    )
                                }
                            }
                        },
                        onNavigate = onNavigate,
                        onClear = onClear,
                    )
                }
            },
            isExpandedScreen = isExpandedScreen,
            inputFieldModifier = inputFieldModifier,
        ) {
            content()
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
    modifier: Modifier = Modifier,
    inputFieldModifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val inputField =
        @Composable {
            SearchBarDefaults.InputField(
                query = query,
                onQueryChange = onQueryChange,
                onSearch = onSearch,
                modifier = inputFieldModifier,
                expanded = active,
                onExpandedChange = onActiveChange,
                placeholder = placeholder,
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
            )
        }

    if (isExpandedScreen) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = modifier,
        ) {
            DockedSearchBar(
                inputField = inputField,
                expanded = active,
                onExpandedChange = onActiveChange,
                content = content,
            )
        }
    } else {
        SearchBar(
            inputField = inputField,
            expanded = active,
            onExpandedChange = onActiveChange,
            modifier = Modifier.fillMaxWidth(),
            content = content,
        )
    }
}

@Composable
internal fun SearchIcon(
    active: Boolean,
    onButtonClick: () -> Unit,
) {
    if (active) {
        NavigationIconButton(
            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
            onClick = onButtonClick,
        )
    } else {
        Icon(Icons.Rounded.Search, contentDescription = null)
    }
}

@Composable
internal fun SearchActionButton(
    isQueryEmpty: Boolean,
    navigateContent: @Composable (() -> Unit) -> Unit,
    onNavigate: () -> Unit,
    onClear: () -> Unit,
) {
    Crossfade(isQueryEmpty, label = "") {
        if (it) {
            navigateContent(onNavigate)
        } else {
            NavigationIconButton(
                onClick = onClear,
                imageVector = Icons.Rounded.Close,
            )
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
            onClear = {},
            onBack = {},
            onSearch = {},
            onQueryChange = { newValue ->
                searchTerm = newValue
            },
            iconCount = 2,
            onNavigate = {},
            isExpandedScreen = true,
            content = {
                SearchContents(
                    "",
                    SearchMode.LABEL,
                    {},
                    iconInfo,
                )
            },
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
private fun SearchActionButtonPreview() {
    LawniconsTheme {
        Column {
            SearchActionButton(isQueryEmpty = false, {}, {}, {})
            SearchActionButton(isQueryEmpty = true, {}, {}, {})
        }
    }
}
