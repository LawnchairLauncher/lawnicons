package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Github: ImageVector
    get() {
        if (_Github != null) {
            return _Github!!
        }
        _Github = ImageVector.Builder(
            name = "Github",
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
                moveTo(121.13f, 169f)
                curveToRelative(0.03f, -4.61f, 0.06f, -20.59f, 0.06f, -26.92f)
                curveToRelative(0f, -9.15f, -3.11f, -15.13f, -6.6f, -18.16f)
                curveTo(136.25f, 121.49f, 159f, 113.2f, 159f, 75.51f)
                curveToRelative(0f, -10.71f, -3.76f, -19.47f, -10f, -26.32f)
                curveToRelative(1f, -2.48f, 4.34f, -12.46f, -0.97f, -25.96f)
                curveToRelative(0f, 0f, -8.15f, -2.64f, -26.73f, 10.07f)
                curveToRelative(-7.78f, -2.18f, -16.09f, -3.26f, -24.35f, -3.31f)
                curveToRelative(-8.27f, 0.04f, -16.58f, 1.13f, -24.35f, 3.31f)
                curveTo(54.01f, 20.59f, 45.86f, 23.23f, 45.86f, 23.23f)
                curveToRelative(-5.31f, 13.51f, -1.97f, 23.48f, -0.97f, 25.96f)
                curveToRelative(-6.24f, 6.85f, -10f, 15.61f, -10f, 26.32f)
                curveToRelative(0f, 37.69f, 22.75f, 45.97f, 44.42f, 48.4f)
                curveToRelative(-3.49f, 3.03f, -6.6f, 9.01f, -6.6f, 18.16f)
                curveToRelative(0f, 6.33f, 0.03f, 22.32f, 0.06f, 26.92f)
                moveTo(29f, 129.54f)
                curveToRelative(9.77f, 0.69f, 15.4f, 9.6f, 15.4f, 9.6f)
                curveToRelative(8.68f, 14.99f, 22.78f, 10.66f, 28.32f, 8.15f)
            }
        }.build()

        return _Github!!
    }

@Suppress("ObjectPropertyName")
private var _Github: ImageVector? = null
