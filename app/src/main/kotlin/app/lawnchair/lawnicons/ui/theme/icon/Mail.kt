package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Mail: ImageVector
    get() {
        if (_Mail != null) {
            return _Mail!!
        }
        _Mail = ImageVector.Builder(
            name = "Mail",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(160f, 800f)
                quadTo(127f, 800f, 103.5f, 776.5f)
                quadTo(80f, 753f, 80f, 720f)
                lineTo(80f, 240f)
                quadTo(80f, 207f, 103.5f, 183.5f)
                quadTo(127f, 160f, 160f, 160f)
                lineTo(800f, 160f)
                quadTo(833f, 160f, 856.5f, 183.5f)
                quadTo(880f, 207f, 880f, 240f)
                lineTo(880f, 720f)
                quadTo(880f, 753f, 856.5f, 776.5f)
                quadTo(833f, 800f, 800f, 800f)
                lineTo(160f, 800f)
                close()
                moveTo(800f, 320f)
                lineTo(501f, 507f)
                quadTo(496f, 510f, 490.5f, 511.5f)
                quadTo(485f, 513f, 480f, 513f)
                quadTo(475f, 513f, 469.5f, 511.5f)
                quadTo(464f, 510f, 459f, 507f)
                lineTo(160f, 320f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(800f, 720f)
                quadTo(800f, 720f, 800f, 720f)
                quadTo(800f, 720f, 800f, 720f)
                lineTo(800f, 320f)
                close()
                moveTo(480f, 440f)
                lineTo(800f, 240f)
                lineTo(160f, 240f)
                lineTo(480f, 440f)
                close()
                moveTo(160f, 320f)
                lineTo(160f, 330f)
                quadTo(160f, 325f, 160f, 317.5f)
                quadTo(160f, 310f, 160f, 301f)
                quadTo(160f, 281f, 160f, 271f)
                quadTo(160f, 261f, 160f, 272f)
                lineTo(160f, 240f)
                lineTo(160f, 240f)
                lineTo(160f, 272f)
                quadTo(160f, 261f, 160f, 271.5f)
                quadTo(160f, 282f, 160f, 301f)
                quadTo(160f, 311f, 160f, 318.5f)
                quadTo(160f, 326f, 160f, 330f)
                lineTo(160f, 320f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                quadTo(160f, 720f, 160f, 720f)
                lineTo(160f, 320f)
                close()
            }
        }.build()

        return _Mail!!
    }

@Suppress("ObjectPropertyName")
private var _Mail: ImageVector? = null
