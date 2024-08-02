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

data class HomeTopBarUiState(
    val isSearchExpanded: Boolean,
    val isExpandedScreen: Boolean,
    val searchTerm: String,
    val searchMode: SearchMode,
    val searchedIconInfoModel: IconInfoModel?,
    val isIconPicker: Boolean,
    val appIcon: ImageBitmap,
)

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HomeTopBar(
    uiState: HomeTopBarUiState,
    onFocusChange: () -> Unit,
    onClearSearch: () -> Unit,
    onChangeMode: (SearchMode) -> Unit,
    onSearchIcons: (String) -> Unit,
    onNavigate: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
    focusRequester: FocusRequester,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier,
) {
    val (isSearchExpanded, isExpandedScreen, searchTerm, searchMode, searchedIconInfoModel, isIconPicker, appIcon) = uiState

    AnimatedContent(targetState = isSearchExpanded || isExpandedScreen, label = "TopAppBar to SearchBar", modifier = modifier) { targetState ->
        if (targetState) {
            searchedIconInfoModel?.let {
                SearchBar(
                    searchTerm = searchTerm,
                    onClearSearch = onClearSearch,
                    onChangeMode = onChangeMode,
                    onSearch = onSearchIcons,
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
    searchMode: SearchMode,
    searchTerm: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onChangeMode: (SearchMode) -> Unit,
    onNavigate: () -> Unit,
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    onSendResult: (IconInfo) -> Unit,
    iconInfoModel: IconInfoModel,
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
                onSearch(newValue)
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
