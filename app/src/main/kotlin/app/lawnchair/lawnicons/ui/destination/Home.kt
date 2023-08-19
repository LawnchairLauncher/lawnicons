package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import app.lawnchair.lawnicons.ui.components.home.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.LawniconsSearchBar
import app.lawnchair.lawnicons.ui.components.home.PlaceholderSearchBar
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Home(
    lawniconsViewModel: LawniconsViewModel = hiltViewModel(),
    onNavigate: (String) -> Unit,
    isExpandedScreen: Boolean,
) {
    val iconInfoModel by lawniconsViewModel.iconInfoModel.collectAsState()
    val searchedIconInfoModel by lawniconsViewModel.searchedIconInfoModel.collectAsState()
    var searchTerm by rememberSaveable { mutableStateOf(value = "") }

    Crossfade(
        targetState = iconInfoModel != null,
        label = "",
    ) { targetState ->
        if (targetState) {
            searchedIconInfoModel?.let {
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
                    iconInfo = it,
                    onNavigate = onNavigate,
                    isExpandedScreen = isExpandedScreen,
                )
            }
            iconInfoModel?.let {
                IconPreviewGrid(iconInfo = it.iconInfo, isExpandedScreen = isExpandedScreen)
            }
        } else {
            PlaceholderSearchBar()
        }
    }
}
