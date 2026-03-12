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

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.FloatingToolbarScrollBehavior
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.lawnchair.lawnicons.R
import app.lawnchair.lawnicons.data.model.IconInfoModel
import app.lawnchair.lawnicons.data.model.SearchMode
import app.lawnchair.lawnicons.ui.components.AnnouncementList
import app.lawnchair.lawnicons.ui.components.home.HomeBottomToolbar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBar
import app.lawnchair.lawnicons.ui.components.home.NewIconsCard
import app.lawnchair.lawnicons.ui.components.home.PlaceholderUI
import app.lawnchair.lawnicons.ui.components.home.iconpreview.AppBarListItem
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGridPaddings
import app.lawnchair.lawnicons.ui.components.home.search.rememberSearchState
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.ui.util.PreviewProviders
import app.lawnchair.lawnicons.ui.util.SampleData
import dev.zacsweers.metrox.viewmodel.metroViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

fun EntryProviderScope<NavKey>.homeDestination(
    isExpandedScreen: Boolean,
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onNavigateToIconRequest: () -> Unit,
    onNavigateToDebugMenu: () -> Unit,
) {
    entry<Home> {
        Home(
            onNavigateToAbout = onNavigateToAbout,
            onNavigateToNewIcons = onNavigateToNewIcons,
            onNavigateToIconRequest = onNavigateToIconRequest,
            onNavigateToDebugMenu = onNavigateToDebugMenu,
            isExpandedScreen = isExpandedScreen,
        )
    }
}

@Composable
private fun Home(
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onNavigateToIconRequest: () -> Unit,
    onNavigateToDebugMenu: () -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    lawniconsViewModel: HomeViewModel = metroViewModel<HomeViewModelImpl>(),
) {
    val uiState by lawniconsViewModel.uiState.collectAsStateWithLifecycle()

    val searchMode by lawniconsViewModel.searchMode.collectAsStateWithLifecycle()
    val searchResults by lawniconsViewModel.searchResults.collectAsStateWithLifecycle()
    val searchTermTextState = lawniconsViewModel.searchTermTextState

    val searchUiState = HomeSearchUiState(
        searchResults = searchResults,
        textFieldState = searchTermTextState,
        mode = searchMode,
    )

    val navigateActions = remember {
        HomeNavigateActions(
            toAbout = onNavigateToAbout,
            toNewIcons = onNavigateToNewIcons,
            toIconRequest = onNavigateToIconRequest,
            toDebugMenu = onNavigateToDebugMenu,
        )
    }

    val actions = remember {
        HomeActions(
            searchIcons = lawniconsViewModel::searchIcons,
            changeMode = lawniconsViewModel::changeMode,
        )
    }

    HomeScreen(
        uiState = uiState,
        homeSearchUiState = searchUiState,
        navigateActions = navigateActions,
        actions = actions,
        isExpandedScreen = isExpandedScreen,
        modifier = modifier,
    )
}

@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalFoundationApi::class,
)
@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    homeSearchUiState: HomeSearchUiState,
    navigateActions: HomeNavigateActions,
    actions: HomeActions,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    val horizontalPadding =
        if (isExpandedScreen) IconPreviewGridPaddings.Expanded else IconPreviewGridPaddings.Default

    Crossfade(
        modifier = modifier,
        targetState = uiState,
        label = "",
    ) { uiState ->
        if (uiState is HomeUiState.Success) {
            val searchState = rememberSearchState(
                textFieldState = homeSearchUiState.textFieldState,
                mode = homeSearchUiState.mode,
                onModeChange = actions.changeMode,
            )

            val lazyGridState = rememberLazyGridState()
            val snackbarHostState = remember { SnackbarHostState() }

            val scrollBehavior = FloatingToolbarDefaults.exitAlwaysScrollBehavior(
                FloatingToolbarExitDirection.Bottom,
            )

            val coroutineScope = rememberCoroutineScope()
            val layoutDirection = LocalLayoutDirection.current

            Scaffold(
                topBar = {
                    HomeTopBar(
                        searchState = searchState,
                        isExpandedScreen = isExpandedScreen,
                        iconInfoModel = homeSearchUiState.searchResults,
                    )
                },
                snackbarHost = {
                    HomeSnackBar(
                        scrollBehavior = scrollBehavior,
                        snackbarHostState = snackbarHostState,
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior),
            ) { contentPadding ->
                Box(
                    Modifier.padding(
                        start = contentPadding.calculateStartPadding(layoutDirection),
                        end = contentPadding.calculateEndPadding(layoutDirection),
                    ),
                ) {
                    IconPreviewGrid(
                        iconInfo = uiState.iconInfoModel.iconInfo,
                        horizontalPadding = horizontalPadding,
                        gridState = lazyGridState,
                    ) {
                        item(
                            span = { GridItemSpan(maxLineSpan) },
                        ) {
                            AppBarListItem(
                                onLongClick = navigateActions.toDebugMenu,
                            )
                        }
                        if (uiState.hasNewIcons) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                NewIconsCard(navigateActions.toNewIcons)
                            }
                        }
                        if (uiState.announcements.isNotEmpty()) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                AnnouncementList(uiState.announcements)
                            }
                        }
                    }

                    val string = stringResource(R.string.icon_requests_furfilled)

                    HomeBottomToolbar(
                        scrollBehavior = scrollBehavior,
                        showIconRequests = uiState.hasIconRequests,
                        onNavigateToAbout = navigateActions.toAbout,
                        onNavigateToIconRequest = navigateActions.toIconRequest,
                        onIconRequestUnavailable = {
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
                        },
                        onExpandSearch = {
                            coroutineScope.launch {
                                searchState.searchBarState.animateToExpanded()
                            }
                        },
                    )
                }
            }

            LaunchedEffect(homeSearchUiState.textFieldState.text) {
                delay(300)
                actions.searchIcons(homeSearchUiState.textFieldState.text.toString())
            }
        } else {
            PlaceholderUI(horizontalPadding = horizontalPadding)
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HomeSnackBar(
    scrollBehavior: FloatingToolbarScrollBehavior,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        hostState = snackbarHostState,
        modifier = modifier,
    ) {
        val coroutineScope = rememberCoroutineScope()

        val density = LocalDensity.current
        val offsetModifier = Modifier.graphicsLayer {
            val isVisible = scrollBehavior.state.offset == 0F
            translationY =
                if (isVisible) 0f else with(density) { FloatingToolbarDefaults.ContainerSize.toPx() }
        }

        SwipeToDismissBox(
            state = rememberSwipeToDismissBoxState(),
            backgroundContent = {},
            onDismiss = {
                coroutineScope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                }
            },
        ) {
            Snackbar(
                it,
                modifier = offsetModifier,
            )
        }
    }
}

private data class HomeSearchUiState(
    val searchResults: IconInfoModel,
    val textFieldState: TextFieldState,
    val mode: SearchMode,
)

private data class HomeNavigateActions(
    val toAbout: () -> Unit,
    val toNewIcons: () -> Unit,
    val toIconRequest: () -> Unit,
    val toDebugMenu: () -> Unit,
)

private data class HomeActions(
    val searchIcons: (String) -> Unit,
    val changeMode: (SearchMode) -> Unit,
)

@PreviewLawnicons
@Composable
private fun HomeScreenPreview() {
    PreviewProviders {
        Surface(Modifier.fillMaxSize()) {
            val searchTermTextState = remember { TextFieldState() }

            val navigateActions = remember {
                HomeNavigateActions(
                    toAbout = {},
                    toNewIcons = {},
                    toIconRequest = {},
                    toDebugMenu = {},
                )
            }

            val actions = remember {
                HomeActions(
                    searchIcons = {},
                    changeMode = {},
                )
            }

            val model = IconInfoModel(
                iconInfo = SampleData.iconInfoList,
                iconCount = SampleData.iconInfoList.size,
            )

            val searchState = HomeSearchUiState(
                searchResults = model,
                textFieldState = searchTermTextState,
                mode = SearchMode.LABEL,
            )

            val uiState = HomeUiState.Success(
                iconInfoModel = model,
                hasNewIcons = true,
                hasIconRequests = true,
                announcements = SampleData.announcements,
            )

            HomeScreen(
                uiState = uiState,
                homeSearchUiState = searchState,
                navigateActions = navigateActions,
                actions = actions,
                isExpandedScreen = false,
            )
        }
    }
}
