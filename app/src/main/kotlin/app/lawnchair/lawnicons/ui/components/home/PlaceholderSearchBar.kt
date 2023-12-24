package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun PlaceholderSearchBar() {
    Row(
        content = {},
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .zIndex(1f)
            .statusBarsPadding()
            .padding(top = 8.dp)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(56.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level3),
                shape = RoundedCornerShape(100),
            ),
    )
}

@PreviewLawnicons
@Composable
private fun PlaceholderSearchBarPreview() {
    LawniconsTheme {
        PlaceholderSearchBar()
    }
}
