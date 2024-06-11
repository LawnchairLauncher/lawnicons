package app.lawnchair.lawnicons

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import app.lawnchair.lawnicons.ui.Lawnicons
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

        val isIconPicker = intent?.action == "com.novalauncher.THEME"

        setContent {
            val context = LocalContext.current
            val windowSizeClass = calculateWindowSizeClass(this)
            Lawnicons(
                windowSizeClass = windowSizeClass,
                onSendResult = { iconInfo ->
                    val intent = Intent()

                    val bitmap = ResourcesCompat.getDrawable(context.resources, iconInfo.id, null)
                        ?.toBitmap()

                    if (bitmap != null) {
                        try {
                            intent.putExtra(
                                "icon",
                                if (bitmap.isRecycled) {
                                    bitmap
                                } else {
                                    bitmap.copy(bitmap.config, false)
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
                    finish()
                },
                isIconPicker = isIconPicker,
            )
        }
    }
}
