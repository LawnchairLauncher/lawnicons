package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun IconPreview(
    iconInfo: IconInfo,
    iconBackground: Color? = null,
) {
    val isIconInfoShown = remember { mutableStateOf(false) }

    val modifier = Modifier
        .padding(all = 8.dp)
        .aspectRatio(ratio = 1F)
        .clip(shape = CircleShape)
        .clickable(onClick = { isIconInfoShown.value = true })
        .background(
            color = iconBackground ?: if (isIconInfoShown.value) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.surfaceColorAtElevation(
                    Elevation.Level1,
                )
            },
        )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier,
    ) {
        Icon(
            painter = painterResource(id = iconInfo.id),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(0.6f),
            tint = if (isIconInfoShown.value) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onBackground
            },
        )
    }
    if (isIconInfoShown.value) {
        IconInfoPopup(
            iconInfo = iconInfo,
            isPopupShown = isIconInfoShown,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun IconPreviewComposablePreview() {
    LawniconsTheme {
        IconPreview(
            iconInfo = IconInfo(
                name = "Camera",
                drawableName = "@drawable/camera",
                packageName = "com.android.camera",
                id = R.drawable.camera,
            ),
        )
    }
}
