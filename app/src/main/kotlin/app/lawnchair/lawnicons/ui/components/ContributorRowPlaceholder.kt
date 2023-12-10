package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.ListRow
import app.lawnchair.lawnicons.ui.components.core.placeholder.PlaceholderHighlight
import app.lawnchair.lawnicons.ui.components.core.placeholder.material.fade
import app.lawnchair.lawnicons.ui.components.core.placeholder.material.placeholder
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation

@Composable
fun ContributorRowPlaceholder(
    first: Boolean = false,
    last: Boolean = false,
    divider: Boolean = true,
) {
    Row {
        ListRow(
            divider = divider,
            background = true,
            first = first,
            last = last,
            icon = {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .placeholder(
                            visible = true,
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                Elevation.Level4,
                            ),
                            highlight = PlaceholderHighlight.fade(),
                        ),
                )
            },
            label = {
                Box(
                    modifier = Modifier
                        .width(96.dp)
                        .height(18.dp)
                        .placeholder(
                            visible = true,
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(
                                Elevation.Level4,
                            ),
                            highlight = PlaceholderHighlight.fade(),
                        ),
                )
            },
        )
    }
}

@PreviewLawnicons
@Composable
private fun ContributorRowPlaceholderPreview() {
    LawniconsTheme {
        ContributorRowPlaceholder()
    }
}
