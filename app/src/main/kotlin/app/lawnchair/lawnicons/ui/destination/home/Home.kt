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

package app.lawnchair.lawnicons.ui.destination.home

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.ui.components.home.HomeBottomToolbar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBar
import app.lawnchair.lawnicons.ui.components.home.NewIconsCard
import app.lawnchair.lawnicons.ui.components.home.PlaceholderUI
import app.lawnchair.lawnicons.ui.components.home.iconpreview.AppBarListItem
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGridPaddings
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

fun EntryProviderScope<NavKey>.homeDestination(
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onNavigateToIconRequest: () -> Unit,
    onNavigateToDebugMenu: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
) {
    entry<Home> {
        Home(
            onNavigateToAbout = onNavigateToAbout,
            onNavigateToNewIcons = onNavigateToNewIcons,
            onNavigateToIconRequest = onNavigateToIconRequest,
            onNavigateToDebugMenu = onNavigateToDebugMenu,
            isExpandedScreen = isExpandedScreen,
            isIconPicker = isIconPicker,
            onSendResult = onSendResult,
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun Home(
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onNavigateToIconRequest: () -> Unit,
    onNavigateToDebugMenu: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    lawniconsViewModel: HomeViewModel = metroViewModel<HomeViewModelImpl>(),
) {
    with(lawniconsViewModel) {
        val iconInfoModel by iconInfoModel.collectAsStateWithLifecycle()
        val searchedIconInfoModel by searchedIconInfoModel.collectAsStateWithLifecycle()
        val iconRequestModel by iconRequestModel.collectAsStateWithLifecycle()
        val newIconsInfoModel by newIconsInfoModel.collectAsStateWithLifecycle()
        val context = LocalContext.current

        val lazyGridState = rememberLazyGridState()
        val snackbarHostState = remember { SnackbarHostState() }

        val scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
            FloatingToolbarExitDirection.Bottom,
        )

        val horizontalPadding =
            if (isExpandedScreen) IconPreviewGridPaddings.Expanded else IconPreviewGridPaddings.Default

        Crossfade(
            modifier = modifier,
            targetState = iconInfoModel.iconCount > 0,
            label = "",
        ) { visible ->
            if (visible) {
                Scaffold(
                    topBar = {
                        HomeTopBar(
                            textFieldState = searchTermTextState,
                            mode = searchMode,
                            onBack = {
                                expandSearch = false
                            },
                            onNavigate = onNavigateToAbout,
                            onModeChange = ::changeMode,
                            expandSearch = expandSearch,
                            isExpandedScreen = isExpandedScreen,
                            isIconPicker = isIconPicker,
                            onSendResult = onSendResult,
                            iconInfoModel = searchedIconInfoModel,
                        )
                    },
                    snackbarHost = {
                        SnackbarHost(
                            hostState = snackbarHostState,
                        ) {
                            val offset by animateDpAsState(
                                if (scrollBehavior.state.offset != 0F) 0.dp else FloatingToolbarDefaults.ContainerSize,
                            )
                            Snackbar(
                                it,
                                modifier = Modifier.padding(bottom = offset),
                            )
                        }
                    },
                    modifier = Modifier.nestedScroll(scrollBehavior),
                ) {
                    Box {
                        IconPreviewGrid(
                            iconInfo = iconInfoModel.iconInfo,
                            onSendResult = onSendResult,
                            horizontalPadding = horizontalPadding,
                            isIconPicker = isIconPicker,
                            gridState = lazyGridState,
                        ) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                AppBarListItem(
                                    onLongClick = onNavigateToDebugMenu,
                                )
                            }
                            if (newIconsInfoModel.iconCount != 0) {
                                item(
                                    span = { GridItemSpan(maxLineSpan) },
                                ) {
                                    NewIconsCard(onNavigateToNewIcons)
                                }
                            }
                        }
                        val coroutineScope = rememberCoroutineScope()
                        val iconRequestCount = iconRequestModel?.iconCount ?: 0

                        HomeBottomToolbar(
                            context = context,
                            scrollBehavior = scrollBehavior,
                            showIconRequests =
                            (iconRequestsEnabled && iconRequestCount > 0) || preferenceManager.forceEnableIconRequest.asState().value,
                            onNavigateToAbout = onNavigateToAbout,
                            onNavigateToIconRequest = onNavigateToIconRequest,
                            onIconRequestUnavailable = {
                                coroutineScope.launch {
                                    val result = snackbarHostState
                                        .showSnackbar(
                                            message = context.getString(R.string.icon_requests_suspended),
                                            duration = SnackbarDuration.Short,
                                        )
                                    if (result == SnackbarResult.Dismissed) {
                                        snackbarHostState.currentSnackbarData?.dismiss()
                                    }
                                }
                            },
                            onExpandSearch = { expandSearch = true },
                        )
                    }
                }
            } else {
                PlaceholderUI(horizontalPadding = horizontalPadding)
            }
        }

        LaunchedEffect(searchTermTextState.text) {
            searchIcons(searchTermTextState.text.toString())
        }
    }
}

@PreviewLawnicons
@Composable
private fun HomePreview() {
    LawniconsTheme {
        Surface(Modifier.fillMaxSize()) {
            Home(
                onNavigateToAbout = {},
                onNavigateToNewIcons = {},
                onNavigateToIconRequest = {},
                onNavigateToDebugMenu = {},
                isExpandedScreen = true,
                onSendResult = {},
                lawniconsViewModel = DummyLawniconsViewModel(),
            )
        }
    }
}
