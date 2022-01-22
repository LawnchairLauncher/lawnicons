package app.lawnchair.lawnicons

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.insets.statusBarsPadding

@Composable
@ExperimentalFoundationApi
fun Lawnicons(lawniconsViewModel: LawniconsViewModel = viewModel()) {
    val iconInfoModel by lawniconsViewModel.iconInfoModel.collectAsState()
    var searchTerm by remember { mutableStateOf(value = "") }

    LawniconsTheme {
        SystemUi {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
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
        }
    }
}
