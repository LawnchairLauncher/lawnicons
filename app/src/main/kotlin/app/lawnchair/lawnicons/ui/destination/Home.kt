package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.model.SearchMode
import app.lawnchair.lawnicons.ui.components.home.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.search.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.search.PlaceholderSearchBar
import app.lawnchair.lawnicons.ui.components.home.search.SearchContents
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

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
    val iconInfoModel by lawniconsViewModel.iconInfoModel.collectAsState()
    val searchedIconInfoModel by lawniconsViewModel.searchedIconInfoModel.collectAsState()
    val searchMode = lawniconsViewModel.searchMode
    var searchTerm by rememberSaveable { mutableStateOf(value = "") }

    Crossfade(
        modifier = modifier,
        targetState = iconInfoModel != null,
        label = "",
    ) { targetState ->
        if (targetState) {
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
                                    searchTerm = ""
                                    lawniconsViewModel.searchIcons("")
                                },
                                onQueryChange = { newValue ->
                                    searchTerm = newValue
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
                                        onModeChange = {
                                            lawniconsViewModel.changeMode(it)
                                            // Refresh search results
                                            lawniconsViewModel.searchIcons(searchTerm)
                                        },
                                        iconInfo = it.iconInfo,
                                        onSendResult = onSendResult,
                                    )
                                },
                            )
                        }
                    }
                },
            ) { contentPadding ->
                iconInfoModel?.let {
                    val padding = contentPadding
                    IconPreviewGrid(
                        iconInfo = it.iconInfo,
                        isExpandedScreen = isExpandedScreen,
                        isIconPicker = isIconPicker,
                        onSendResult = onSendResult,
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
                    SearchMode.NAME,
                    {},
                    iconInfo = iconInfo,
                )
            },
        )
        IconPreviewGrid(
            iconInfo = iconInfo,
            isExpandedScreen = false,
            {},
            Modifier,
            false,
        )
    }
}
