package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Palette: ImageVector
    get() {
        if (_Palette != null) {
            return _Palette!!
        }
        _Palette = ImageVector.Builder(
            name = "Palette",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 960f,
            viewportHeight = 960f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(480f, 880f)
                quadTo(397f, 880f, 324f, 848.5f)
                quadTo(251f, 817f, 197f, 763f)
                quadTo(143f, 709f, 111.5f, 636f)
                quadTo(80f, 563f, 80f, 480f)
                quadTo(80f, 396f, 112f, 323.5f)
                quadTo(144f, 251f, 198f, 197f)
                quadTo(252f, 143f, 325.5f, 111.5f)
                quadTo(399f, 80f, 484f, 80f)
                quadTo(560f, 80f, 629f, 108f)
                quadTo(698f, 136f, 750f, 183.5f)
                quadTo(802f, 231f, 831f, 295f)
                quadTo(860f, 359f, 860f, 434f)
                quadTo(860f, 540f, 800.5f, 590f)
                quadTo(741f, 640f, 660f, 640f)
                lineTo(580f, 640f)
                quadTo(563f, 640f, 551.5f, 652.5f)
                quadTo(540f, 665f, 540f, 680f)
                quadTo(540f, 700f, 554f, 718f)
                quadTo(568f, 736f, 568f, 760f)
                quadTo(568f, 808f, 539.5f, 844f)
                quadTo(511f, 880f, 480f, 880f)
                close()
                moveTo(240f, 520f)
                quadTo(257f, 520f, 268.5f, 508.5f)
                quadTo(280f, 497f, 280f, 480f)
                quadTo(280f, 463f, 268.5f, 451.5f)
                quadTo(257f, 440f, 240f, 440f)
                quadTo(223f, 440f, 211.5f, 451.5f)
                quadTo(200f, 463f, 200f, 480f)
                quadTo(200f, 497f, 211.5f, 508.5f)
                quadTo(223f, 520f, 240f, 520f)
                close()
                moveTo(360f, 360f)
                quadTo(377f, 360f, 388.5f, 348.5f)
                quadTo(400f, 337f, 400f, 320f)
                quadTo(400f, 303f, 388.5f, 291.5f)
                quadTo(377f, 280f, 360f, 280f)
                quadTo(343f, 280f, 331.5f, 291.5f)
                quadTo(320f, 303f, 320f, 320f)
                quadTo(320f, 337f, 331.5f, 348.5f)
                quadTo(343f, 360f, 360f, 360f)
                close()
                moveTo(600f, 360f)
                quadTo(617f, 360f, 628.5f, 348.5f)
                quadTo(640f, 337f, 640f, 320f)
                quadTo(640f, 303f, 628.5f, 291.5f)
                quadTo(617f, 280f, 600f, 280f)
                quadTo(583f, 280f, 571.5f, 291.5f)
                quadTo(560f, 303f, 560f, 320f)
                quadTo(560f, 337f, 571.5f, 348.5f)
                quadTo(583f, 360f, 600f, 360f)
                close()
                moveTo(720f, 520f)
                quadTo(737f, 520f, 748.5f, 508.5f)
                quadTo(760f, 497f, 760f, 480f)
                quadTo(760f, 463f, 748.5f, 451.5f)
                quadTo(737f, 440f, 720f, 440f)
                quadTo(703f, 440f, 691.5f, 451.5f)
                quadTo(680f, 463f, 680f, 480f)
                quadTo(680f, 497f, 691.5f, 508.5f)
                quadTo(703f, 520f, 720f, 520f)
                close()
            }
        }.build()

        return _Palette!!
    }

@Suppress("ObjectPropertyName")
private var _Palette: ImageVector? = null
