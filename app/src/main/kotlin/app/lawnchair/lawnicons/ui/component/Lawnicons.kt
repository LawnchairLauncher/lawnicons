package app.lawnchair.lawnicons.ui.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import app.lawnchair.lawnicons.ui.destination.*
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Destinations

@Composable
@ExperimentalFoundationApi
fun Lawnicons() {
    val navController = rememberNavController()

    LawniconsTheme {
        SystemUi {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background,
            ) {
                NavHost(
                    navController = navController,
                    startDestination = Destinations.HOME
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
                                builder = { type = NavType.StringType }
                            )
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
}
