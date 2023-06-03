package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow

@Composable
fun IconPopupHint(
    isButtonShown: MutableState<Boolean>
) {
    if (isButtonShown.value) {
        Spacer(modifier = Modifier.height(12.dp))
        SimpleListRow(
            background = true,
            first = true,
            last = true,
            divider = false,
            label = "Tap an icon to view additional info.",
            onClick = {
                isButtonShown.value = false
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
        )
    }
}

