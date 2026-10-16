package app.lawnchair.lawnicons.ui.components

import android.content.Context
import android.provider.Settings
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import app.lawnchair.lawnicons.ui.util.toPaddingValues

@Composable
fun ShowNavbarScrim(isExpandedScreen: Boolean, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    if (context.getSystemNavigationMode() != SystemNavigation.GESTURE) {
        val navBarColor = if (isExpandedScreen) {
            MaterialTheme.colorScheme.background
        } else {
            MaterialTheme.colorScheme.surfaceContainer
        }.copy(alpha = 0.95f)

        val navBarHeight = with(LocalDensity.current) {
            WindowInsets.navigationBars.toPaddingValues().calculateBottomPadding().toPx()
        }

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

enum class SystemNavigation {
    THREE_BUTTON,
    TWO_BUTTON,
    GESTURE,
}

private fun Context.getSystemNavigationMode(): SystemNavigation {
    val mode = Settings.Secure.getInt(contentResolver, "navigation_mode", -1)
    return when (mode) {
        0 -> SystemNavigation.THREE_BUTTON
        1 -> SystemNavigation.TWO_BUTTON
        else -> SystemNavigation.GESTURE
    }
}
