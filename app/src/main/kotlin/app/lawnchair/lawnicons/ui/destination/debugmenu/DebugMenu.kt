/*
 * Copyright 2025 Lawnchair Launcher
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

package app.lawnchair.lawnicons.ui.destination.debugmenu

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.getFirstLabelAndComponent
import app.lawnchair.lawnicons.data.model.splitByComponentName
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.ListRow
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.util.copyTextToClipboard
import kotlinx.serialization.Serializable

@Serializable
data object DebugMenu

fun NavGraphBuilder.debugMenuDestination(
    onBack: () -> Unit,
    isExpandedScreen: Boolean = false,
) {
    composable<DebugMenu> {
        DebugMenu(
            onBack = onBack,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

// screen to view statistics and other internal info
@Composable
fun DebugMenu(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DebugMenuViewModel = hiltViewModel(),
    isExpandedScreen: Boolean = false,
) {
    val iconInfoModel by viewModel.iconInfoModel.collectAsStateWithLifecycle()
    val newIconsModel by viewModel.newIconsModel.collectAsStateWithLifecycle()
    val iconRequestList by viewModel.iconRequestList.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val prefs = viewModel.preferenceManager

    val debugList = listOf(
        ListItem("Icon count", iconInfoModel.iconCount),
        ListItem("New icons count", newIconsModel.iconCount),
        ListItem("Component count", iconInfoModel.iconInfo.splitByComponentName().size),
        ListItem("Icon request count", iconRequestList?.iconCount ?: 0),
    )

    val prefsList = listOf(
        prefs.forceEnableIconRequest,
    )

    LawniconsScaffold(
        title = "Debug menu",
        onBack = onBack,
        isExpandedScreen = isExpandedScreen,
        modifier = modifier,
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
        ) {
            itemsIndexed(debugList) { index, it ->
                SimpleListRow(
                    label = it.label,
                    description = it.value.toString(),
                    onClick = {
                        context.copyTextToClipboard("${it.label}: ${it.value}")
                    },
                    background = true,
                    first = index == 0,
                    last = index == debugList.lastIndex,
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
            }
            itemsIndexed(prefsList) { index, pref ->
                val interactionSource = remember { MutableInteractionSource() }
                SimpleListRow(
                    label = pref.key,
                    endIcon = {
                        Switch(
                            checked = pref.asState().value,
                            onCheckedChange = pref::set,
                            interactionSource = interactionSource,
                        )
                    },
                    onClick = pref::toggle,
                    background = true,
                    first = index == 0,
                    last = index == prefsList.lastIndex,
                )
            }
            item {
                Spacer(Modifier.height(4.dp))
            }
            item {
                ListRow(
                    label = {
                        Text(
                            text = "Unthemed icons",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    endIcon = {
                        IconButton(
                            onClick = {
                                context.copyTextToClipboard(iconRequestList?.list.toString())
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_copy),
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
            items(iconRequestList?.list ?: emptyList()) {
                ListRow(
                    label = {
                        Text(
                            text = "${it.label}\n${it.componentName.flattenToString()}",
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                    enforceHeight = false,
                )
            }
            item {
                Spacer(Modifier.height(8.dp))
            }
            item {
                ListRow(
                    label = {
                        Text(
                            text = "New icons list",
                            style = MaterialTheme.typography.headlineSmall,
                        )
                    },
                    endIcon = {
                        IconButton(
                            onClick = {
                                context.copyTextToClipboard(
                                    newIconsModel.iconInfo.splitByComponentName().toString(),
                                )
                            },
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_copy),
                                contentDescription = null,
                            )
                        }
                    },
                )
            }
            items(newIconsModel.iconInfo.splitByComponentName()) {
                val labelAndComponent = it.getFirstLabelAndComponent()

                ListRow(
                    label = {
                        Text(
                            text = "${labelAndComponent.label}\n${labelAndComponent.componentName.flattenToString()}",
                            fontFamily = FontFamily.Monospace,
                        )
                    },
                    enforceHeight = false,
                )
            }
        }
    }
}

data class ListItem(
    val label: String,
    val value: Int,
)
