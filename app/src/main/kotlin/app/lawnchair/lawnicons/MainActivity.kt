package app.lawnchair.lawnicons

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.lawnchair.lawnicons.model.IconInfo
import app.lawnchair.lawnicons.ui.Lawnicons
import app.lawnchair.lawnicons.ui.components.SetupEdgeToEdge
import app.lawnchair.lawnicons.ui.theme.LawniconsTheme
import app.lawnchair.lawnicons.ui.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalFoundationApi
@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val isIconPicker = intent?.action == Constants.ICON_PICKER_INTENT_ACTION

        setContent {
            val context = LocalContext.current
            val windowSizeClass = calculateWindowSizeClass(this)
            LawniconsTheme {
                val isExpandedScreen =
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

                val navBarColor = if (isExpandedScreen) {
                    MaterialTheme.colorScheme.background.copy(alpha = 0.95f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }

                SetupEdgeToEdge(navBarColor.toArgb())
                Lawnicons(
                    isExpandedScreen = isExpandedScreen,
                    onSendResult = { iconInfo ->
                        setIntentResult(context, iconInfo)
                        finish()
                    },
                    isIconPicker = isIconPicker,
                )
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun setIntentResult(
        context: Context,
        iconInfo: IconInfo,
    ) {
        val intent = Intent()

        val primaryForegroundColor = context.getColor(R.color.primaryForeground)
        val primaryBackgroundColor = context.getColor(R.color.primaryBackground)

        val drawable: Drawable? =
            ResourcesCompat.getDrawable(context.resources, iconInfo.id, theme)?.mutate()?.let {
                DrawableCompat.wrap(
                    it,
                )
            }

        if (drawable != null) {
            DrawableCompat.setTintList(drawable, ColorStateList.valueOf(primaryForegroundColor))
            DrawableCompat.setTintList(drawable, ColorStateList.valueOf(primaryBackgroundColor))

            val bitmap = drawable.toBitmap()

            try {
                intent.putExtra(
                    "icon",
                    if (bitmap.isRecycled) {
                        bitmap
                    } else {
                        bitmap.copy(requireNotNull(bitmap.config), false)
                    },
                )
            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
            }
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconInfo.id)
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED, intent)
        }
    }
}
