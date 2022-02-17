package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import com.google.accompanist.insets.LocalWindowInsets

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: List<IconInfo>,
) {
    val density = LocalDensity.current
    val navigationBarHeight = with(density) {
        LocalWindowInsets.current.navigationBars.bottom.toDp()
    }

    LazyVerticalGrid(
        modifier = Modifier.padding(top = 26.dp),
        cells = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 42.dp,
            end = 8.dp,
            bottom = navigationBarHeight,
        )
    ) {
        items(items = iconInfo) { iconInfo ->
            IconPreview(iconId = iconInfo.id)
        }
    }
}
