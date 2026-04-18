/*
 * Copyright 2026 Lawnchair Launcher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.ui.theme.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val LawnIcons.OpenCollective: ImageVector
    get() {
        if (_OpenCollective != null) {
            return _OpenCollective!!
        }
        _OpenCollective = ImageVector.Builder(
            name = "OpenCollective",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(fill = SolidColor(Color.Black)) {
                moveTo(5.171f, 12f)
                curveTo(5.171f, 8.228f, 8.228f, 5.171f, 12f, 5.171f)
                curveTo(12.858f, 5.171f, 13.681f, 5.33f, 14.439f, 5.62f)
                lineTo(15.37f, 4.689f)
                curveTo(14.346f, 4.216f, 13.205f, 3.951f, 12f, 3.951f)
                curveTo(7.555f, 3.951f, 3.951f, 7.555f, 3.951f, 12f)
                curveTo(3.951f, 16.445f, 7.555f, 20.049f, 12f, 20.049f)
                curveTo(13.205f, 20.049f, 14.346f, 19.784f, 15.37f, 19.311f)
                lineTo(14.439f, 18.38f)
                curveTo(13.681f, 18.67f, 12.858f, 18.829f, 12f, 18.829f)
                curveTo(8.228f, 18.829f, 5.171f, 15.772f, 5.171f, 12f)
                close()
                moveTo(20.049f, 12f)
                curveTo(20.049f, 10.795f, 19.784f, 9.654f, 19.311f, 8.63f)
                lineTo(18.38f, 9.56f)
                curveTo(18.67f, 10.319f, 18.829f, 11.142f, 18.829f, 12f)
                curveTo(18.829f, 12.858f, 18.67f, 13.681f, 18.38f, 14.439f)
                lineTo(19.311f, 15.37f)
                curveTo(19.784f, 14.346f, 20.049f, 13.205f, 20.049f, 12f)
                close()
                moveTo(7.122f, 12f)
                curveTo(7.122f, 14.694f, 9.306f, 16.878f, 12f, 16.878f)
                curveTo(12.799f, 16.878f, 13.55f, 16.687f, 14.213f, 16.348f)
                lineTo(14.249f, 16.331f)
                curveTo(14.618f, 16.161f, 15.057f, 16.238f, 15.346f, 16.528f)
                lineTo(17.662f, 18.843f)
                curveTo(17.869f, 19.05f, 17.972f, 19.339f, 17.942f, 19.631f)
                curveTo(17.913f, 19.922f, 17.754f, 20.185f, 17.51f, 20.346f)
                curveTo(15.929f, 21.392f, 14.034f, 22f, 12f, 22f)
                curveTo(6.477f, 22f, 2f, 17.523f, 2f, 12f)
                curveTo(2f, 6.477f, 6.477f, 2f, 12f, 2f)
                curveTo(14.034f, 2f, 15.929f, 2.608f, 17.51f, 3.654f)
                curveTo(17.754f, 3.815f, 17.913f, 4.078f, 17.942f, 4.369f)
                curveTo(17.972f, 4.661f, 17.869f, 4.95f, 17.662f, 5.157f)
                lineTo(15.346f, 7.472f)
                curveTo(15.047f, 7.771f, 14.59f, 7.844f, 14.213f, 7.652f)
                curveTo(13.55f, 7.313f, 12.799f, 7.122f, 12f, 7.122f)
                curveTo(9.306f, 7.122f, 7.122f, 9.306f, 7.122f, 12f)
                close()
                moveTo(22f, 12f)
                curveTo(22f, 14.034f, 21.392f, 15.929f, 20.346f, 17.51f)
                curveTo(20.185f, 17.754f, 19.922f, 17.913f, 19.631f, 17.942f)
                curveTo(19.339f, 17.972f, 19.05f, 17.869f, 18.843f, 17.662f)
                lineTo(16.528f, 15.346f)
                curveTo(16.229f, 15.047f, 16.156f, 14.59f, 16.348f, 14.213f)
                curveTo(16.687f, 13.55f, 16.878f, 12.799f, 16.878f, 12f)
                curveTo(16.878f, 11.201f, 16.687f, 10.45f, 16.348f, 9.787f)
                curveTo(16.156f, 9.41f, 16.229f, 8.953f, 16.528f, 8.654f)
                lineTo(18.843f, 6.338f)
                curveTo(19.05f, 6.131f, 19.339f, 6.028f, 19.631f, 6.058f)
                curveTo(19.922f, 6.087f, 20.185f, 6.246f, 20.346f, 6.49f)
                curveTo(21.392f, 8.07f, 22f, 9.966f, 22f, 12f)
                close()
            }
        }.build()

        return _OpenCollective!!
    }

@Suppress("ObjectPropertyName")
private var _OpenCollective: ImageVector? = null
