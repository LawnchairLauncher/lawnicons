package app.lawnchair.lawnicons.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun IconPreview(@DrawableRes iconId: Int) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(all = 8.dp)
            .aspectRatio(ratio = 1F)
            .clip(shape = CircleShape)
            .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level1)),
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(fraction = 0.6f),
            tint = MaterialTheme.colorScheme.onBackground,
        )
    }
}
