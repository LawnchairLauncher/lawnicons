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

package app.lawnchair.lawnicons.ui.components.home.iconpreview

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.components.IconLink
import app.lawnchair.lawnicons.ui.components.core.ListRow
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Constants
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.SampleData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconInfoSheet(
    iconInfo: IconInfo,
    modifier: Modifier = Modifier,
    isPopupShown: (Boolean) -> Unit,
) {
    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
    )

    val groupedComponents = rememberSaveable {
        iconInfo.componentNames
            .groupBy { it.label }
            .map { (label, components) ->
                label to components.map { it.componentName }
            }
    }

    val githubName = iconInfo.drawableName.replace(
        oldValue = "_foreground",
        newValue = "",
    )

    val shareContents = rememberSaveable { getShareContents(githubName, groupedComponents) }

    ModalBottomSheet(
        onDismissRequest = {
            isPopupShown(false)
        },
        sheetState = sheetState,
        contentWindowInsets = {
            WindowInsets(0.dp)
        },
        modifier = modifier,
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
        ) {
            item {
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
            }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    IconLink(
                        iconResId = R.drawable.github_foreground,
                        label = stringResource(id = R.string.view_on_github),
                        url = "${Constants.GITHUB}/blob/develop/svgs/$githubName.svg",
                    )
                    Spacer(Modifier.width(16.dp))
                    IconLink(
                        iconResId = R.drawable.share_icon,
                        label = stringResource(id = R.string.share),
                        onClick = {
                            val intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareContents)
                                type = "text/plain"
                            }

                            val shareIntent = Intent.createChooser(intent, null)
                            context.startActivity(shareIntent)
                        },
                    )
                }
            }
            item {
                LinkHeader(
                    label = stringResource(id = R.string.drawable),
                )
            }
            item {
                ListRow(
                    label = {
                        Text(githubName)
                    },
                    description = {
                        Text(
                            text = stringResource(R.string.icon_info_outdated_warning),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    divider = false,
                    enforceHeight = false,
                )
            }
            item {
                Spacer(Modifier.height(16.dp))
            }
            item {
                LinkHeader(
                    label = stringResource(id = R.string.mapped_components),
                )
            }
            items(groupedComponents) { (label, componentName) ->
                IconInfoListRow(label, componentName)
            }
            item {
                Spacer(Modifier.height(24.dp))
            }
            item {
                Spacer(Modifier.navigationBarsPadding())
            }
        }
    }
}

private fun getShareContents(
    githubName: String,
    groupedComponents: List<Pair<String, List<String>>>,
): String {
    val formattedComponents =
        groupedComponents.joinToString(separator = "\n") { (group, components) ->
            val componentList = components.joinToString(separator = "\n") { it }
            "$group:\n$componentList"
        }
    return "Drawable: $githubName\n\nMapped components: \n$formattedComponents"
}

@Composable
private fun LinkHeader(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier.padding(start = 16.dp, bottom = 6.dp),
    )
}

@Composable
private fun IconInfoListRow(
    label: String,
    componentNames: List<String>,
) {
    SelectionContainer {
        ListRow(
            label = {
                Text(
                    text = label,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            },
            description = {
                Spacer(Modifier.height(4.dp))
                Column {
                    componentNames.forEach {
                        Text(
                            text = it,
                            maxLines = 2,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(6.dp))
                    }
                }
            },
            divider = false,
            enforceHeight = false,
        )
        Spacer(Modifier.height(16.dp))
    }
}

@PreviewLawnicons
@Composable
private fun IconInfoPopupPreview() {
    val showPopup = remember { mutableStateOf(true) }
    LawniconsTheme {
        IconInfoSheet(
            iconInfo = SampleData.iconInfoSample,
        ) {
            showPopup.value = it
        }
    }
}
