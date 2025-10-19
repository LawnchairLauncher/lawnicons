/*
 * Copyright 2021 Sarsa Murmu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Modifications made by Lawnchair Launcher
 */

package app.lawnchair.lawnicons.data.repository.iconrequest

import android.graphics.Bitmap
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.scale

object AdaptiveIconBitmap {
    private val path: Path = Path().apply {
        moveTo(0f, 0f)
        lineTo(0f, 50f)
        lineTo(50f, 50f)
        lineTo(50f, 0f)
        close()
    }
    private val pathSize = Rect(0, 0, 50, 50)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)

    private const val SCALE_FACTOR = 0.66
    private const val SIZE = 256

    fun toBitmap(drawable: AdaptiveIconDrawable): Bitmap {
        val wholeSize = (SIZE / SCALE_FACTOR).toInt()

        val fgBmp = (drawable.foreground ?: emptyDrawable()).toScaledBitmap()
        val bgBmp = (drawable.background ?: emptyDrawable()).toScaledBitmap()

        val mergedBmp = createBitmap(wholeSize, wholeSize).apply {
            val canvas = Canvas(this)
            canvas.drawBitmapCentered(bgBmp)
            canvas.drawBitmapCentered(fgBmp)
        }

        val cropSize = (wholeSize * SCALE_FACTOR).toInt()
        val cropPos = (wholeSize - cropSize) / 2
        val croppedBmp = Bitmap.createBitmap(mergedBmp, cropPos, cropPos, cropSize, cropSize)

        val scaledPath = croppedBmp.createScaledPath()

        val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            shader = BitmapShader(croppedBmp, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        return createBitmap(SIZE, SIZE).also {
            val canvas = Canvas(it)
            canvas.drawPath(scaledPath, bitmapPaint)
        }
    }

    private fun emptyDrawable(): Drawable = ShapeDrawable(OvalShape()).apply {
        paint.color = Color.TRANSPARENT
        intrinsicHeight = SIZE
        intrinsicWidth = SIZE
    }

    private fun Drawable.toScaledBitmap(): Bitmap {
        val bitmap = this.toBitmap()
        val newHeight = SIZE / SCALE_FACTOR
        val newWidth = bitmap.width * (newHeight / bitmap.height)
        return bitmap.scale(newWidth.toInt(), newHeight.toInt())
    }

    private fun Bitmap.createScaledPath(): Path {
        val matrix = Matrix().apply {
            setScale(
                this@createScaledPath.width.toFloat() / pathSize.width(),
                this@createScaledPath.height.toFloat() / pathSize.height(),
            )
        }
        return Path().apply {
            path.transform(matrix, this)
        }
    }

    private fun Canvas.drawBitmapCentered(bitmap: Bitmap) {
        val matrix = Matrix().apply {
            setTranslate(
                (width - bitmap.width) / 2f,
                (height - bitmap.height) / 2f,
            )
        }
        drawBitmap(bitmap, matrix, paint)
    }
}
