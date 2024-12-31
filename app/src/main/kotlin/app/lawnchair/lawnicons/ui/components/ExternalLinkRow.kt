package app.lawnchair.lawnicons.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun ExternalLinkRow(
    name: String,
    url: String,
    modifier: Modifier = Modifier,
    divider: Boolean = true,
    background: Boolean = false,
    first: Boolean = false,
    last: Boolean = false,
    startIcon: @Composable (() -> Unit)? = null,
) {
    val context = LocalContext.current
    val onClick =
        {
            val website = Uri.parse(url)
            val intent = Intent(Intent.ACTION_VIEW, website)
            context.startActivity(intent)
        }

    SimpleListRow(
        modifier = modifier,
        background = background,
        first = first,
        last = last,
        divider = divider,
        label = name,
        onClick = onClick,
        startIcon = startIcon,
    )
}

@PreviewLawnicons
@Composable
private fun ExternalLinkRowPreview() {
    LawniconsTheme {
        ExternalLinkRow(
            name = "User",
            url = "https://lawnchair.app/",
        )
    }
}
