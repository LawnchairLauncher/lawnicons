package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.IconRequest: ImageVector
    get() {
        if (_IconRequest != null) {
            return _IconRequest!!
        }
        _IconRequest = ImageVector.Builder(
            name = "IconRequest",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(3f, 18f)
                verticalLineTo(6.5f)
                moveTo(21f, 13f)
                verticalLineToRelative(-2f)
                moveToRelative(-9f, 4f)
                verticalLineTo(9f)
                moveTo(6.5f, 3f)
                horizontalLineToRelative(12f)
                moveTo(11f, 21f)
                horizontalLineToRelative(2f)
                moveToRelative(-4f, -9f)
                horizontalLineToRelative(6f)
                moveToRelative(6f, -6f)
                verticalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, -2f, -2f)
                horizontalLineToRelative(-0.5f)
                moveTo(21f, 18f)
                verticalLineToRelative(1f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, -2f, 2f)
                horizontalLineToRelative(-1f)
                moveTo(3f, 6.5f)
                verticalLineTo(5f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 2f, -2f)
                horizontalLineToRelative(1.5f)
                moveTo(3f, 18f)
                verticalLineToRelative(1f)
                arcToRelative(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = false, 2f, 2f)
                horizontalLineToRelative(1f)
            }
        }.build()

        return _IconRequest!!
    }

@Suppress("ObjectPropertyName")
private var _IconRequest: ImageVector? = null
