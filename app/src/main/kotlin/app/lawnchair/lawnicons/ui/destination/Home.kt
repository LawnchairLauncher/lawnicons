package app.lawnchair.lawnicons.ui.destination

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import app.lawnchair.lawnicons.ui.component.IconPreviewGrid
import app.lawnchair.lawnicons.ui.component.SearchBar
import app.lawnchair.lawnicons.ui.component.SearchBarBase
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Home(
    lawniconsViewModel: LawniconsViewModel = hiltViewModel(),
    navController: NavController,
) {
    val iconInfoModel by lawniconsViewModel.iconInfoModel.collectAsState()
    var searchTerm by remember { mutableStateOf(value = "") }
    Crossfade(
        targetState = iconInfoModel != null,
        modifier = Modifier.statusBarsPadding(),
    ) { targetState ->
        if (targetState) {
            iconInfoModel?.let {
                SearchBar(
                    value = searchTerm,
                    iconCount = it.iconCount,
                    navController = navController,
                    onValueChange = { newValue ->
                        searchTerm = newValue
                        lawniconsViewModel.searchIcons(newValue)
                    },
                )
                IconPreviewGrid(iconInfo = it.iconInfo)
            }
        } else {
            SearchBarBase()
        }
    }
}
