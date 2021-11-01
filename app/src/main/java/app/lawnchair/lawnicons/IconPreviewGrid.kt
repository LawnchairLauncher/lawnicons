package app.lawnchair.lawnicons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid() {
    val context = LocalContext.current
    val iconIds = remember { getIconIds(context = context) }
    val density = LocalDensity.current
    LazyVerticalGrid(
        cells = GridCells.Fixed(count = 5),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = with(density) { LocalWindowInsets.current.statusBars.top.toDp() },
            end = 8.dp,
            bottom = with(density) { LocalWindowInsets.current.navigationBars.bottom.toDp() }
        )
    ) {
        items(items = iconIds) { iconId ->
            IconPreview(iconId = iconId)
        }
    }
}