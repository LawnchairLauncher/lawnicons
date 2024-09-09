package app.lawnchair.lawnicons.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.destination.About
import app.lawnchair.lawnicons.ui.destination.Acknowledgement
import app.lawnchair.lawnicons.ui.destination.Acknowledgements
import app.lawnchair.lawnicons.ui.destination.Contributors
import app.lawnchair.lawnicons.ui.destination.Home
import app.lawnchair.lawnicons.ui.destination.NewIcons
import app.lawnchair.lawnicons.ui.destination.aboutDestination
import app.lawnchair.lawnicons.ui.destination.acknowledgementDestination
import app.lawnchair.lawnicons.ui.destination.acknowledgementsDestination
import app.lawnchair.lawnicons.ui.destination.contributorsDestination
import app.lawnchair.lawnicons.ui.destination.homeDestination
import app.lawnchair.lawnicons.ui.destination.newIconsDestination
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
@ExperimentalFoundationApi
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
                isExpandedScreen = isExpandedScreen,
                isIconPicker = isIconPicker,
                onSendResult = onSendResult,
            )
            acknowledgementsDestination(
                onBack = navController::popBackStack,
                onNavigate = {
                    navController.navigate(Acknowledgement(it))
                },
                isExpandedScreen = isExpandedScreen,
            )
            acknowledgementDestination(
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
