package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathData
import androidx.compose.ui.graphics.vector.group
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.Lawnicons: ImageVector
    get() {
        if (_Lawnicons != null) {
            return _Lawnicons!!
        }
        _Lawnicons = ImageVector.Builder(
            name = "Lawnicons",
            defaultWidth = 512.dp,
            defaultHeight = 512.dp,
            viewportWidth = 512f,
            viewportHeight = 512f,
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(256f, 0f)
                    lineTo(256f, 0f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 512f, 256f)
                    lineTo(512f, 256f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 256f, 512f)
                    lineTo(256f, 512f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 256f)
                    lineTo(0f, 256f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 256f, 0f)
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3E9EFF),
                            1f to Color(0xFF3975FF),
                        ),
                        start = Offset(256f, 0f),
                        end = Offset(256f, 448.5f),
                    ),
                ) {
                    moveTo(256f, 0f)
                    lineTo(256f, 0f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 512f, 256f)
                    lineTo(512f, 256f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 256f, 512f)
                    lineTo(256f, 512f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 256f)
                    lineTo(0f, 256f)
                    arcTo(256f, 256f, 0f, isMoreThanHalf = false, isPositiveArc = true, 256f, 0f)
                    close()
                }
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF1EF0A2),
                            1f to Color(0xFF0ADA65),
                        ),
                        start = Offset(256f, 293.5f),
                        end = Offset(256f, 550f),
                    ),
                ) {
                    moveTo(256f, 688f)
                    moveToRelative(-407f, 0f)
                    arcToRelative(
                        407f,
                        407f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        814f,
                        0f,
                    )
                    arcToRelative(
                        407f,
                        407f,
                        0f,
                        isMoreThanHalf = true,
                        isPositiveArc = true,
                        -814f,
                        0f,
                    )
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 32f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(136.11f, 136.11f)
                    curveTo(114.63f, 157.59f, 114.63f, 192.41f, 136.11f, 213.89f)
                    curveTo(157.59f, 235.37f, 192.41f, 235.37f, 213.89f, 213.89f)
                    curveTo(235.37f, 192.41f, 235.37f, 157.59f, 213.89f, 136.11f)
                    curveTo(192.41f, 114.63f, 157.59f, 114.63f, 136.11f, 136.11f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 32f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(287f, 148f)
                    curveTo(287f, 134.74f, 297.74f, 124f, 311f, 124f)
                    horizontalLineTo(365f)
                    curveTo(378.26f, 124f, 389f, 134.74f, 389f, 148f)
                    verticalLineTo(202f)
                    curveTo(389f, 215.26f, 378.26f, 226f, 365f, 226f)
                    horizontalLineTo(311f)
                    curveTo(297.74f, 226f, 287f, 215.26f, 287f, 202f)
                    verticalLineTo(148f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 32f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(161.18f, 294f)
                    curveTo(168.88f, 280.67f, 188.12f, 280.67f, 195.82f, 294f)
                    lineTo(234.36f, 360.75f)
                    curveTo(242.06f, 374.08f, 232.43f, 390.75f, 217.04f, 390.75f)
                    horizontalLineTo(139.96f)
                    curveTo(124.57f, 390.75f, 114.94f, 374.08f, 122.64f, 360.75f)
                    lineTo(161.18f, 294f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 32f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(314.41f, 377.64f)
                    curveTo(319.22f, 385.64f, 327.98f, 391f, 338f, 391f)
                    curveTo(348.02f, 391f, 356.78f, 385.64f, 361.59f, 377.64f)
                    curveTo(365.81f, 370.61f, 372.61f, 363.81f, 379.64f, 359.59f)
                    curveTo(387.64f, 354.78f, 393f, 346.02f, 393f, 336f)
                    curveTo(393f, 325.98f, 387.64f, 317.22f, 379.64f, 312.41f)
                    curveTo(372.61f, 308.19f, 365.81f, 301.39f, 361.59f, 294.36f)
                    curveTo(356.78f, 286.36f, 348.02f, 281f, 338f, 281f)
                    curveTo(327.98f, 281f, 319.22f, 286.36f, 314.41f, 294.36f)
                    curveTo(310.19f, 301.39f, 303.39f, 308.19f, 296.36f, 312.41f)
                    curveTo(288.36f, 317.22f, 283f, 325.98f, 283f, 336f)
                    curveTo(283f, 346.02f, 288.36f, 354.78f, 296.36f, 359.59f)
                    curveTo(303.39f, 363.81f, 310.19f, 370.61f, 314.41f, 377.64f)
                    close()
                }
            }
        }.build()

        return _Lawnicons!!
    }

@Suppress("ObjectPropertyName")
private var _Lawnicons: ImageVector? = null
