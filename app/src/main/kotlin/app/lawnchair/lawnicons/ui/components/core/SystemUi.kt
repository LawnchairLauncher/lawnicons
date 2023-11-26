package app.lawnchair.lawnicons.ui.components.core

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun SystemUi() {
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    SideEffect {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = useDarkIcons,
        )
    }
}

@Preview(
    showSystemUi = true,
)
@Composable
fun SystemUIPreview() {
    LawniconsTheme {
        SystemUi()
    }
}
