package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import app.lawnchair.lawnicons.ui.util.toPaddingValues
import kotlinx.collections.immutable.ImmutableList

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(
    iconInfo: ImmutableList<IconInfo>,
    isExpandedScreen: Boolean,
) {
    LazyVerticalGrid(
        modifier = Modifier
            .statusBarsPadding()
            .padding(top = 26.dp),
        columns = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = if (!isExpandedScreen) {
            WindowInsets.navigationBars.toPaddingValues(
                additionalStart = 8.dp,
                additionalTop = 42.dp,
                additionalEnd = 8.dp,
            )
        } else {
            WindowInsets.navigationBars.toPaddingValues(
                additionalStart = 32.dp,
                additionalTop = 42.dp,
                additionalEnd = 32.dp,
            )
        },

    ) {
        items(items = iconInfo) { iconInfo ->
            IconPreview(
                iconInfo = iconInfo,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@PreviewLawnicons
@Composable
private fun IconGridPreview() {
    LawniconsTheme {
        IconPreviewGrid(
            SampleData.iconInfoList,
            true,
        )
    }
}
