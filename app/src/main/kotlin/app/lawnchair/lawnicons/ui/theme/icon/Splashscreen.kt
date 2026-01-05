package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Splashscreen: ImageVector
    get() {
        if (_Splashscreen != null) {
            return _Splashscreen!!
        }
        _Splashscreen = ImageVector.Builder(
            name = "Splashscreen",
            defaultWidth = 240.dp,
            defaultHeight = 240.dp,
            viewportWidth = 240f,
            viewportHeight = 240f,
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(120f, 40f)
                    lineTo(120f, 40f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 200f, 120f)
                    lineTo(200f, 120f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 120f, 200f)
                    lineTo(120f, 200f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 40f, 120f)
                    lineTo(40f, 120f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 120f, 40f)
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3E9EFF),
                            1f to Color(0xFF3975FF),
                        ),
                        start = Offset(120f, 40f),
                        end = Offset(120f, 180.16f),
                    ),
                ) {
                    moveTo(120f, 40f)
                    lineTo(120f, 40f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 200f, 120f)
                    lineTo(200f, 120f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 120f, 200f)
                    lineTo(120f, 200f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 40f, 120f)
                    lineTo(40f, 120f)
                    arcTo(80f, 80f, 0f, isMoreThanHalf = false, isPositiveArc = true, 120f, 40f)
                    close()
                }
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF1EF0A2),
                            1f to Color(0xFF0ADA65),
                        ),
                        start = Offset(120f, 131.32f),
                        end = Offset(120f, 211.62f),
                    ),
                ) {
                    moveTo(247.41f, 254.82f)
                    curveToRelative(0f, 70.36f, -57.04f, 127.41f, -127.41f, 127.41f)
                    reflectiveCurveTo(-7.41f, 325.18f, -7.41f, 254.82f)
                    reflectiveCurveTo(49.63f, 127.41f, 120f, 127.41f)
                    reflectiveCurveToRelative(127.41f, 57.04f, 127.41f, 127.41f)
                    close()
                }
                path(
                    fill = SolidColor(Color.White),
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    moveTo(78.57f, 78.59f)
                    curveToRelative(8.73f, -8.78f, 22.87f, -8.78f, 31.59f, 0f)
                    curveToRelative(8.73f, 8.78f, 8.73f, 23.03f, 0f, 31.81f)
                    curveToRelative(-8.73f, 8.78f, -22.87f, 8.78f, -31.59f, 0f)
                    curveToRelative(-8.72f, -8.78f, -8.72f, -23.03f, 0f, -31.81f)
                    close()
                    moveTo(103.05f, 85.76f)
                    arcToRelative(
                        12.21f,
                        12.21f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -17.35f,
                        0f,
                    )
                    curveToRelative(-4.79f, 4.82f, -4.79f, 12.65f, 0f, 17.47f)
                    arcToRelative(
                        12.21f,
                        12.21f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        17.35f,
                        0f,
                    )
                    curveToRelative(4.79f, -4.82f, 4.79f, -12.65f, 0f, -17.47f)
                    close()
                    moveTo(137.16f, 83.41f)
                    arcToRelative(
                        2.53f,
                        2.53f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -2.52f,
                        2.54f,
                    )
                    verticalLineToRelative(17.11f)
                    arcToRelative(
                        2.53f,
                        2.53f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        2.52f,
                        2.53f,
                    )
                    horizontalLineToRelative(16.99f)
                    arcToRelative(
                        2.53f,
                        2.53f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        2.52f,
                        -2.53f,
                    )
                    lineTo(156.67f, 85.94f)
                    curveToRelative(0f, -1.4f, -1.13f, -2.53f, -2.52f, -2.53f)
                    horizontalLineToRelative(-16.99f)
                    close()
                    moveTo(124.58f, 85.94f)
                    curveToRelative(0f, -7f, 5.64f, -12.67f, 12.59f, -12.67f)
                    horizontalLineToRelative(16.99f)
                    curveToRelative(6.95f, 0f, 12.59f, 5.67f, 12.59f, 12.67f)
                    verticalLineToRelative(17.11f)
                    curveToRelative(0f, 7f, -5.64f, 12.67f, -12.59f, 12.67f)
                    horizontalLineToRelative(-16.99f)
                    curveToRelative(-6.95f, 0f, -12.59f, -5.67f, -12.59f, -12.67f)
                    lineTo(124.58f, 85.94f)
                    close()
                    moveTo(96.56f, 134.73f)
                    arcToRelative(
                        1.25f,
                        1.25f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -2.18f,
                        0f,
                    )
                    lineToRelative(-12.13f, 21.15f)
                    curveToRelative(-0.49f, 0.85f, 0.12f, 1.9f, 1.09f, 1.9f)
                    horizontalLineToRelative(24.25f)
                    curveToRelative(0.97f, 0f, 1.58f, -1.06f, 1.09f, -1.9f)
                    lineToRelative(-12.13f, -21.15f)
                    close()
                    moveTo(85.66f, 129.66f)
                    curveToRelative(4.36f, -7.6f, 15.26f, -7.6f, 19.62f, 0f)
                    lineToRelative(12.13f, 21.15f)
                    curveToRelative(4.36f, 7.6f, -1.09f, 17.11f, -9.81f, 17.11f)
                    lineTo(83.35f, 167.92f)
                    curveToRelative(-8.72f, 0f, -14.17f, -9.51f, -9.81f, -17.11f)
                    lineToRelative(12.13f, -21.15f)
                    close()
                    moveTo(133.92f, 129.7f)
                    curveToRelative(2.38f, -4f, 6.75f, -6.69f, 11.74f, -6.69f)
                    curveToRelative(4.99f, 0f, 9.35f, 2.69f, 11.74f, 6.69f)
                    curveToRelative(0.9f, 1.51f, 2.45f, 3.07f, 3.96f, 3.98f)
                    arcToRelative(
                        13.8f,
                        13.8f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        6.65f,
                        11.82f,
                    )
                    curveToRelative(0f, 5.03f, -2.67f, 9.42f, -6.65f, 11.82f)
                    curveToRelative(-1.5f, 0.91f, -3.05f, 2.47f, -3.96f, 3.98f)
                    curveToRelative(-2.38f, 4f, -6.75f, 6.69f, -11.74f, 6.69f)
                    curveToRelative(-4.99f, 0f, -9.35f, -2.69f, -11.74f, -6.69f)
                    curveToRelative(-0.9f, -1.51f, -2.45f, -3.07f, -3.95f, -3.98f)
                    arcToRelative(
                        13.8f,
                        13.8f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -6.65f,
                        -11.82f,
                    )
                    arcToRelative(
                        13.8f,
                        13.8f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        6.65f,
                        -11.82f,
                    )
                    curveToRelative(1.51f, -0.91f, 3.05f, -2.47f, 3.96f, -3.98f)
                    close()
                    moveTo(142.55f, 134.92f)
                    curveToRelative(-1.75f, 2.94f, -4.48f, 5.69f, -7.4f, 7.45f)
                    arcToRelative(
                        3.64f,
                        3.64f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1.76f,
                        3.13f,
                    )
                    curveToRelative(0f, 1.32f, 0.69f, 2.48f, 1.76f, 3.13f)
                    curveToRelative(2.92f, 1.76f, 5.65f, 4.51f, 7.4f, 7.45f)
                    arcToRelative(
                        3.61f,
                        3.61f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        6.22f,
                        0f,
                    )
                    curveToRelative(1.75f, -2.94f, 4.48f, -5.69f, 7.4f, -7.45f)
                    arcToRelative(
                        3.64f,
                        3.64f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        1.76f,
                        -3.13f,
                    )
                    arcToRelative(
                        3.64f,
                        3.64f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1.76f,
                        -3.13f,
                    )
                    curveToRelative(-2.92f, -1.76f, -5.65f, -4.51f, -7.4f, -7.45f)
                    arcToRelative(
                        3.61f,
                        3.61f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -6.21f,
                        0f,
                    )
                    close()
                }
            }
        }.build()

        return _Splashscreen!!
    }

@Suppress("ObjectPropertyName")
private var _Splashscreen: ImageVector? = null
