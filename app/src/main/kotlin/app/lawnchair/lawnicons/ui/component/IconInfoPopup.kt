package app.lawnchair.lawnicons.ui.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import app.lawnchair.lawnicons.ui.util.StringConstants

@Composable
fun IconInfoPopup(
    @DrawableRes iconId: Int,
    iconName: String,
    iconDrawableName: String,
    iconPackageName: String,
    isPopupShown: MutableState<Boolean>,
) {
    Dialog(
        onDismissRequest = { isPopupShown.value = false },
    ) {
        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10),
                )
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(200.dp),
            )
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    buildAnnotatedString {
                        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Drawable:" + StringConstants.NON_BREAKABLE_SPACE_CHARACTER)
                        }
                        append(iconDrawableName)
                    },
                )
                Text(
                    buildAnnotatedString {
                        pushStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground))
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("Package:" + StringConstants.NON_BREAKABLE_SPACE_CHARACTER)
                        }
                        append(
                            iconName.replace(
                                StringConstants.SPACE_CHARACTER,
                                StringConstants.NON_BREAKABLE_SPACE_CHARACTER,
                            ),
                        )
                        append("($iconPackageName)")
                    },
                    softWrap = true,
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                TextButton(onClick = { isPopupShown.value = false }) {
                    Text(text = "OK")
                }
            }
        }
    }
}
