package app.lawnchair.lawnicons.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconInfoPopup(
    iconInfo: IconInfo,
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
        windowInsets = WindowInsets(0.dp),
        modifier = Modifier
            .navigationBarsPadding(),
    ) {
        Column(
            modifier = Modifier
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
                    description = "If the icon is outdated due to rebranding, create an issue on GitHub.",
                    divider = false,
                )
            }
            Spacer(Modifier.height(16.dp))

            Card(
                label = stringResource(id = R.string.package_prefix),
            ) {
                SimpleListRow(
                    label = iconInfo.name,
                    description = iconInfo.packageName,
                    divider = false,
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
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
