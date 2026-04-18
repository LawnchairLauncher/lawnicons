package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Discord: ImageVector
    get() {
        if (_Discord != null) {
            return _Discord!!
        }
        _Discord = ImageVector.Builder(
            name = "Discord",
            defaultWidth = 192.dp,
            defaultHeight = 192.dp,
            viewportWidth = 192f,
            viewportHeight = 192f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 14f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(68.51f, 136.28f)
                lineTo(60.66f, 152f)
                curveToRelative(-10f, -4.17f, -20.36f, -8.34f, -31.37f, -15.52f)
                curveToRelative(-3.84f, -2.51f, -6.17f, -6.76f, -6.26f, -11.35f)
                curveToRelative(-0.48f, -23.55f, 5.04f, -47.22f, 19.01f, -72.25f)
                curveToRelative(1.83f, -3.28f, 4.88f, -5.68f, 8.41f, -6.93f)
                curveToRelative(8.43f, -2.99f, 14.15f, -5.31f, 23.95f, -6.95f)
                lineToRelative(5.89f, 10.81f)
                reflectiveCurveToRelative(5.89f, -1.97f, 15.71f, -1.97f)
                curveToRelative(9.82f, 0f, 15.7f, 1.97f, 15.7f, 1.97f)
                lineTo(117.6f, 39f)
                curveToRelative(9.8f, 1.63f, 15.52f, 3.95f, 23.95f, 6.95f)
                curveToRelative(3.53f, 1.26f, 6.58f, 3.66f, 8.41f, 6.93f)
                curveToRelative(13.97f, 25.03f, 19.49f, 48.7f, 19.01f, 72.25f)
                curveToRelative(-0.09f, 4.59f, -2.42f, 8.84f, -6.26f, 11.35f)
                curveToRelative(-11.01f, 7.18f, -21.37f, 11.35f, -31.37f, 15.52f)
                lineToRelative(-7.85f, -15.72f)
                moveToRelative(-66.75f, -7.86f)
                reflectiveCurveToRelative(19.63f, 9.83f, 39.26f, 9.83f)
                reflectiveCurveToRelative(39.26f, -9.83f, 39.26f, -9.83f)
            }
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 12f,
            ) {
                moveTo(70.63f, 106f)
                curveToRelative(3.3f, 0f, 5.97f, -3.58f, 5.97f, -8f)
                reflectiveCurveToRelative(-2.67f, -8f, -5.97f, -8f)
                curveToRelative(-3.3f, 0f, -5.97f, 3.58f, -5.97f, 8f)
                reflectiveCurveToRelative(2.67f, 8f, 5.97f, 8f)
                close()
                moveTo(120.37f, 106f)
                curveToRelative(3.3f, 0f, 5.97f, -3.58f, 5.97f, -8f)
                reflectiveCurveToRelative(-2.67f, -8f, -5.97f, -8f)
                curveToRelative(-3.3f, 0f, -5.97f, 3.58f, -5.97f, 8f)
                reflectiveCurveToRelative(2.67f, 8f, 5.97f, 8f)
                close()
            }
        }.build()

        return _Discord!!
    }

@Suppress("ObjectPropertyName")
private var _Discord: ImageVector? = null
