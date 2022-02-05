package app.lawnchair.lawnicons.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap

fun Context.appIcon(): Bitmap = packageManager.getApplicationIcon(packageName).toBitmap()
