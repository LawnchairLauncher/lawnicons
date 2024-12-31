package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun NavigationIconButton(
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = tint,
        )
    }
}

@PreviewLawnicons
@Composable
private fun NavigationIconButtonPreview() {
    LawniconsTheme {
        NavigationIconButton(
            imageVector = Icons.Rounded.Clear,
            size = 52.dp,
            onClick = {},
        )
    }
}
