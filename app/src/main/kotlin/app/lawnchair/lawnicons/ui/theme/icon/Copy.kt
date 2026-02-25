package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Copy: ImageVector
    get() {
        if (_Copy != null) {
            return _Copy!!
        }
        _Copy = ImageVector.Builder(
            name = "Copy",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(360f, 720f)
                quadTo(327f, 720f, 303.5f, 696.5f)
                quadTo(280f, 673f, 280f, 640f)
                lineTo(280f, 160f)
                quadTo(280f, 127f, 303.5f, 103.5f)
                quadTo(327f, 80f, 360f, 80f)
                lineTo(720f, 80f)
                quadTo(753f, 80f, 776.5f, 103.5f)
                quadTo(800f, 127f, 800f, 160f)
                lineTo(800f, 640f)
                quadTo(800f, 673f, 776.5f, 696.5f)
                quadTo(753f, 720f, 720f, 720f)
                lineTo(360f, 720f)
                close()
                moveTo(360f, 640f)
                lineTo(720f, 640f)
                quadTo(720f, 640f, 720f, 640f)
                quadTo(720f, 640f, 720f, 640f)
                lineTo(720f, 160f)
                quadTo(720f, 160f, 720f, 160f)
                quadTo(720f, 160f, 720f, 160f)
                lineTo(360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                lineTo(360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                close()
                moveTo(200f, 880f)
                quadTo(167f, 880f, 143.5f, 856.5f)
                quadTo(120f, 833f, 120f, 800f)
                lineTo(120f, 280f)
                quadTo(120f, 263f, 131.5f, 251.5f)
                quadTo(143f, 240f, 160f, 240f)
                quadTo(177f, 240f, 188.5f, 251.5f)
                quadTo(200f, 263f, 200f, 280f)
                lineTo(200f, 800f)
                quadTo(200f, 800f, 200f, 800f)
                quadTo(200f, 800f, 200f, 800f)
                lineTo(600f, 800f)
                quadTo(617f, 800f, 628.5f, 811.5f)
                quadTo(640f, 823f, 640f, 840f)
                quadTo(640f, 857f, 628.5f, 868.5f)
                quadTo(617f, 880f, 600f, 880f)
                lineTo(200f, 880f)
                close()
                moveTo(360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                lineTo(360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                lineTo(360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                quadTo(360f, 160f, 360f, 160f)
                lineTo(360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                quadTo(360f, 640f, 360f, 640f)
                close()
            }
        }.build()

        return _Copy!!
    }

@Suppress("ObjectPropertyName")
private var _Copy: ImageVector? = null
