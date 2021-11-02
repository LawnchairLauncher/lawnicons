package app.lawnchair.lawnicons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
@ExperimentalFoundationApi
fun Lawnicons() {
    var searchTerm by remember { mutableStateOf(value = "") }
    LawniconsTheme {
        SystemUi {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    SearchBar(
                        value = searchTerm,
                        onValueChange = { searchTerm = it }
                    )
                    IconPreviewGrid(searchTerm = searchTerm)
                }
            }
        }
    }
}