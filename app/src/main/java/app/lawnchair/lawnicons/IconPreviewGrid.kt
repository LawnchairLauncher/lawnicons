package app.lawnchair.lawnicons

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets

@Composable
@ExperimentalFoundationApi
fun IconPreviewGrid(searchTerm: String) {
    val context = LocalContext.current
    val iconInfo = remember {
        getIconInfo(context = context)
            .associateBy { it.name }
            .values
            .sortedBy { it.name }
    }
    val filteredIconInfo by derivedStateOf {
        val searchLowerCase = searchTerm.lowercase()
        when {
            searchLowerCase.isEmpty() -> iconInfo
            else -> iconInfo.filter { it.name.lowercase().contains(searchLowerCase) }
        }
    }
    val density = LocalDensity.current
    LazyVerticalGrid(
        modifier = Modifier
            .padding(top = 34.dp),
        cells = GridCells.Adaptive(minSize = 80.dp),
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 42.dp,
            end = 8.dp,
            bottom = with(density) { LocalWindowInsets.current.navigationBars.bottom.toDp() }
        )
    ) {
        items(items = filteredIconInfo) { iconInfo ->
            IconPreview(iconId = iconInfo.id)
        }
    }
}
