package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
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
)

@Composable
fun HomeTopBar(
    uiState: HomeTopBarUiState,
    onFocusChange: () -> Unit,
    onClearSearch: () -> Unit,
    onChangeMode: (SearchMode) -> Unit,
    onSearchIcons: (String) -> Unit,
    onNavigate: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    val (isSearchExpanded, isExpandedScreen, searchTerm, searchMode, searchedIconInfoModel, isIconPicker) = uiState

    searchedIconInfoModel?.let { iconInfoModel ->
        AnimatedSearchBar(
            modifier = modifier,
            searchTerm = searchTerm,
            onClearSearch = onClearSearch,
            onModeChange = onChangeMode,
            onSearch = onSearchIcons,
            iconInfoModel = iconInfoModel,
            onNavigate = onNavigate,
            isSearchExpanded = isSearchExpanded,
            isExpandedScreen = isExpandedScreen,
            isIconPicker = isIconPicker,
            searchMode = searchMode,
            onSendResult = onSendResult,
            onFocusChange = onFocusChange,
            inputFieldModifier = Modifier.focusRequester(focusRequester),
        )
    }
}

@Composable
private fun AnimatedSearchBar(
    searchMode: SearchMode,
    searchTerm: String,
    onSearch: (String) -> Unit,
    onClearSearch: () -> Unit,
    onModeChange: (SearchMode) -> Unit,
    onNavigate: () -> Unit,
    isSearchExpanded: Boolean,
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    onSendResult: (IconInfo) -> Unit,
    iconInfoModel: IconInfoModel,
    onFocusChange: () -> Unit,
    modifier: Modifier = Modifier,
    inputFieldModifier: Modifier = Modifier,
) {
    val offset = animateDpAsState(
        targetValue = (if (isSearchExpanded || isExpandedScreen) 0 else -1000).dp,
        label = "move search bar",
        animationSpec = tween(
            durationMillis = 600,
        ),
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .offset {
                IntOffset(0, offset.value.roundToPx())
            },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val isIconInfoShown = rememberSaveable { mutableStateOf(false) }
        LawniconsSearchBar(
            query = searchTerm,
            isQueryEmpty = searchTerm == "",
            onClear = onClearSearch,
            onBack = onFocusChange,
            onSearch = {
                isIconInfoShown.value = true
            },
            onQueryChange = onSearch,
            iconCount = iconInfoModel.iconCount,
            onNavigate = onNavigate,
            isExpandedScreen = isExpandedScreen,
            isIconPicker = isIconPicker,
            content = {
                SearchContents(
                    searchTerm = searchTerm,
                    searchMode = searchMode,
                    onModeChange = onModeChange,
                    iconInfo = iconInfoModel.iconInfo,
                    onSendResult = onSendResult,
                    showSheet = isIconInfoShown.value,
                    onToggleSheet = { isIconInfoShown.value = it },
                )
            },
            inputFieldModifier = inputFieldModifier,
        )
    }
}
