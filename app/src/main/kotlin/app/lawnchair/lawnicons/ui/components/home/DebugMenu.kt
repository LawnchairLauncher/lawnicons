/*
 * Copyright 2024 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.ui.components.home

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.BuildConfig
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfoModel
import app.lawnchair.lawnicons.model.IconRequestModel
import app.lawnchair.lawnicons.model.splitByComponentName
import app.lawnchair.lawnicons.repository.BasePreferenceManager
import app.lawnchair.lawnicons.repository.PreferenceManager
import app.lawnchair.lawnicons.repository.preferenceManager
import app.lawnchair.lawnicons.ui.components.core.Card
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceColor
import app.lawnchair.lawnicons.ui.util.copyTextToClipboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugMenu(
    iconInfoModel: IconInfoModel,
    iconRequestModel: IconRequestModel?,
    newIconsInfoModel: IconInfoModel,
) {
    val context = LocalContext.current
    val prefs = preferenceManager()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    ModalBottomSheet(
        onDismissRequest = { prefs.showDebugMenu.set(false) },
        sheetState = sheetState,
        dragHandle = {
            Spacer(Modifier.height(22.dp))
        },
    ) {
        SheetContent(
            iconInfoCount = iconInfoModel.iconInfo.size,
            componentCount = iconInfoModel.iconInfo.splitByComponentName().size,
            newIconInfoList = newIconsInfoModel.toString(),
            newIconInfoCount = newIconsInfoModel.iconInfo.size,
            iconRequestList = iconRequestModel?.list?.joinToString("\n") { "${it.label}\n${it.componentName}" }
                ?: "null",
            iconRequestCount = iconRequestModel?.iconCount ?: 0,
            context,
            prefs,
        )
    }
}

@Composable
private fun SheetContent(
    iconInfoCount: Int,
    componentCount: Int,
    newIconInfoList: String,
    newIconInfoCount: Int,
    iconRequestList: String,
    iconRequestCount: Int,
    context: Context,
    prefs: PreferenceManager,
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .clip(MaterialTheme.shapes.large)
            .background(adaptiveSurfaceColor)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            "Debug menu",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )

        CopyableListItem("Icon count", iconInfoCount, context)
        CopyableListItem("Component count", componentCount, context)
        CopyableListItem("New icon count", newIconInfoCount, context)
        CopyableListItem("Icon request count", iconRequestCount, context)

        SwitchPref(prefs.showDebugMenu)
        SwitchPref(prefs.forceEnableIconRequest)
        SwitchPref(prefs.showFirstLaunchSnackbar)
        SwitchPref(prefs.iconRequestsEnabled)

        CopyableListItem(
            "Current version code",
            prefs.currentLawniconsVersion.asState().value,
            context,
        )
        CopyableListItem("Actual version code", BuildConfig.VERSION_CODE, context)
        Button({ prefs.currentLawniconsVersion.set(0) }) { Text("Reset version code") }

        CopyableList("Icon request list", iconRequestList, context)
        CopyableList("New icons list", newIconInfoList, context)
    }
}

@Composable
private fun CopyableList(
    label: String,
    content: String,
    context: Context,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(top = 16.dp, bottom = 8.dp),
        )
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp),
            ) {
                Text(
                    text = content,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState()),
                )
            }
        }
        TextButton(
            onClick = {
                copyTextToClipboard(context, content)
            },
        ) {
            Text(stringResource(R.string.copy_to_clipboard))
        }
    }
}

@Composable
fun CopyableListItem(
    label: String,
    value: Int,
    context: Context,
    modifier: Modifier = Modifier,
) {
    SimpleListRow(
        label,
        description = value.toString(),
        onClick = {
            copyTextToClipboard(context, "$label: $value")
        },
        modifier = modifier,
    )
}

@Composable
private fun SwitchPref(
    pref: BasePreferenceManager.BoolPref,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    SimpleListRow(
        pref.key,
        endIcon = {
            Switch(
                checked = pref.asState().value,
                onCheckedChange = pref::set,
                interactionSource = interactionSource,
            )
        },
        onClick = pref::toggle,
        modifier = modifier,
    )
}
