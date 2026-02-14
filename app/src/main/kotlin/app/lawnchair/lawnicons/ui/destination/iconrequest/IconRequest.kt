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

package app.lawnchair.lawnicons.ui.destination.iconrequest

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SplitButtonDefaults
import androidx.compose.material3.SplitButtonLayout
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.SystemIconInfo
import app.lawnchair.lawnicons.ui.components.AnnouncementDefaults
import app.lawnchair.lawnicons.ui.components.AnnouncementList
import app.lawnchair.lawnicons.ui.components.core.LawniconsScaffold
import app.lawnchair.lawnicons.ui.components.core.ListRowLabel
import app.lawnchair.lawnicons.ui.components.core.SimpleListRow
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceColor
import app.lawnchair.lawnicons.ui.theme.adaptiveSurfaceContainerColor
import app.lawnchair.lawnicons.ui.theme.icon.Copy
import app.lawnchair.lawnicons.ui.theme.icon.IconRequest
import app.lawnchair.lawnicons.ui.theme.icon.KeyboardArrowDown
import app.lawnchair.lawnicons.ui.theme.icon.LawnIcons
import app.lawnchair.lawnicons.ui.theme.icon.Save
import app.lawnchair.lawnicons.ui.theme.icon.Share
import coil.compose.AsyncImage
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object IconRequest : NavKey

fun EntryProviderScope<NavKey>.iconRequestDestination(
    isExpandedScreen: Boolean,
    onBack: () -> Unit,
) {
    entry<IconRequest> {
        IconRequest(
            onBack = onBack,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IconRequest(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    viewModel: IconRequestViewModel = metroViewModel(),
    onBack: () -> Unit,
) {
    val context = LocalContext.current

    val isEnabled by viewModel.isEnabled.collectAsStateWithLifecycle()

    val announcements by viewModel.announcements.collectAsStateWithLifecycle()

    val availableIcons by viewModel.availableIcons.collectAsStateWithLifecycle()
    val isSavingInProgress by viewModel.isSavingInProgress.collectAsStateWithLifecycle()
    val selectedIcons by viewModel.selectedIcons.collectAsStateWithLifecycle()
    val selectedIconsCount = selectedIcons.size

    val scrollBehavior = BottomAppBarDefaults.exitAlwaysScrollBehavior()

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        val uri = result.data?.data
        val wasSuccessful = result.resultCode == Activity.RESULT_OK
        viewModel.saveFile(context = context, uri = uri, wasSuccessful = wasSuccessful)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LawniconsScaffold(
        title = stringResource(R.string.missing_icons),
        isExpandedScreen = isExpandedScreen,
        onBack = onBack,
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            FlexibleBottomAppBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ) {
                val allSelected =
                    availableIcons.isNotEmpty() && selectedIcons.containsAll(availableIcons)

                OutlinedButton(
                    onClick = {
                        if (allSelected) {
                            viewModel.clearSelection()
                        } else {
                            viewModel.selectAll()
                        }
                    },
                ) {
                    Text(
                        stringResource(
                            if (allSelected) R.string.clear_all else R.string.select_all,
                        ),
                    )
                }

                val coroutineScope = rememberCoroutineScope()
                val string = stringResource(R.string.icon_requests_suspended)

                IconRequestButton(
                    enabled = selectedIconsCount > 0 && !isSavingInProgress,
                    isExpandedScreen = isExpandedScreen,
                    onRequest = {
                        if (isEnabled) {
                            viewModel.requestIcons(context)
                        } else {
                            coroutineScope.launch {
                                val result = snackbarHostState
                                    .showSnackbar(
                                        message = string,
                                        duration = SnackbarDuration.Short,
                                    )
                                if (result == SnackbarResult.Dismissed) {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                }
                            }
                        }
                    },
                    onShareFile = {
                        viewModel.shareFile(context)
                    },
                    onSaveFile = {
                        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "application/zip"
                            putExtra(Intent.EXTRA_TITLE, "lawnicons_request.zip")
                        }
                        saveFileLauncher.launch(intent)
                    },
                    onCopyComponents = {
                        viewModel.copyComponents(context)
                    },
                )
            }
        },
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues,
        ) {
            if (announcements.isNotEmpty()) {
                item {
                    AnnouncementList(
                        announcements,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        colors = AnnouncementDefaults.colors(
                            containerColor = adaptiveSurfaceContainerColor,
                        ),
                    )
                }
            }
            item {
                Spacer(Modifier.height(4.dp))
            }
            items(availableIcons) { systemIconInfo ->
                IconRequestRow(
                    systemIconInfo = systemIconInfo,
                    checked = systemIconInfo in selectedIcons,
                    onCheckedChange = {
                        viewModel.toggleSelection(systemIconInfo)
                    },
                )
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private fun IconRequestButton(
    enabled: Boolean,
    onRequest: () -> Unit,
    onShareFile: () -> Unit,
    onSaveFile: () -> Unit,
    onCopyComponents: () -> Unit,
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean = false,
) {
    var checked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .wrapContentSize(),
    ) {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(
                    onClick = onRequest,
                    enabled = enabled,
                ) {
                    Icon(
                        imageVector = LawnIcons.IconRequest,
                        modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize),
                        contentDescription = null,
                    )
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text(stringResource(R.string.request_icons_button))
                }
            },
            trailingButton = {
                val stateDescriptionText = stringResource(
                    if (checked) R.string.toggle_button_expanded else R.string.toggle_button_collapsed,
                )
                val contentDescriptionText =
                    stringResource(R.string.toggle_button_content_description)

                SplitButtonDefaults.TrailingButton(
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                    },
                    enabled = enabled,
                    modifier = Modifier.semantics {
                        stateDescription = stateDescriptionText
                        contentDescription = contentDescriptionText
                    },
                ) {
                    val rotation: Float by
                        animateFloatAsState(
                            targetValue = if (checked) 180f else 0f,
                            label = "Trailing icon rotation",
                        )
                    Icon(
                        imageVector = LawnIcons.KeyboardArrowDown,
                        modifier =
                        Modifier
                            .size(SplitButtonDefaults.TrailingIconSize)
                            .graphicsLayer {
                                this.rotationZ = rotation
                            },
                        contentDescription = stringResource(R.string.show_options_sheet),
                    )
                }
            },
        )

        if (checked) {
            ResponsiveMenu(
                isExpandedScreen = isExpandedScreen,
                menuItems = listOf(
                    MenuItemRow(
                        title = stringResource(R.string.share_file),
                        onClick = onShareFile,
                        icon = {
                            Icon(imageVector = LawnIcons.Share, contentDescription = null)
                        },
                    ),
                    MenuItemRow(
                        title = stringResource(R.string.save_file),
                        onClick = onSaveFile,
                        icon = {
                            Icon(
                                imageVector = LawnIcons.Save,
                                contentDescription = null,
                            )
                        },
                    ),
                    MenuItemRow(
                        title = stringResource(R.string.copy_components),
                        onClick = onCopyComponents,
                        icon = {
                            Icon(
                                imageVector = LawnIcons.Copy,
                                contentDescription = null,
                            )
                        },
                    ),
                ),
                onDismissRequest = {
                    checked = false
                },
            )
        }
    }
}

data class MenuItemRow(
    val title: String,
    val onClick: () -> Unit,
    val icon: @Composable () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ResponsiveMenu(
    menuItems: List<MenuItemRow>,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    isExpandedScreen: Boolean = false,
) {
    if (isExpandedScreen) {
        DropdownMenu(
            expanded = true,
            onDismissRequest = onDismissRequest,
            modifier = modifier,
        ) {
            menuItems.forEach {
                DropdownMenuItem(
                    text = {
                        Text(it.title)
                    },
                    onClick = it.onClick,
                    leadingIcon = it.icon,
                )
            }
        }
    } else {
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
        )
        val coroutineScope = rememberCoroutineScope()
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            modifier = modifier,
        ) {
            LazyColumn {
                itemsIndexed(menuItems) { index, it ->
                    SimpleListRow(
                        label = it.title,
                        startIcon = it.icon,
                        shapes = ListItemDefaults.segmentedShapes(index, menuItems.size),
                    ) {
                        it.onClick()
                        coroutineScope.launch {
                            sheetState.hide()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IconRequestRow(
    systemIconInfo: SystemIconInfo,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(
        selected = checked,
        onClick = {
            onCheckedChange(!checked)
        },
        colors = ListItemDefaults.colors(
            containerColor = adaptiveSurfaceColor,
            selectedContainerColor = adaptiveSurfaceColor,
        ),
        modifier = modifier,
        content = {
            ListRowLabel(systemIconInfo.label)
        },
        supportingContent = {
            Text(
                text = systemIconInfo.componentName.flattenToString(),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        leadingContent = {
            AsyncImage(
                model = systemIconInfo.drawable,
                contentDescription = null,
                modifier = Modifier.requiredSize(48.dp),
            )
        },
        trailingContent = {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}
