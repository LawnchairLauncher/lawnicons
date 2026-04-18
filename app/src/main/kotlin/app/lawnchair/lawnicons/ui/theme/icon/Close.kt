package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Close: ImageVector
    get() {
        if (_Close != null) {
            return _Close!!
        }
        _Close = ImageVector.Builder(
            name = "Close",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 536f)
                lineTo(284f, 732f)
                quadTo(273f, 743f, 256f, 743f)
                quadTo(239f, 743f, 228f, 732f)
                quadTo(217f, 721f, 217f, 704f)
                quadTo(217f, 687f, 228f, 676f)
                lineTo(424f, 480f)
                lineTo(228f, 284f)
                quadTo(217f, 273f, 217f, 256f)
                quadTo(217f, 239f, 228f, 228f)
                quadTo(239f, 217f, 256f, 217f)
                quadTo(273f, 217f, 284f, 228f)
                lineTo(480f, 424f)
                lineTo(676f, 228f)
                quadTo(687f, 217f, 704f, 217f)
                quadTo(721f, 217f, 732f, 228f)
                quadTo(743f, 239f, 743f, 256f)
                quadTo(743f, 273f, 732f, 284f)
                lineTo(536f, 480f)
                lineTo(732f, 676f)
                quadTo(743f, 687f, 743f, 704f)
                quadTo(743f, 721f, 732f, 732f)
                quadTo(721f, 743f, 704f, 743f)
                quadTo(687f, 743f, 676f, 732f)
                lineTo(480f, 536f)
                close()
            }
        }.build()

        return _Close!!
    }

@Suppress("ObjectPropertyName")
private var _Close: ImageVector? = null
