package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData

@Composable
fun IconInfoPopup(
    iconInfo: IconInfo,
    isPopupShown: (Boolean) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { isPopupShown(false) },
        title = { Text(text = iconInfo.name) },
        icon = {
            if (LocalInspectionMode.current) {
                val icon = when (iconInfo.id) {
                    1 -> Icons.Rounded.Email
                    2 -> Icons.Rounded.Search
                    3 -> Icons.Rounded.Call
                    else -> Icons.Rounded.Warning
                }
                Icon(
                    icon,
                    contentDescription = iconInfo.drawableName,
                    modifier = Modifier.size(250.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            } else {
                Icon(
                    painterResource(id = iconInfo.id),
                    contentDescription = iconInfo.drawableName,
                    modifier = Modifier.size(250.dp),
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { isPopupShown(false) }) {
                Text(text = stringResource(id = R.string.ok_button))
            }
        },
        text = {
            Column {
                Text(
                    fontWeight = FontWeight.Bold,
                    text = stringResource(
                        id = R.string.drawable_prefix,
                    ),
                )
                Text(
                    fontFamily = FontFamily.Monospace,
                    text = iconInfo.drawableName,
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    fontWeight = FontWeight.Bold,
                    text = stringResource(
                        id = R.string.package_prefix,
                    ),
                )
                Text(
                    fontFamily = FontFamily.Monospace,
                    text = iconInfo.packageName,
                )
            }
        },
    )
}

@PreviewLawnicons
@Composable
private fun IconInfoPopupPreview() {
    val showPopup = remember { mutableStateOf(true) }
    LawniconsTheme {
        IconInfoPopup(
            iconInfo = SampleData.iconInfoSample,
        ) {
            showPopup.value = it
        }
    }
}
