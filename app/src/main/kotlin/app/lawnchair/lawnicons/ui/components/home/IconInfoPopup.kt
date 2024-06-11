package app.lawnchair.lawnicons.ui.components.home

import android.content.ClipData
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.components.IconLink
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconInfoPopup(
    iconInfo: IconInfo,
    modifier: Modifier = Modifier,
    isPopupShown: (Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = {
            isPopupShown(false)
        },
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets(0.dp)
        },
        modifier = modifier
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
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
            }

            val githubName = iconInfo.drawableName.replace(
                oldValue = "_foreground",
                newValue = "",
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                IconLink(
                    iconResId = R.drawable.github_foreground,
                    label = stringResource(id = R.string.view_on_github),
                    url = "https://github.com/LawnchairLauncher/lawnicons/blob/develop/svgs/$githubName.svg",
                )
            }

            Card(
                label = stringResource(id = R.string.drawable),
            ) {
                SimpleListRow(
                    label = githubName,
                    description = stringResource(R.string.icon_info_outdated_warning),
                    divider = false,
                )
            }
            Spacer(Modifier.height(16.dp))

            Card(
                label = stringResource(id = R.string.mapped_components),
            ) {
                val data = iconInfo.componentNames
                data.forEachIndexed { index, (name, componentName) ->
                    SimpleListRow(
                        label = name,
                        description = componentName,
                        first = index == 0,
                        last = index == data.lastIndex,
                        endIcon = {
                            val context = LocalContext.current
                            IconButton(onClick = {
                                copyTextToClipboard(context, "${name}%0A${componentName}")
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.copy_to_clipboard),
                                    contentDescription = stringResource(
                                        R.string.copy_to_clipboard,
                                    ),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        },
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

private fun copyTextToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText(context.getString(R.string.copied_text), text)
    clipboard.setPrimaryClip(clip)
}

@PreviewLawnicons
@Composable
private fun IconInfoPopupPreview() {
    val showPopup = remember { mutableStateOf(true) }
    LawniconsTheme {
        IconInfoPopup(
            iconInfo = SampleData.iconInfoSample
        ) {
            showPopup.value = it
        }
    }
}
