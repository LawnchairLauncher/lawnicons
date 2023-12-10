package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Elevation
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun Card(
    modifier: Modifier = Modifier,
    label: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        if (label != null) {
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 32.dp, bottom = 12.dp),
            )
        }
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = Elevation.Level1,
            shape = RoundedCornerShape(size = 16.dp),
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Column {
                content()
            }
        }
    }
}

@PreviewLawnicons
@Composable
private fun CardPreview() {
    LawniconsTheme {
        Card(
            label = "Example",
        ) {
            SimpleListRow(
                label = "Example text in card",
                background = true,
                first = true,
                last = true,
                divider = false,
            )
        }
    }
}
