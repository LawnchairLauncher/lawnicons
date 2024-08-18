package app.lawnchair.lawnicons.ui.destination

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel
import kotlinx.collections.immutable.toImmutableList

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
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
        val context = LocalContext.current

        val lazyGridState = rememberLazyGridState()
        val snackbarHostState = remember { SnackbarHostState() }

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
                                isSearchExpanded = expandSearch,
                                isExpandedScreen = isExpandedScreen,
                                searchedIconInfoModel = searchedIconInfoModel,
                                searchTerm = searchTerm,
                                searchMode = searchMode,
                                isIconPicker = isIconPicker,
                            ),
                            onFocusChange = { expandSearch = !expandSearch },
                            onClearSearch = ::clearSearch,
                            onChangeMode = ::changeMode,
                            onSearchIcons = ::searchIcons,
                            onNavigate = onNavigate,
                            onSendResult = onSendResult,
                            focusRequester = focusRequester,
                        )
                    },
                    bottomBar = {
                        if (!isExpandedScreen) {
                            HomeBottomBar(
                                context = context,
                                iconRequestModel = iconRequestModel,
                                snackbarHostState = snackbarHostState,
                                onNavigate = onNavigate,
                                onExpandSearch = { expandSearch = true },
                                isIconRequestClicked = isIconRequestButtonClicked,
                                onIconRequestClick = ::onIconRequestButtonClicked,
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
                ) {
                    IconPreviewGrid(
                        iconInfo = iconInfoModel.iconInfo,
                        isExpandedScreen = isExpandedScreen,
                        isIconPicker = isIconPicker,
                        onSendResult = onSendResult,
                        gridState = lazyGridState,
                    )
                }
            } else {
                PlaceholderSearchBar(
                    isExpandedScreen = isExpandedScreen,
                )
            }
        }
        LaunchedEffect(expandSearch) {
            if (expandSearch) {
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
            onClear = {
                searchTerm = ""
            },
            onBack = {},
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
