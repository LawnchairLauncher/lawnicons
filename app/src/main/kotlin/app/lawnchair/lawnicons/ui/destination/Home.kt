package app.lawnchair.lawnicons.ui.destination

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.repository.preferenceManager
import app.lawnchair.lawnicons.ui.components.home.DebugMenu
import app.lawnchair.lawnicons.ui.components.home.HomeBottomBar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBar
import app.lawnchair.lawnicons.ui.components.home.HomeTopBarUiState
import app.lawnchair.lawnicons.ui.components.home.IconRequestFAB
import app.lawnchair.lawnicons.ui.components.home.NewIconsCard
import app.lawnchair.lawnicons.ui.components.home.PlaceholderUI
import app.lawnchair.lawnicons.ui.components.home.iconpreview.AppBarListItem
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGrid
import app.lawnchair.lawnicons.ui.components.home.iconpreview.IconPreviewGridPadding
import app.lawnchair.lawnicons.ui.components.home.search.PlaceholderSearchBar
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.PreviewLawnicons
import app.lawnchair.lawnicons.viewmodel.DummyLawniconsViewModel
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModel
import app.lawnchair.lawnicons.viewmodel.LawniconsViewModelImpl
import kotlinx.serialization.Serializable

@Serializable
data object Home

fun NavGraphBuilder.homeDestination(
    isExpandedScreen: Boolean,
    isIconPicker: Boolean,
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
) {
    composable<Home> {
        Home(
            onNavigateToAbout = onNavigateToAbout,
            onNavigateToNewIcons = onNavigateToNewIcons,
            isExpandedScreen = isExpandedScreen,
            isIconPicker = isIconPicker,
            onSendResult = onSendResult,
        )
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Home(
    onNavigateToAbout: () -> Unit,
    onNavigateToNewIcons: () -> Unit,
    onSendResult: (IconInfo) -> Unit,
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
    lawniconsViewModel: LawniconsViewModel = hiltViewModel<LawniconsViewModelImpl>(),
) {
    with(lawniconsViewModel) {
        val iconInfoModel by iconInfoModel.collectAsStateWithLifecycle()
        val searchedIconInfoModel by searchedIconInfoModel.collectAsStateWithLifecycle()
        val iconRequestModel by iconRequestModel.collectAsStateWithLifecycle()
        val newIconsInfoModel by newIconsInfoModel.collectAsStateWithLifecycle()
        val context = LocalContext.current

        val lazyGridState = rememberLazyGridState()
        val snackbarHostState = remember { SnackbarHostState() }

        val focusRequester = remember { FocusRequester() }
        val prefs = preferenceManager(context)

        Crossfade(
            modifier = modifier,
            targetState = iconInfoModel.iconCount > 0,
            label = "",
        ) { visible ->
            if (visible) {
                Scaffold(
                    topBar = {
                        HomeTopBar(
                            uiState = HomeTopBarUiState(
                                isSearchExpanded = expandSearch,
                                isExpandedScreen = isExpandedScreen,
                                searchedIconInfoModel = searchedIconInfoModel,
                                searchTerm = searchTerm,
                                searchMode = searchMode,
                                isIconPicker = isIconPicker,
                            ),
                            onFocusChange = { expandSearch = !expandSearch },
                            onClearSearch = ::clearSearch,
                            onChangeMode = ::changeMode,
                            onSearchIcons = ::searchIcons,
                            onNavigate = onNavigateToAbout,
                            onSendResult = onSendResult,
                            focusRequester = focusRequester,
                        )
                    },
                    bottomBar = {
                        if (!isExpandedScreen) {
                            AnimatedVisibility(
                                !expandSearch,
                                enter = fadeIn(),
                                exit = fadeOut(),
                            ) {
                                HomeBottomBar(
                                    context = context,
                                    iconRequestsEnabled = iconRequestsEnabled,
                                    iconRequestModel = iconRequestModel,
                                    snackbarHostState = snackbarHostState,
                                    onNavigate = onNavigateToAbout,
                                    onExpandSearch = { expandSearch = true },
                                )
                            }
                        }
                    },
                    floatingActionButton = {
                        if (isExpandedScreen) {
                            IconRequestFAB(
                                iconRequestsEnabled = iconRequestsEnabled,
                                iconRequestModel = iconRequestModel,
                                lazyGridState = lazyGridState,
                                snackbarHostState = snackbarHostState,
                            )
                        }
                    },
                    snackbarHost = {
                        SnackbarHost(hostState = snackbarHostState)
                    },
                ) {
                    IconPreviewGrid(
                        iconInfo = iconInfoModel.iconInfo,
                        onSendResult = onSendResult,
                        contentPadding = if (isExpandedScreen) IconPreviewGridPadding.ExpandedSize else IconPreviewGridPadding.Defaults,
                        isIconPicker = isIconPicker,
                        gridState = lazyGridState,
                    ) {
                        if (!isExpandedScreen) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                AppBarListItem()
                            }
                        }
                        if (newIconsInfoModel.iconCount != 0) {
                            item(
                                span = { GridItemSpan(maxLineSpan) },
                            ) {
                                NewIconsCard(onNavigateToNewIcons)
                            }
                        }
                    }
                }
            } else {
                if (isExpandedScreen) {
                    PlaceholderSearchBar()
                } else {
                    PlaceholderUI(prefs.showNewIconsCard.asState().value)
                }
            }
        }

        LaunchedEffect(expandSearch) {
            if (expandSearch) {
                focusRequester.requestFocus()
            }
        }

        if (prefs.showDebugMenu.asState().value) {
            DebugMenu(
                iconInfoModel,
                iconRequestModel,
                newIconsInfoModel,
            )
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
                isExpandedScreen = true,
                onSendResult = {},
                lawniconsViewModel = DummyLawniconsViewModel(),
            )
        }
    }
}
