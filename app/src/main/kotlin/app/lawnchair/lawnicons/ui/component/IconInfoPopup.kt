package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.util.StringConstants

@Composable
fun IconInfoPopup(
    iconInfo: IconInfo,
    isPopupShown: MutableState<Boolean>,
) {
    AlertDialog(
        onDismissRequest = { isPopupShown.value = false },
        title = { Text(text = iconInfo.name) },
        icon = {
            Icon(
                painter = painterResource(id = iconInfo.id),
                contentDescription = iconInfo.drawableName,
                modifier = Modifier.size(250.dp),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        },
        confirmButton = {
            TextButton(onClick = { isPopupShown.value = false }) {
                Text(text = stringResource(id = R.string.ok_button))
            }
        },
        text = {
            Text(
                buildAnnotatedString {
                    pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(
                            stringResource(
                                id = R.string.drawable_prefix,
                            ) + StringConstants.NON_BREAKABLE_SPACE_CHARACTER,
                        )
                    }
                    append(iconInfo.drawableName)
                    append("\n")
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(
                            stringResource(
                                id = R.string.package_prefix,
                            ) + StringConstants.NON_BREAKABLE_SPACE_CHARACTER,
                        )
                    }
                    append(iconInfo.packageName)
                },
            )
        },
    )
}
