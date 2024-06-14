package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.SearchMode
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    onNavigate: (String) -> Unit,
    onSendResult: (IconInfo) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    lawniconsViewModel: LawniconsViewModel = hiltViewModel(),
) {
    with(lawniconsViewModel) {
        val iconInfoModel by iconInfoModel.collectAsStateWithLifecycle()
        val searchedIconInfoModel by searchedIconInfoModel.collectAsStateWithLifecycle()
        val iconRequestModel by iconRequestList.collectAsStateWithLifecycle()
        val searchMode = searchMode
        val searchTerm = searchTerm

        val lazyGridState = rememberLazyGridState()

        Crossfade(
            modifier = modifier,
            targetState = iconInfoModel != null,
            label = "",
        ) { visible ->
            if (visible) {
                Scaffold(
                    topBar = {
                        searchedIconInfoModel?.let {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                LawniconsSearchBar(
                                    query = searchTerm,
                                    isQueryEmpty = searchTerm == "",
                                    onClearAndBackClick = {
                                        lawniconsViewModel.clearSearch()
                                    },
                                    onQueryChange = { newValue ->
                                        lawniconsViewModel.searchIcons(newValue)
                                    },
                                    iconInfoModel = it,
                                    onNavigate = onNavigate,
                                    isExpandedScreen = isExpandedScreen,
                                    isIconPicker = isIconPicker,
                                    content = {
                                        SearchContents(
                                            searchTerm = searchTerm,
                                            searchMode = searchMode,
                                            onModeChange = { mode ->
                                                lawniconsViewModel.changeMode(mode)
                                            },
                                            iconInfo = it.iconInfo,
                                            onSendResult = {
                                                onSendResult(it)
                                            },
                                        )
                                    },
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        IconRequestFAB(
                            iconRequestModel = iconRequestModel,
                            lazyGridState = lazyGridState,
                        )
                    },
                ) { contentPadding ->
                    iconInfoModel?.let {
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
