package app.lawnchair.lawnicons.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
fun Lawnicons(
    isExpandedScreen: Boolean,
    onSendResult: (IconInfo) -> Unit,
    modifier: Modifier = Modifier,
    isIconPicker: Boolean = false,
) {
    val navController = rememberNavController()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        NavHost(
            navController = navController,
            startDestination = Home,
            enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
            exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
            popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
            popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
        ) {
            homeDestination(
                onNavigateToAbout = { navController.navigate(About) },
                onNavigateToNewIcons = { navController.navigate(NewIcons) },
                onNavigateToIconRequest = { navController.navigate(IconRequest) },
                onNavigateToDebugMenu = { navController.navigate(DebugMenu) },
                isExpandedScreen = isExpandedScreen,
                isIconPicker = isIconPicker,
                onSendResult = onSendResult,
            )
            debugMenuDestination(
                isExpandedScreen = isExpandedScreen,
                onBack = navController::popBackStack,
            )
            iconRequestDestination(
                isExpandedScreen = isExpandedScreen,
                onBack = navController::popBackStack,
            )
            acknowledgementsDestination(
                onBack = navController::popBackStack,
                isExpandedScreen = isExpandedScreen,
            )
            aboutDestination(
                onBack = navController::popBackStack,
                onNavigateToContributors = {
                    navController.navigate(Contributors)
                },
                onNavigateToAcknowledgements = {
                    navController.navigate(Acknowledgements)
                },
                isExpandedScreen = isExpandedScreen,
            )
            contributorsDestination(
                onBack = navController::popBackStack,
                isExpandedScreen = isExpandedScreen,
            )
            newIconsDestination(
                onBack = navController::popBackStack,
                isExpandedScreen = isExpandedScreen,
            )
        }
    }
}
