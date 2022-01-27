package app.lawnchair.lawnicons

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Home(lawniconsViewModel: LawniconsViewModel = hiltViewModel()) {
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
