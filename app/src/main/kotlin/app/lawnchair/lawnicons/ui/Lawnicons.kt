package app.lawnchair.lawnicons.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import app.lawnchair.lawnicons.data.model.IconInfo
import app.lawnchair.lawnicons.ui.destination.about.About
import app.lawnchair.lawnicons.ui.destination.about.aboutDestination
import app.lawnchair.lawnicons.ui.destination.acknowledgements.Acknowledgements
import app.lawnchair.lawnicons.ui.destination.acknowledgements.acknowledgementsDestination
import app.lawnchair.lawnicons.ui.destination.contributors.Contributors
import app.lawnchair.lawnicons.ui.destination.contributors.contributorsDestination
import app.lawnchair.lawnicons.ui.destination.debugmenu.DebugMenu
import app.lawnchair.lawnicons.ui.destination.debugmenu.debugMenuDestination
import app.lawnchair.lawnicons.ui.destination.home.Home
import app.lawnchair.lawnicons.ui.destination.home.homeDestination
import app.lawnchair.lawnicons.ui.destination.iconrequest.IconRequest
import app.lawnchair.lawnicons.ui.destination.iconrequest.iconRequestDestination
import app.lawnchair.lawnicons.ui.destination.newicons.NewIcons
import app.lawnchair.lawnicons.ui.destination.newicons.newIconsDestination
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory
import soup.compose.material.motion.animation.materialSharedAxisX
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun Lawnicons(
    metroVmf: MetroViewModelFactory,
    isExpandedScreen: Boolean,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides metroVmf) {
        val navigationState = rememberNavigationState(
            startRoute = Home,
            topLevelRoutes = setOf(Home, About, NewIcons, IconRequest, DebugMenu),
        )
        val navigator = remember { Navigator(navigationState) }
        val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
        val slideDistance = rememberSlideDistance()

        val entryProvider = entryProvider {
            homeDestination(
                onNavigateToAbout = { navigator.navigate(About) },
                onNavigateToNewIcons = { navigator.navigate(NewIcons) },
                onNavigateToIconRequest = { navigator.navigate(IconRequest) },
                onNavigateToDebugMenu = { navigator.navigate(DebugMenu) },
                isExpandedScreen = isExpandedScreen,
                isIconPicker = isIconPicker,
                onSendResult = onSendResult,
            )
            debugMenuDestination(
                isExpandedScreen = isExpandedScreen,
                onBack = navigator::goBack,
            )
            iconRequestDestination(
                isExpandedScreen = isExpandedScreen,
                onBack = navigator::goBack,
            )
            acknowledgementsDestination(
                onBack = navigator::goBack,
                isExpandedScreen = isExpandedScreen,
            )
            aboutDestination(
                onBack = navigator::goBack,
                onNavigateToContributors = {
                    navigator.navigate(Contributors)
                },
                onNavigateToAcknowledgements = {
                    navigator.navigate(Acknowledgements)
                },
                isExpandedScreen = isExpandedScreen,
            )
            contributorsDestination(
                onBack = navigator::goBack,
                isExpandedScreen = isExpandedScreen,
            )
            newIconsDestination(
                onBack = navigator::goBack,
                isExpandedScreen = isExpandedScreen,
            )
        }

        Surface(
            modifier = modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavDisplay(
                entries = navigationState.toEntries(entryProvider),
                onBack = navigator::goBack,
                transitionSpec = { materialSharedAxisX(!isRtl, slideDistance) },
                popTransitionSpec = { materialSharedAxisX(isRtl, slideDistance) },
                predictivePopTransitionSpec = { materialSharedAxisX(isRtl, slideDistance) },
            )
        }
    }
}
