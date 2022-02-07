package app.lawnchair.lawnicons.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter

@Composable
@OptIn(ExperimentalMaterialApi::class)
fun ContributorRow(
    name: String,
    photoUrl: String,
    profileUrl: String? = null,
    socialUrl: String? = null,
    description: String? = null,
    divider: Boolean = true,
    background: Boolean = false,
    first: Boolean = false,
    last: Boolean = false,
) {
    val context = LocalContext.current
    val url = profileUrl ?: socialUrl
    val onClick = if (url != null) {
        {
            val website = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, website)
            context.startActivity(intent)
        }
    } else null

    SimpleListRow(
        background = background,
        first = first,
        last = last,
        divider = divider,
        label = name,
        description = description,
        onClick = onClick,
        icon = {
            Image(
                contentDescription = name,
                painter = rememberImagePainter(
                    data = photoUrl,
                    builder = { crossfade(enable = true) },
                ),
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape),
            )
        },
    )
}
