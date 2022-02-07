package app.lawnchair.lawnicons.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.ln
import androidx.compose.material3.MaterialTheme as Material3Theme

@Composable
fun LawniconsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (darkTheme) darkColorScheme() else lightColorScheme()
    }

    MaterialTheme(
        colors = materialColors(colorScheme = colorScheme, isDark = darkTheme)
    ) {
        Material3Theme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}

internal fun ColorScheme.surfaceColorAtElevation(
    elevation: Dp,
): Color {
    if (elevation == 0.dp) return surface
    val alpha = ((4.5f * ln(elevation.value + 1)) + 2f) / 100f
    return primary.copy(alpha = alpha).compositeOver(surface)
}

@Composable
fun materialColors(colorScheme: ColorScheme, isDark: Boolean) = remember(colorScheme, isDark) {
    Colors(
        primary = colorScheme.primary,
        primaryVariant = colorScheme.primaryContainer,
        secondary = colorScheme.primary,
        secondaryVariant = colorScheme.primaryContainer,
        background = colorScheme.background,
        surface = colorScheme.surfaceColorAtElevation(1.dp),
        error = colorScheme.error,
        onPrimary = colorScheme.onPrimary,
        onSecondary = colorScheme.onSecondary,
        onBackground = colorScheme.onBackground,
        onSurface = colorScheme.onSurface,
        onError = colorScheme.onError,
        isLight = !isDark
    )
}
