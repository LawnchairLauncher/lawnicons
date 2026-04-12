package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults.rememberTooltipPositionProvider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.theme.icon.Close
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders

@Composable
fun NavigationIconButton(
    label: String,
    onClick: () -> Unit,
    imageVector: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    tint: Color = MaterialTheme.colorScheme.onSurface,
) {
    TooltipBox(
        positionProvider = rememberTooltipPositionProvider(TooltipAnchorPosition.Below, 4.dp),
        tooltip = {
            PlainTooltip {
                Text(label)
            }
        },
        state = rememberTooltipState(),
        modifier = modifier,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .clickable(onClick = onClick),
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = label,
                tint = tint,
            )
        }
    }
}

@PreviewLawnicons
@Composable
private fun NavigationIconButtonPreview() {
    PreviewProviders {
        NavigationIconButton(
            onClick = {},
            imageVector = LawnIcons.Close,
            size = 52.dp,
            label = "Close",
        )
    }
}
