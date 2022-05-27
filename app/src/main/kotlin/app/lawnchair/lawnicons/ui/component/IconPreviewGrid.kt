package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.util.toPaddingValues

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: List<IconInfo>,
) {
    LazyVerticalGrid(
        modifier = Modifier.padding(top = 26.dp),
        columns = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = WindowInsets.navigationBars.toPaddingValues(
            additionalStart = 8.dp,
            additionalTop = 42.dp,
            additionalEnd = 8.dp,
        ),
    ) {
        items(items = iconInfo) { iconInfo ->
            IconPreview(iconId = iconInfo.id)
        }
    }
}
