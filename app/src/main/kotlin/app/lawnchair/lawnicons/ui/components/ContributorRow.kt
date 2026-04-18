package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemShapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.icon.Check
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders
import app.lawnchair.lawnicons.ui.util.visitUrl
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContributorRow(
    name: String,
    photoUrl: String,
    shapes: ListItemShapes,
    modifier: Modifier = Modifier,
    profileUrl: String? = null,
    socialUrl: String? = null,
    description: String? = null,
    divider: Boolean = true,
) {
    val context = LocalContext.current
    val url = profileUrl ?: socialUrl
    val onClick = if (url != null) {
        { context.visitUrl(url) }
    } else {
        null
    }

    SimpleListRow(
        label = name,
        modifier = modifier,
        description = description,
        startIcon = {
            if (LocalInspectionMode.current) {
                Icon(imageVector = LawnIcons.Check, contentDescription = null)
            } else {
                AsyncImage(
                    contentDescription = name,
                    model = ImageRequest.Builder(context = LocalContext.current)
                        .data(data = photoUrl)
                        .crossfade(enable = true)
                        .build(),
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )
            }
        },
        divider = divider,
        background = true,
        shapes = shapes,
        onClick = onClick,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLawnicons
@Composable
private fun ContributorRowPreview() {
    PreviewProviders {
        ContributorRow(
            name = "User",
            photoUrl = "https://lawnchair.app/images/lawnchair.png",
            description = "The Lawnchair Logo",
            shapes = ListRowDefaults.singleItemShapes,
        )
    }
}
