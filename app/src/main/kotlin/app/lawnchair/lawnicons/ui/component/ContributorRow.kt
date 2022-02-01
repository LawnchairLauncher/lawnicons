package app.lawnchair.lawnicons.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material3.Text
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
) {
    val context = LocalContext.current

    ListItem(
        modifier = Modifier.clickable {
            val website = Uri.parse(photoUrl)
            val intent = Intent(Intent.ACTION_VIEW, website)
            context.startActivity(intent)
        },
        icon = {
            Image(
                contentDescription = name,
                painter = rememberImagePainter(
                    data = photoUrl,
                    builder = { crossfade(enable = true) },
                ),
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
            )
        }
    ) {
        Text(text = name)
    }
}
