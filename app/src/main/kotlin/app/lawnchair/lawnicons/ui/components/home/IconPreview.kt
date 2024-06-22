package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import kotlin.math.ln

private fun ColorScheme.iconColor(): Color {
    val elevation = 3.dp
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return primary.copy(alpha = alpha).compositeOver(surface)
}

@Composable
fun IconPreview(
    iconInfo: IconInfo,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    iconBackground: Color? = null,
    isIconPicker: Boolean = false,
) {
    val isIconInfoShown = rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .padding(all = 8.dp)
            .aspectRatio(ratio = 1F)
            .clip(shape = CircleShape)
            .clickable(
                onClick = {
                    if (isIconPicker) {
                        onSendResult(iconInfo)
                    } else {
                        isIconInfoShown.value = true
                    }
                },
            )
            .background(
                color = iconBackground ?: if (isIconInfoShown.value) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.iconColor()
                },
            ),
    ) {
        if (LocalInspectionMode.current) {
            val icon = when (iconInfo.id) {
                1 -> Icons.Rounded.Email
                2 -> Icons.Rounded.Search
                3 -> Icons.Rounded.Call
                else -> Icons.Rounded.Warning
            }
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.6f),
                tint = if (isIconInfoShown.value) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        } else {
            Icon(
                painter = painterResource(iconInfo.id),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(0.6f),
                tint = if (isIconInfoShown.value) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.onPrimaryContainer
                },
            )
        }
    }
    AnimatedVisibility(isIconInfoShown.value) {
        IconInfoSheet(
            iconInfo = iconInfo,
        ) {
            isIconInfoShown.value = it
        }
    }
}

@PreviewLawnicons
@Composable
private fun IconPreviewComposablePreview() {
    LawniconsTheme {
        IconPreview(
            iconInfo = SampleData.iconInfoSample,
            {},
        )
    }
}
