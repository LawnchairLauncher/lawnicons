package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

@Composable
fun ClickableIcon(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    onClick: () -> Unit,
    imageVector: ImageVector,
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