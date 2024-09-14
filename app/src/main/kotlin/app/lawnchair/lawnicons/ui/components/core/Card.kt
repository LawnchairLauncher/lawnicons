package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons

@Composable
fun Card(
    modifier: Modifier = Modifier,
    contentModifier: Modifier = Modifier,
    label: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(modifier = modifier) {
        if (label != null) {
            CardHeader(label)
        }
        Surface(
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = RoundedCornerShape(size = 16.dp),
            modifier = contentModifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
fun CardHeader(label: String, modifier: Modifier = Modifier) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 32.dp, bottom = 6.dp),
    )
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
                first = true,
                last = true,
                divider = false,
            )
        }
    }
}
