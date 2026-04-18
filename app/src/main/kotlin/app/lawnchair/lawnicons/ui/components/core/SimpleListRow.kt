package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceContainerColor
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SimpleListRow(
    label: String,
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    description: String? = null,
    startIcon: (@Composable () -> Unit)? = null,
    endIcon: (@Composable () -> Unit)? = null,
    divider: Boolean = true,
    background: Boolean = false,
    shapes: ListItemShapes = ListRowDefaults.singleItemShapes,
    onClick: (() -> Unit)? = null,
) {
    Column(modifier) {
        SegmentedListItem(
            onClick = {
                if (onClick != null) {
                    onClick()
                }
            },
            shapes = shapes,
            modifier = contentModifier.padding(
                horizontal = ListRowDefaults.basePadding,
            ),
            content = {
                ListRowLabel(label)
            },
            supportingContent =
            if (description != null) {
                {
                    ListRowDescription(description)
                }
            } else {
                null
            },
            leadingContent = startIcon,
            trailingContent = endIcon,
            colors = ListItemDefaults.segmentedColors(
                containerColor = if (background) adaptiveSurfaceContainerColor else Color.Transparent,
            ),
        )
        if (divider) {
            Spacer(Modifier.height(ListItemDefaults.SegmentedGap))
        }
    }
}

@Composable
fun ListRowLabel(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        modifier = modifier,
        maxLines = 1,
        style = MaterialTheme.typography.bodyLarge,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun ListRowDescription(description: String, modifier: Modifier = Modifier) {
    Text(
        text = description,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLawnicons
@Composable
private fun SimpleListRowPreview() {
    PreviewProviders {
        SimpleListRow(
            label = "Example",
            description = "Example description",
            divider = false,
            background = true,
        )
    }
}
