package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedListItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.ListRowDefaults
import app.lawnchair.lawnicons.ui.components.core.placeholder.PlaceholderHighlight
import app.lawnchair.lawnicons.ui.components.core.placeholder.fade
import app.lawnchair.lawnicons.ui.components.core.placeholder.placeholder
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceContainerColor
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ContributorRowPlaceholder(
    modifier: Modifier = Modifier,
    shapes: ListItemShapes = ListRowDefaults.singleItemShapes,
) {
    SegmentedListItem(
        onClick = { },
        shapes = shapes,
        colors = ListItemDefaults.colors(
            containerColor = adaptiveSurfaceContainerColor,
        ),
        modifier = modifier.padding(
            horizontal = ListRowDefaults.basePadding,
        ),
        content = {
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(18.dp)
                    .placeholder(
                        visible = true,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .placeholder(
                        visible = true,
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceContainerHighest,
                        highlight = PlaceholderHighlight.fade(),
                    ),
            )
        },
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@PreviewLawnicons
@Composable
private fun ContributorRowPlaceholderPreview() {
    PreviewProviders {
        ContributorRowPlaceholder()
    }
}
