package app.lawnchair.lawnicons.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun IconPreview(
    @DrawableRes iconId: Int,
    iconName: String,
    iconDrawableName: String,
    iconPackageName: String,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(all = 8.dp)
            .aspectRatio(ratio = 1F)
            .clip(shape = CircleShape)
            .background(
                color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level1),
            ),
    ) {
        val isIconInfoShown = remember { mutableStateOf(false) }
        ClickableIcon(
            painter = painterResource(id = iconId),
            modifier = Modifier,
            tint = MaterialTheme.colorScheme.onBackground,
            onClick = { isIconInfoShown.value = true },
        )
        if (isIconInfoShown.value) {
            IconInfoPopup(
                iconId = iconId,
                iconDrawableName = iconDrawableName,
                iconPackageName = iconPackageName,
                iconName = iconName,
                isPopupShown = isIconInfoShown,
            )
        }
    }
}
