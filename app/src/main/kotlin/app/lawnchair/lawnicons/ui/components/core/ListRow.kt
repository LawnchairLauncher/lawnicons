package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

private val basePadding = 16.dp

@Composable
fun ListRow(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    icon: (@Composable () -> Unit)? = null,
    tall: Boolean = description != null,
    divider: Boolean = true,
    background: Boolean = false,
    first: Boolean = false,
    last: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val height = if (tall) 72.dp else 56.dp
    val dividerHeight = 1.dp
    val dividerHeightPx = with(LocalDensity.current) { dividerHeight.toPx() }
    val dividerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(32.dp)
    val topCornerRadius = if (first) 16.dp else 0.dp
    val bottomCornerRadius = if (last) 16.dp else 0.dp
    val basePaddingPx = with(LocalDensity.current) { basePadding.toPx() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .then(
                if (background) {
                    Modifier
                        .padding(horizontal = basePadding)
                        .clip(
                            RoundedCornerShape(
                                topStart = topCornerRadius,
                                topEnd = topCornerRadius,
                                bottomStart = bottomCornerRadius,
                                bottomEnd = bottomCornerRadius,
                            ),
                        )
                        .background(
                            MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level1),
                        )
                } else {
                    Modifier
                },
            )
            .then(
                if (divider) {
                    Modifier.drawBehind {
                        drawLine(
                            strokeWidth = dividerHeightPx,
                            color = dividerColor,
                            start = Offset(
                                x = basePaddingPx,
                                y = size.height - dividerHeightPx / 2,
                            ),
                            end = Offset(
                                x = size.width - basePaddingPx,
                                y = size.height - dividerHeightPx / 2,
                            ),
                        )
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        Content(
            label = label,
            description = description,
            icon = icon,
            onClick = onClick,
        )
    }
}

@Composable
private fun Content(
    label: @Composable () -> Unit,
    description: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    onClick: (() -> Unit)?,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = basePadding),
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(basePadding))
        }
        Column {
            label()
            if (description != null) {
                description()
            }
        }
    }
}
