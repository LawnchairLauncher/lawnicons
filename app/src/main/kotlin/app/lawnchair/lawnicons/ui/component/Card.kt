package app.lawnchair.lawnicons.ui.component

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
import app.lawnchair.lawnicons.ui.util.Elevation

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
                modifier = modifier.padding(start = 32.dp, bottom = 12.dp),
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
