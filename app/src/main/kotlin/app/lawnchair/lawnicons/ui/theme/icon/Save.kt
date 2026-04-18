package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Save: ImageVector
    get() {
        if (_Save != null) {
            return _Save!!
        }
        _Save = ImageVector.Builder(
            name = "Save",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(200f, 840f)
                quadTo(167f, 840f, 143.5f, 816.5f)
                quadTo(120f, 793f, 120f, 760f)
                lineTo(120f, 200f)
                quadTo(120f, 167f, 143.5f, 143.5f)
                quadTo(167f, 120f, 200f, 120f)
                lineTo(647f, 120f)
                quadTo(663f, 120f, 677.5f, 126f)
                quadTo(692f, 132f, 703f, 143f)
                lineTo(817f, 257f)
                quadTo(828f, 268f, 834f, 282.5f)
                quadTo(840f, 297f, 840f, 313f)
                lineTo(840f, 760f)
                quadTo(840f, 793f, 816.5f, 816.5f)
                quadTo(793f, 840f, 760f, 840f)
                lineTo(200f, 840f)
                close()
                moveTo(760f, 314f)
                lineTo(646f, 200f)
                lineTo(200f, 200f)
                quadTo(200f, 200f, 200f, 200f)
                quadTo(200f, 200f, 200f, 200f)
                lineTo(200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                lineTo(760f, 760f)
                quadTo(760f, 760f, 760f, 760f)
                quadTo(760f, 760f, 760f, 760f)
                lineTo(760f, 314f)
                close()
                moveTo(480f, 720f)
                quadTo(530f, 720f, 565f, 685f)
                quadTo(600f, 650f, 600f, 600f)
                quadTo(600f, 550f, 565f, 515f)
                quadTo(530f, 480f, 480f, 480f)
                quadTo(430f, 480f, 395f, 515f)
                quadTo(360f, 550f, 360f, 600f)
                quadTo(360f, 650f, 395f, 685f)
                quadTo(430f, 720f, 480f, 720f)
                close()
                moveTo(280f, 400f)
                lineTo(560f, 400f)
                quadTo(577f, 400f, 588.5f, 388.5f)
                quadTo(600f, 377f, 600f, 360f)
                lineTo(600f, 280f)
                quadTo(600f, 263f, 588.5f, 251.5f)
                quadTo(577f, 240f, 560f, 240f)
                lineTo(280f, 240f)
                quadTo(263f, 240f, 251.5f, 251.5f)
                quadTo(240f, 263f, 240f, 280f)
                lineTo(240f, 360f)
                quadTo(240f, 377f, 251.5f, 388.5f)
                quadTo(263f, 400f, 280f, 400f)
                close()
                moveTo(200f, 314f)
                lineTo(200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                lineTo(200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                quadTo(200f, 760f, 200f, 760f)
                lineTo(200f, 200f)
                quadTo(200f, 200f, 200f, 200f)
                quadTo(200f, 200f, 200f, 200f)
                lineTo(200f, 200f)
                lineTo(200f, 314f)
                close()
            }
        }.build()

        return _Save!!
    }

@Suppress("ObjectPropertyName")
private var _Save: ImageVector? = null
