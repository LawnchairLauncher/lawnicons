package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.KeyboardArrowDown: ImageVector
    get() {
        if (_KeyboardArrowDown != null) {
            return _KeyboardArrowDown!!
        }
        _KeyboardArrowDown = ImageVector.Builder(
            name = "KeyboardArrowDown",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 599f)
                quadTo(472f, 599f, 465f, 596.5f)
                quadTo(458f, 594f, 452f, 588f)
                lineTo(268f, 404f)
                quadTo(257f, 393f, 257f, 376f)
                quadTo(257f, 359f, 268f, 348f)
                quadTo(279f, 337f, 296f, 337f)
                quadTo(313f, 337f, 324f, 348f)
                lineTo(480f, 504f)
                lineTo(636f, 348f)
                quadTo(647f, 337f, 664f, 337f)
                quadTo(681f, 337f, 692f, 348f)
                quadTo(703f, 359f, 703f, 376f)
                quadTo(703f, 393f, 692f, 404f)
                lineTo(508f, 588f)
                quadTo(502f, 594f, 495f, 596.5f)
                quadTo(488f, 599f, 480f, 599f)
                close()
            }
        }.build()

        return _KeyboardArrowDown!!
    }

@Suppress("ObjectPropertyName")
private var _KeyboardArrowDown: ImageVector? = null
