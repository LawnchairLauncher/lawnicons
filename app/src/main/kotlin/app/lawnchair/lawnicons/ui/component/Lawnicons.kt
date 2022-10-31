package app.lawnchair.lawnicons.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.navArgument
import app.lawnchair.lawnicons.ui.destination.About
import app.lawnchair.lawnicons.ui.destination.Acknowledgement
import app.lawnchair.lawnicons.ui.destination.Acknowledgements
import app.lawnchair.lawnicons.ui.destination.Contributors
import app.lawnchair.lawnicons.ui.destination.Home
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Destinations
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
@ExperimentalFoundationApi
@OptIn(ExperimentalAnimationApi::class)
fun Lawnicons() {
    val navController = rememberAnimatedNavController()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()

    LawniconsTheme {
        SystemUi()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            AnimatedNavHost(
                navController = navController,
                startDestination = Destinations.HOME,
                enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
                exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
                popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
                popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
            ) {
                composable(route = Destinations.HOME) {
                    Home(navController = navController)
                }
                composable(route = Destinations.ACKNOWLEDGEMENTS) {
                    Acknowledgements(navController = navController)
                }
                composable(
                    route = "${Destinations.ACKNOWLEDGEMENT}/{id}",
                    arguments = listOf(
                        navArgument(
                            name = "id",
                            builder = { type = NavType.StringType },
                        ),
                    ),
                ) { backStackEntry ->
                    Acknowledgement(
                        name = backStackEntry.arguments?.getString("id"),
                        navController = navController,
                    )
                }
                composable(route = Destinations.ABOUT) {
                    About(navController = navController)
                }
                composable(route = Destinations.CONTRIBUTORS) {
                    Contributors(navController = navController)
                }
            }
        }
    }
}
