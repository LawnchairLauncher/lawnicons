package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.LauncherBackground: ImageVector
    get() {
        if (_LauncherBackground != null) {
            return _LauncherBackground!!
        }
        _LauncherBackground = ImageVector.Builder(
            name = "LauncherBackground",
            defaultWidth = 108.dp,
            defaultHeight = 108.dp,
            viewportWidth = 108f,
            viewportHeight = 108f,
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(108f)
                    verticalLineToRelative(108f)
                    horizontalLineTo(0f)
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3E9EFF),
                            1f to Color(0xFF3975FF),
                        ),
                        start = Offset(54f, 0f),
                        end = Offset(54f, 94.61f),
                    ),
                ) {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(108f)
                    verticalLineToRelative(108f)
                    horizontalLineTo(0f)
                    close()
                }
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF1EF0A2),
                            1f to Color(0xFF0ADA65),
                        ),
                        start = Offset(54f, 61.64f),
                        end = Offset(54f, 115.84f),
                    ),
                ) {
                    moveTo(140f, 145f)
                    curveToRelative(0f, 47.5f, -38.5f, 86f, -86f, 86f)
                    reflectiveCurveToRelative(-86f, -38.5f, -86f, -86f)
                    reflectiveCurveTo(6.5f, 59f, 54f, 59f)
                    reflectiveCurveToRelative(86f, 38.5f, 86f, 86f)
                    close()
                }
            }
        }.build()

        return _LauncherBackground!!
    }

@Suppress("ObjectPropertyName")
private var _LauncherBackground: ImageVector? = null
