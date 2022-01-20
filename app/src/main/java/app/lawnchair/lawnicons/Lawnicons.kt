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
    val iconInfo by lawniconsViewModel.iconInfo.collectAsState()
    var searchTerm by remember { mutableStateOf(value = "") }

    LawniconsTheme {
        SystemUi {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Crossfade(
                    targetState = iconInfo,
                    modifier = Modifier.statusBarsPadding(),
                ) {
                    if (it != null) {
                        SearchBar(
                            value = searchTerm,
                            iconCount = it.size,
                            onValueChange = { newValue ->
                                searchTerm = newValue
                            },
                        )
                        IconPreviewGrid(
                            iconInfo = it,
                            searchTerm = searchTerm,
                        )
                    } else {
                        SearchBarBase()
                    }
                }
            }
        }
    }
}
