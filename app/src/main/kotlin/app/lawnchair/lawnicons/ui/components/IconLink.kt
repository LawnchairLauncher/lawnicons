package app.lawnchair.lawnicons.ui.components

import android.content.Intent
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun IconLink(
    @DrawableRes iconResId: Int,
    label: String,
    url: String,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val inPreviewMode = LocalInspectionMode.current
    IconLink(
        iconResId = iconResId,
        label = label,
        onClick = {
            if (!inPreviewMode) {
                val webpage = Uri.parse(url)
                val intent = Intent(Intent.ACTION_VIEW, webpage)
                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                }
            }
        },
        modifier = modifier,
    )
}

@Composable
fun IconLink(
    @DrawableRes iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val inPreviewMode = LocalInspectionMode.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .padding(horizontal = 16.dp)
            .height(64.dp)
            .clip(MaterialTheme.shapes.medium)
            .clickable {
                onClick()
            },
    ) {
        if (!inPreviewMode) {
            Image(
                painterResource(id = iconResId),
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalContentColor.current),
                modifier = Modifier
                    .size(24.dp),
            )
        } else {
            Image(
                Icons.Rounded.Star,
                contentDescription = null,
                colorFilter = ColorFilter.tint(color = LocalContentColor.current),
                modifier = Modifier
                    .size(24.dp),
            )
        }
        Spacer(modifier = Modifier.requiredHeight(4.dp))
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@PreviewLawnicons
@Composable
private fun FancyButtonLinkPreview() {
    LawniconsTheme {
        Surface {
            IconLink(
                iconResId = 0,
                label = "Example",
                url = "https://example.com",
            )
        }
    }
}
