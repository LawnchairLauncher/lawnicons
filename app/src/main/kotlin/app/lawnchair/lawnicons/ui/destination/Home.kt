package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.HomeBottomBar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBarUiState
import app.lawnchair.lawnicons.ui.components.home.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.IconRequestFAB
import app.lawnchair.lawnicons.ui.components.home.search.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.search.PlaceholderSearchBar
import app.lawnchair.lawnicons.ui.components.home.search.SearchContents
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.util.appIcon
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel
import kotlinx.collections.immutable.toImmutableList

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun Home(
    onNavigate: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    lawniconsViewModel: LawniconsViewModel = hiltViewModel(),
) {
    with(lawniconsViewModel) {
        val iconInfoModel by iconInfoModel.collectAsStateWithLifecycle()
        val searchedIconInfoModel by searchedIconInfoModel.collectAsStateWithLifecycle()
        val iconRequestModel by iconRequestModel.collectAsStateWithLifecycle()
        val searchMode = searchMode
        val searchTerm = searchTerm

        val expandSearch = remember { mutableStateOf(false) }
        val context = LocalContext.current

        val lazyGridState = rememberLazyGridState()
        val snackbarHostState = remember { SnackbarHostState() }

        val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
        val focusRequester = remember { FocusRequester() }

        Crossfade(
            modifier = modifier,
            targetState = iconInfoModel.iconCount > 0,
            label = "",
        ) { visible ->
            if (visible) {
                Scaffold(
                    topBar = {
                        HomeTopBar(
                            uiState = HomeTopBarUiState(
                                isSearchExpanded = expandSearch.value,
                                isExpandedScreen = isExpandedScreen,
                                searchedIconInfoModel = searchedIconInfoModel,
                                searchTerm = searchTerm,
                                searchMode = searchMode,
                                isIconPicker = isIconPicker,
                                appIcon = context.appIcon().asImageBitmap(),
                            ),
                            onFocusChange = { expandSearch.value = !expandSearch.value },
                            onClearSearch = { clearSearch() },
                            onChangeMode = { changeMode(it) },
                            onSearchIcons = { searchIcons(it) },
                            onNavigate = onNavigate,
                            onSendResult = onSendResult,
                            focusRequester = focusRequester,
                            scrollBehavior = scrollBehavior,
                        )
                    },
                    bottomBar = {
                        if (!isExpandedScreen) {
                            HomeBottomBar(
                                context = context,
                                iconRequestModel = iconRequestModel,
                                snackbarHostState = snackbarHostState,
                                onNavigate = onNavigate,
                                onExpandSearch = { expandSearch.value = !expandSearch.value },
                                isIconRequestClicked = isIconRequestButtonClicked,
                                onIconRequestClick = {
                                    onIconRequestButtonClicked()
                                },
                            )
                        }
                    },
                    floatingActionButton = {
                        if (isExpandedScreen) {
                            IconRequestFAB(
                                iconRequestModel = iconRequestModel,
                                snackbarHostState = snackbarHostState,
                                lazyGridState = lazyGridState,
                            )
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                ) { contentPadding ->
                    iconInfoModel.let {
                        val padding = contentPadding // Ignore padding value
                        IconPreviewGrid(
                            iconInfo = it.iconInfo,
                            isExpandedScreen = isExpandedScreen,
                            isIconPicker = isIconPicker,
                            onSendResult = onSendResult,
                            gridState = lazyGridState,
                        )
                    }
                }
            } else {
                PlaceholderSearchBar(
                    isExpandedScreen = isExpandedScreen,
                )
            }
        }
        LaunchedEffect(expandSearch.value) {
            if (expandSearch.value) {
                focusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun HomePreview() {
    var searchTerm by remember { mutableStateOf(value = "") }
    val iconInfo = SampleData.iconInfoList

    LawniconsTheme {
        LawniconsSearchBar(
            query = searchTerm,
            isQueryEmpty = searchTerm == "",
            onClearAndBackClick = {
                searchTerm = ""
            },
            onQueryChange = { newValue ->
                searchTerm = newValue
                // No actual searching, this is just a preview
            },
            iconCount = 3,
            onNavigate = {},
            isExpandedScreen = true,
            content = {
                SearchContents(
                    "",
                    SearchMode.LABEL,
                    {},
                    iconInfo = iconInfo,
                )
            },
        )
        IconPreviewGrid(
            iconInfo = iconInfo.toImmutableList(),
            isExpandedScreen = false,
            {},
            Modifier,
            false,
        )
    }
}
