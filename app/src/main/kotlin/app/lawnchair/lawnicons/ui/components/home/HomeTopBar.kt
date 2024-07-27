package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.search.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.search.SearchContents

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTopBar(
    isSearchExpanded: Boolean,
    onFocusChange: () -> Unit,
    isExpandedScreen: Boolean,
    onClearSearch: () -> Unit,
    onChangeMode: (SearchMode) -> Unit,
    onSearchIcons: (String) -> Unit,
    searchedIconInfoModel: IconInfoModel?,
    onNavigate: () -> Unit,
    isIconPicker: Boolean,
    searchTerm: String,
    searchMode: SearchMode,
    onSendResult: (IconInfo) -> Unit,
    focusRequester: FocusRequester,
    scrollBehavior: TopAppBarScrollBehavior,
    appIcon: ImageBitmap,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(targetState = isSearchExpanded || isExpandedScreen, label = "TopAppBar to SearchBar", modifier = modifier) { targetState ->
        if (targetState) {
            searchedIconInfoModel?.let {
                SearchBar(
                    searchTerm = searchTerm,
                    onClearSearch = onClearSearch,
                    onChangeMode = onChangeMode,
                    onSearchIcons = onSearchIcons,
                    iconInfoModel = it,
                    onNavigate = onNavigate,
                    isExpandedScreen = isExpandedScreen,
                    isIconPicker = isIconPicker,
                    searchMode = searchMode,
                    onSendResult = onSendResult,
                    onFocusChange = onFocusChange,
                    inputFieldModifier = Modifier.focusRequester(focusRequester),
                )
            }
        } else {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Image(
                            bitmap = appIcon,
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier.size(36.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            stringResource(id = R.string.app_name),
                        )
                    }
                },
            )
        }
    }
}

@Composable
private fun SearchBar(
    onClearSearch: () -> Unit,
    onChangeMode: (SearchMode) -> Unit,
    onSearchIcons: (String) -> Unit,
    searchTerm: String,
    iconInfoModel: IconInfoModel,
    onNavigate: () -> Unit,
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    searchMode: SearchMode,
    onSendResult: (IconInfo) -> Unit,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
    inputFieldModifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        LawniconsSearchBar(
            query = searchTerm,
            isQueryEmpty = searchTerm == "",
            onClearAndBackClick = {
                onClearSearch()
                onFocusChange()
            },
            onQueryChange = { newValue ->
                onSearchIcons(newValue)
            },
            iconInfoModel = iconInfoModel,
            onNavigate = onNavigate,
            isExpandedScreen = isExpandedScreen,
            isIconPicker = isIconPicker,
            content = {
                SearchContents(
                    searchTerm = searchTerm,
                    searchMode = searchMode,
                    onModeChange = { mode ->
                        onChangeMode(mode)
                    },
                    iconInfo = iconInfoModel.iconInfo,
                    onSendResult = {
                        onSendResult(it)
                    },
                )
            },
            inputFieldModifier = inputFieldModifier,
        )
    }
}
