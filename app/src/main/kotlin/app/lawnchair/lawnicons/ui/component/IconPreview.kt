package app.lawnchair.lawnicons.ui.component

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun IconPreview(
    iconInfo: IconInfo,
) {
    val isIconInfoShown = remember { mutableStateOf(false) }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(all = 8.dp)
            .aspectRatio(ratio = 1F)
            .clip(shape = CircleShape)
            .background(
                color = if (isIconInfoShown.value) {
                    MaterialTheme.colorScheme.surfaceVariant
                } else {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(
                        Elevation.Level1,
                    )
                },
            )
            .clickable(onClick = { isIconInfoShown.value = true }),
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
