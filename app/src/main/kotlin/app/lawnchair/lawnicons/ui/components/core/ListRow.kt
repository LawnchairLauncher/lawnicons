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
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.thenIf
import app.lawnchair.lawnicons.ui.util.thenIfNotNull

private val basePadding = 16.dp

@Composable
fun ListRow(
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    description: (@Composable () -> Unit)? = null,
    startIcon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    tall: Boolean = description != null,
    divider: Boolean = true,
    background: Boolean = false,
    first: Boolean = false,
    last: Boolean = false,
    onClick: (() -> Unit)? = null,
    enforceHeight: Boolean = true,
) {
    val dividerHeight = 1.dp
    val dividerHeightPx = with(LocalDensity.current) { dividerHeight.toPx() }
    val dividerColor = MaterialTheme.colorScheme.outlineVariant
    val topCornerRadius = if (first) 16.dp else 0.dp
    val bottomCornerRadius = if (last) 16.dp else 0.dp
    val basePaddingPx = with(LocalDensity.current) { basePadding.toPx() }

    val backgroundColor = MaterialTheme.colorScheme.surfaceContainer

    Box(
        modifier = modifier
            .fillMaxWidth()
            .semantics(
                mergeDescendants = true,
            ) {}
            .thenIf(enforceHeight) {
                Modifier.height(if (tall) 72.dp else 56.dp)
            }
            .thenIf(background) {
                padding(horizontal = basePadding)
                    .clip(
                        RoundedCornerShape(
                            topStart = topCornerRadius,
                            topEnd = topCornerRadius,
                            bottomStart = bottomCornerRadius,
                            bottomEnd = bottomCornerRadius,
                        ),
                    )
                    .background(
                        backgroundColor,
                    )
            }
            .thenIf(divider) {
                drawBehind {
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
            },
    ) {
        Content(
            label = label,
            description = description,
            icon = startIcon,
            endIcon = endIcon,
            onClick = onClick,
            modifier = contentModifier,
        )
    }
}

@Composable
private fun Content(
    label: @Composable () -> Unit,
    description: (@Composable () -> Unit)?,
    icon: (@Composable () -> Unit)?,
    endIcon: (@Composable () -> Unit)?,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxSize()
            .thenIfNotNull(onClick) { clickable(onClick = it) }
            .padding(horizontal = basePadding),
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(basePadding))
        }
        Column(
            if (endIcon != null) {
                Modifier.weight(0.95f)
            } else {
                Modifier.fillMaxWidth()
            },
        ) {
            label()
            if (description != null) {
                description()
            }
        }
        if (endIcon != null) {
            Spacer(modifier = Modifier.weight(0.05f))
            endIcon()
        }
    }
}
