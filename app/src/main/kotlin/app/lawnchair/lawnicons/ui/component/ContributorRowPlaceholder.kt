package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.surfaceColorAtElevation
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.fade
import com.google.accompanist.placeholder.material.placeholder

@Preview(showBackground = true)
@Composable
@OptIn(ExperimentalMaterialApi::class)
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
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level4),
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
                            color = MaterialTheme.colorScheme.surfaceColorAtElevation(Elevation.Level4),
                            highlight = PlaceholderHighlight.fade(),
                        )
                )
            }
        )
    }
}
