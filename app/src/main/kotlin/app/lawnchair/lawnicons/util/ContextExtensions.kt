package app.lawnchair.lawnicons.util

import android.content.Context
import android.graphics.Bitmap
import androidx.core.graphics.drawable.toBitmap
import app.lawnchair.lawnicons.R

fun Context.appIcon(): Bitmap = (
    this.resources.getDrawable(R.mipmap.ic_launcher, this.theme)
        ?: packageManager.getApplicationIcon(packageName)
    )
    .toBitmap()
