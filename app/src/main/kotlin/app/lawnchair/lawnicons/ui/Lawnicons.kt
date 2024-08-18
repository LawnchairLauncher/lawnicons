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
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.destination.About
import app.lawnchair.lawnicons.ui.destination.Acknowledgement
import app.lawnchair.lawnicons.ui.destination.Acknowledgements
import app.lawnchair.lawnicons.ui.destination.Contributors
import app.lawnchair.lawnicons.ui.destination.Home
import app.lawnchair.lawnicons.ui.util.About
import app.lawnchair.lawnicons.ui.util.Acknowledgement
import app.lawnchair.lawnicons.ui.util.Acknowledgements
import app.lawnchair.lawnicons.ui.util.Contributors
import app.lawnchair.lawnicons.ui.util.Home
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
            composable<Home> {
                Home(
                    onNavigate = { navController.navigate(About) },
                    isExpandedScreen = isExpandedScreen,
                    isIconPicker = isIconPicker,
                    onSendResult = onSendResult,
                )
            }
            composable<Acknowledgements> {
                Acknowledgements(
                    onBack = navController::popBackStack,
                    onNavigate = {
                        navController.navigate(Acknowledgement(it))
                    },
                    isExpandedScreen = isExpandedScreen,
                )
            }
            composable<Acknowledgement> { backStackEntry ->
                val acknowledgement: Acknowledgement = backStackEntry.toRoute()
                Acknowledgement(
                    name = acknowledgement.id,
                    onBack = navController::popBackStack,
                    isExpandedScreen = isExpandedScreen,
                )
            }
            composable<About> {
                About(
                    onBack = navController::popBackStack,
                    onNavigateToContributors = { navController.navigate(Contributors) },
                    onNavigateToAcknowledgements = { navController.navigate(Acknowledgements) },
                    isExpandedScreen = isExpandedScreen,
                )
            }
            composable<Contributors> {
                Contributors(onBack = navController::popBackStack, isExpandedScreen = isExpandedScreen)
            }
        }
    }
}
