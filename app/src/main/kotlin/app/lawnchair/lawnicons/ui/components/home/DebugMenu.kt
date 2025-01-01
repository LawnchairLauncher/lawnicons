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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
            .verticalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SimpleListRow("Icon count: $iconInfoCount")
        SimpleListRow("Component count: $componentCount")
        SimpleListRow("New icon count: $newIconInfoCount")
        SimpleListRow("Icon request count: $iconRequestCount")

        SwitchPref(prefs.showDebugMenu)
        SwitchPref(prefs.showNewIconsCard)
        SwitchPref(prefs.forceEnableIconRequest)
        SwitchPref(prefs.showFirstLaunchSnackbar)

        SimpleListRow(
            "Current version",
            description = prefs.currentLawniconsVersion.asState().value.toString(),
        )
        SimpleListRow(
            "Actual version",
            description = BuildConfig.VERSION_CODE.toString(),
        )
        Button({ prefs.currentLawniconsVersion.set(0) }) { Text("Reset version code") }

        CopyableList(iconRequestList, context)
        CopyableList(newIconInfoList, context)
    }
}

@Composable
private fun CopyableList(string: String, context: Context) {
    Card {
        Column(
            modifier = Modifier
                .padding(16.dp),
        ) {
            Text(
                text = string,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .horizontalScroll(rememberScrollState()),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = {
                        copyTextToClipboard(context, string)
                    },
                ) {
                    Text(stringResource(R.string.copy_to_clipboard))
                }
            }
        }
    }
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
