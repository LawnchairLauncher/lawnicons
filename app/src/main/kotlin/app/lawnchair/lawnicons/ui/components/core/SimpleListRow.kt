package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun SimpleListRow(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    startIcon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    tall: Boolean = description != null,
    divider: Boolean = true,
    background: Boolean = false,
    first: Boolean = false,
    last: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    ListRow(
        modifier = modifier,
        startIcon = startIcon,
        endIcon = endIcon,
        tall = tall,
        divider = divider,
        background = background,
        first = first,
        last = last,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        description = {
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
    )
}

@PreviewLawnicons
@Composable
private fun SimpleListRowPreview() {
    LawniconsTheme {
        SimpleListRow(
            label = "Example",
            description = "Example description",
            divider = false,
            background = true,
            first = false,
            last = false,
        )
    }
}
