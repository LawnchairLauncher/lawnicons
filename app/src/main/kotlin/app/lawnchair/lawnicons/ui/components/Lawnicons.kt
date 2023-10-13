package app.lawnchair.lawnicons.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.lawnchair.lawnicons.ui.components.core.SystemUi
import app.lawnchair.lawnicons.ui.destination.About
import app.lawnchair.lawnicons.ui.destination.Acknowledgement
import app.lawnchair.lawnicons.ui.destination.Acknowledgements
import app.lawnchair.lawnicons.ui.destination.Contributors
import app.lawnchair.lawnicons.ui.destination.Home
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Destinations
import soup.compose.material.motion.animation.materialSharedAxisXIn
import soup.compose.material.motion.animation.materialSharedAxisXOut
import soup.compose.material.motion.animation.rememberSlideDistance

@Composable
@ExperimentalFoundationApi
fun Lawnicons(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val slideDistance = rememberSlideDistance()

    LawniconsTheme {
        SystemUi()
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            NavHost(
                navController = navController,
                startDestination = Destinations.HOME,
                enterTransition = { materialSharedAxisXIn(!isRtl, slideDistance) },
                exitTransition = { materialSharedAxisXOut(!isRtl, slideDistance) },
                popEnterTransition = { materialSharedAxisXIn(isRtl, slideDistance) },
                popExitTransition = { materialSharedAxisXOut(isRtl, slideDistance) },
            ) {
                composable(route = Destinations.HOME) {
                    Home(navController = navController, windowSizeClass = windowSizeClass)
                }
                composable(route = Destinations.ACKNOWLEDGEMENTS) {
                    Acknowledgements(navController = navController, windowSizeClass = windowSizeClass)
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
                        windowSizeClass = windowSizeClass,
                    )
                }
                composable(route = Destinations.ABOUT) {
                    About(navController = navController, windowSizeClass = windowSizeClass)
                }
                composable(route = Destinations.CONTRIBUTORS) {
                    Contributors(navController = navController, windowSizeClass = windowSizeClass)
                }
            }
        }
    }
}
