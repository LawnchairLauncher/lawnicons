package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.LauncherForeground: ImageVector
    get() {
        if (_LauncherForeground != null) {
            return _LauncherForeground!!
        }
        _LauncherForeground = ImageVector.Builder(
            name = "LauncherForeground",
            defaultWidth = 108.dp,
            defaultHeight = 108.dp,
            viewportWidth = 108f,
            viewportHeight = 108f,
        ).apply {
            group(
                scaleX = 0.65f,
                scaleY = 0.65f,
                translationX = 18.9f,
                translationY = 18.9f,
            ) {
                path(
                    fill = SolidColor(Color.White),
                    pathFillType = PathFillType.EvenOdd,
                ) {
                    moveTo(25.52f, 25.53f)
                    arcToRelative(
                        15.28f,
                        15.28f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        21.72f,
                        0f,
                    )
                    curveToRelative(6f, 6.04f, 6f, 15.83f, 0f, 21.87f)
                    arcToRelative(
                        15.28f,
                        15.28f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -21.72f,
                        0f,
                    )
                    curveToRelative(-6f, -6.04f, -6f, -15.83f, 0f, -21.87f)
                    close()
                    moveTo(42.35f, 30.46f)
                    arcToRelative(
                        8.4f,
                        8.4f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -11.93f,
                        0f,
                    )
                    curveToRelative(-3.3f, 3.32f, -3.3f, 8.7f, 0f, 12.01f)
                    arcToRelative(
                        8.4f,
                        8.4f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        11.93f,
                        0f,
                    )
                    curveToRelative(3.3f, -3.32f, 3.3f, -8.7f, 0f, -12.01f)
                    close()
                    moveTo(65.8f, 28.84f)
                    curveToRelative(-0.96f, 0f, -1.73f, 0.78f, -1.73f, 1.74f)
                    verticalLineToRelative(11.76f)
                    curveToRelative(0f, 0.96f, 0.77f, 1.74f, 1.73f, 1.74f)
                    horizontalLineToRelative(11.68f)
                    curveToRelative(0.95f, 0f, 1.73f, -0.78f, 1.73f, -1.74f)
                    lineTo(79.21f, 30.58f)
                    curveToRelative(0f, -0.96f, -0.77f, -1.74f, -1.73f, -1.74f)
                    lineTo(65.8f, 28.84f)
                    close()
                    moveTo(57.15f, 30.58f)
                    curveToRelative(0f, -4.81f, 3.87f, -8.71f, 8.65f, -8.71f)
                    horizontalLineToRelative(11.68f)
                    curveToRelative(4.78f, 0f, 8.65f, 3.9f, 8.65f, 8.71f)
                    verticalLineToRelative(11.76f)
                    curveToRelative(0f, 4.81f, -3.87f, 8.71f, -8.65f, 8.71f)
                    lineTo(65.8f, 51.06f)
                    curveToRelative(-4.78f, 0f, -8.65f, -3.9f, -8.65f, -8.71f)
                    lineTo(57.15f, 30.58f)
                    close()
                    moveTo(37.89f, 64.13f)
                    arcToRelative(
                        0.86f,
                        0.86f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1.5f,
                        0f,
                    )
                    lineToRelative(-8.34f, 14.54f)
                    arcToRelative(
                        0.87f,
                        0.87f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        0.75f,
                        1.31f,
                    )
                    horizontalLineToRelative(16.67f)
                    arcToRelative(
                        0.87f,
                        0.87f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        0.75f,
                        -1.31f,
                    )
                    lineToRelative(-8.34f, -14.54f)
                    close()
                    moveTo(30.39f, 60.64f)
                    curveToRelative(3f, -5.23f, 10.49f, -5.23f, 13.49f, 0f)
                    lineToRelative(8.34f, 14.54f)
                    curveToRelative(3f, 5.23f, -0.75f, 11.76f, -6.74f, 11.76f)
                    lineTo(28.8f, 86.94f)
                    curveToRelative(-5.99f, 0f, -9.74f, -6.53f, -6.74f, -11.76f)
                    lineToRelative(8.34f, -14.54f)
                    close()
                    moveTo(63.57f, 60.67f)
                    curveToRelative(1.64f, -2.75f, 4.64f, -4.6f, 8.07f, -4.6f)
                    curveToRelative(3.43f, 0f, 6.43f, 1.85f, 8.07f, 4.6f)
                    curveToRelative(0.62f, 1.04f, 1.68f, 2.11f, 2.72f, 2.74f)
                    arcTo(
                        9.48f,
                        9.48f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        87f,
                        71.54f,
                    )
                    arcToRelative(
                        9.48f,
                        9.48f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -4.57f,
                        8.13f,
                    )
                    curveToRelative(-1.03f, 0.63f, -2.1f, 1.7f, -2.72f, 2.74f)
                    curveToRelative(-1.64f, 2.75f, -4.64f, 4.6f, -8.07f, 4.6f)
                    curveToRelative(-3.43f, 0f, -6.43f, -1.85f, -8.07f, -4.6f)
                    curveToRelative(-0.62f, -1.04f, -1.68f, -2.11f, -2.72f, -2.74f)
                    arcToRelative(
                        9.49f,
                        9.49f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        -4.57f,
                        -8.13f,
                    )
                    arcToRelative(
                        9.48f,
                        9.48f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = true,
                        4.57f,
                        -8.13f,
                    )
                    curveToRelative(1.03f, -0.63f, 2.1f, -1.7f, 2.72f, -2.74f)
                    close()
                    moveTo(69.5f, 64.26f)
                    curveToRelative(-1.21f, 2.02f, -3.08f, 3.91f, -5.09f, 5.12f)
                    arcToRelative(
                        2.51f,
                        2.51f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -1.21f,
                        2.15f,
                    )
                    curveToRelative(0f, 0.91f, 0.48f, 1.71f, 1.21f, 2.15f)
                    curveToRelative(2.01f, 1.21f, 3.88f, 3.1f, 5.09f, 5.12f)
                    arcToRelative(
                        2.48f,
                        2.48f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        4.27f,
                        0f,
                    )
                    curveToRelative(1.21f, -2.02f, 3.08f, -3.91f, 5.09f, -5.12f)
                    arcToRelative(
                        2.51f,
                        2.51f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        1.21f,
                        -2.15f,
                    )
                    curveToRelative(0f, -0.91f, -0.48f, -1.71f, -1.21f, -2.15f)
                    curveToRelative(-2.01f, -1.21f, -3.88f, -3.1f, -5.09f, -5.13f)
                    arcToRelative(
                        2.48f,
                        2.48f,
                        0f,
                        isMoreThanHalf = false,
                        isPositiveArc = false,
                        -4.27f,
                        0f,
                    )
                    close()
                }
            }
        }.build()

        return _LauncherForeground!!
    }

@Suppress("ObjectPropertyName")
private var _LauncherForeground: ImageVector? = null
