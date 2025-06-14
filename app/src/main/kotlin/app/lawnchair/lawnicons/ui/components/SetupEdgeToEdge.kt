package app.lawnchair.lawnicons.ui.components

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import app.lawnchair.lawnicons.ui.util.toPaddingValues

@Composable
fun ComponentActivity.SetupEdgeToEdge(
    isExpandedScreen: Boolean,
    modifier: Modifier = Modifier,
) {
    val contentColor = MaterialTheme.colorScheme.onBackground.toArgb()
    val isDarkTheme = isSystemInDarkTheme()

    val navBarColor = if (isExpandedScreen) {
        MaterialTheme.colorScheme.background
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }.copy(alpha = 0.95f)

    val density = LocalDensity.current
    val navBarHeight = with(density) {
        WindowInsets.navigationBars.toPaddingValues().calculateBottomPadding().toPx()
    }

    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        LaunchedEffect(Unit) {
            enableEdgeToEdge(
                navigationBarStyle = if (isDarkTheme) {
                    SystemBarStyle.dark(navBarColor.toArgb())
                } else {
                    SystemBarStyle.light(
                        navBarColor.toArgb(),
                        contentColor,
                    )
                },
            )
        }
    } else {
        val context = LocalContext.current

        if (context.getSystemNavigationMode() != SystemNavigation.GESTURE) {
            Canvas(modifier.fillMaxSize()) {
                drawRect(
                    color = navBarColor,
                    size = size.copy(height = navBarHeight),
                    topLeft = Offset(
                        x = 0f,
                        y = size.height - navBarHeight,
                    ),
                )
            }
        }
    }
}

enum class SystemNavigation {
    THREE_BUTTON,
    TWO_BUTTON,
    GESTURE,
}

private fun Context.getSystemNavigationMode() =
    SystemNavigation.entries[Settings.Secure.getInt(contentResolver, "navigation_mode", -1)]
