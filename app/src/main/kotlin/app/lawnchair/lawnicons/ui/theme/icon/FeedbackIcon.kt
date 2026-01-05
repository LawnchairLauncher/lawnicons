package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.FeedbackIcon: ImageVector
    get() {
        if (_FeedbackIcon != null) {
            return _FeedbackIcon!!
        }
        _FeedbackIcon = ImageVector.Builder(
            name = "FeedbackIcon",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 600f)
                quadTo(497f, 600f, 508.5f, 588.5f)
                quadTo(520f, 577f, 520f, 560f)
                quadTo(520f, 543f, 508.5f, 531.5f)
                quadTo(497f, 520f, 480f, 520f)
                quadTo(463f, 520f, 451.5f, 531.5f)
                quadTo(440f, 543f, 440f, 560f)
                quadTo(440f, 577f, 451.5f, 588.5f)
                quadTo(463f, 600f, 480f, 600f)
                close()
                moveTo(480f, 440f)
                quadTo(497f, 440f, 508.5f, 428.5f)
                quadTo(520f, 417f, 520f, 400f)
                lineTo(520f, 240f)
                quadTo(520f, 223f, 508.5f, 211.5f)
                quadTo(497f, 200f, 480f, 200f)
                quadTo(463f, 200f, 451.5f, 211.5f)
                quadTo(440f, 223f, 440f, 240f)
                lineTo(440f, 400f)
                quadTo(440f, 417f, 451.5f, 428.5f)
                quadTo(463f, 440f, 480f, 440f)
                close()
                moveTo(240f, 720f)
                lineTo(148f, 812f)
                quadTo(129f, 831f, 104.5f, 820.5f)
                quadTo(80f, 810f, 80f, 783f)
                lineTo(80f, 160f)
                quadTo(80f, 127f, 103.5f, 103.5f)
                quadTo(127f, 80f, 160f, 80f)
                lineTo(800f, 80f)
                quadTo(833f, 80f, 856.5f, 103.5f)
                quadTo(880f, 127f, 880f, 160f)
                lineTo(880f, 640f)
                quadTo(880f, 673f, 856.5f, 696.5f)
                quadTo(833f, 720f, 800f, 720f)
                lineTo(240f, 720f)
                close()
                moveTo(206f, 640f)
                lineTo(800f, 640f)
                quadTo(800f, 640f, 800f, 640f)
                quadTo(800f, 640f, 800f, 640f)
                lineTo(800f, 160f)
                quadTo(800f, 160f, 800f, 160f)
                quadTo(800f, 160f, 800f, 160f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 685f)
                lineTo(206f, 640f)
                close()
                moveTo(160f, 640f)
                lineTo(160f, 640f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                quadTo(160f, 160f, 160f, 160f)
                lineTo(160f, 640f)
                quadTo(160f, 640f, 160f, 640f)
                quadTo(160f, 640f, 160f, 640f)
                close()
            }
        }.build()

        return _FeedbackIcon!!
    }

@Suppress("ObjectPropertyName")
private var _FeedbackIcon: ImageVector? = null
