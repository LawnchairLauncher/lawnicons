package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Back: ImageVector
    get() {
        if (_Back != null) {
            return _Back!!
        }
        _Back = ImageVector.Builder(
            name = "Back",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
            autoMirror = true,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(313f, 520f)
                lineTo(509f, 716f)
                quadTo(521f, 728f, 520.5f, 744f)
                quadTo(520f, 760f, 508f, 772f)
                quadTo(496f, 783f, 480f, 783.5f)
                quadTo(464f, 784f, 452f, 772f)
                lineTo(188f, 508f)
                quadTo(182f, 502f, 179.5f, 495f)
                quadTo(177f, 488f, 177f, 480f)
                quadTo(177f, 472f, 179.5f, 465f)
                quadTo(182f, 458f, 188f, 452f)
                lineTo(452f, 188f)
                quadTo(463f, 177f, 479.5f, 177f)
                quadTo(496f, 177f, 508f, 188f)
                quadTo(520f, 200f, 520f, 216.5f)
                quadTo(520f, 233f, 508f, 245f)
                lineTo(313f, 440f)
                lineTo(760f, 440f)
                quadTo(777f, 440f, 788.5f, 451.5f)
                quadTo(800f, 463f, 800f, 480f)
                quadTo(800f, 497f, 788.5f, 508.5f)
                quadTo(777f, 520f, 760f, 520f)
                lineTo(313f, 520f)
                close()
            }
        }.build()

        return _Back!!
    }

@Suppress("ObjectPropertyName")
private var _Back: ImageVector? = null
