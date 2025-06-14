package app.lawnchair.lawnicons

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.DrawableCompat
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.setDecorFitsSystemWindows(false)
            window.setNavigationBarContrastEnforced(false)
        }

        val isIconPicker = intent?.action == Constants.ICON_PICKER_INTENT_ACTION

        setContent {
            val context = LocalContext.current
            val windowSizeClass = calculateWindowSizeClass(this)
            LawniconsTheme {
                val isExpandedScreen =
                    windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded

                Lawnicons(
                    isExpandedScreen = isExpandedScreen,
                    onSendResult = { iconInfo ->
                        setIntentResult(context, iconInfo)
                        finish()
                    },
                    isIconPicker = isIconPicker,
                )
                SetupEdgeToEdge(isExpandedScreen)
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
            val targetBitmapSize = 192
            val bitmap = createBitmap(targetBitmapSize, targetBitmapSize)
            val canvas = Canvas(bitmap)

            canvas.drawColor(primaryBackgroundColor)

            val foregroundDrawable = drawable.apply {
                DrawableCompat.setTintList(this, ColorStateList.valueOf(primaryForegroundColor))
            }

            val foregroundActualSize = (targetBitmapSize * (2.0f / 3.0f)).toInt()

            val insetFromEdgeHorizontal = (targetBitmapSize - foregroundActualSize) / 2
            val insetFromEdgeVertical = (targetBitmapSize - foregroundActualSize) / 2

            val right = targetBitmapSize - insetFromEdgeHorizontal
            val bottom = targetBitmapSize - insetFromEdgeVertical

            foregroundDrawable.setBounds(
                insetFromEdgeHorizontal,
                insetFromEdgeVertical,
                right,
                bottom,
            )
            foregroundDrawable.draw(canvas)

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
                Log.d("SetIntentResult", e.toString())
            }
            val iconRes = Intent.ShortcutIconResource.fromContext(this, iconInfo.id)

            if (BuildConfig.DEBUG) {
                Log.d(
                    "SetIntentResult",
                    "Preparing to send bitmap. " +
                        "Width: ${bitmap.width}, Height: ${bitmap.height}, " +
                        "Config: ${bitmap.config}, ByteCount: ${bitmap.byteCount}, " +
                        "isRecycled: ${bitmap.isRecycled}",
                )
            }
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes)
            setResult(RESULT_OK, intent)
        } else {
            setResult(RESULT_CANCELED, intent)
        }
    }
}
