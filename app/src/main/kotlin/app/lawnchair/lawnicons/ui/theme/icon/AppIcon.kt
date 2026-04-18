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

val LawnIcons.AppIcon: ImageVector
    get() {
        if (_AppIcon != null) {
            return _AppIcon!!
        }
        _AppIcon = ImageVector.Builder(
            name = "Lawnicons",
            defaultWidth = 72.dp,
            defaultHeight = 72.dp,
            viewportWidth = 72f,
            viewportHeight = 72f,
        ).apply {
            group(
                clipPathData = PathData {
                    moveTo(0f, 0f)
                    horizontalLineToRelative(72f)
                    verticalLineToRelative(72f)
                    horizontalLineToRelative(-72f)
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF3399FF),
                            1f to Color(0xFF3369FF),
                        ),
                        start = Offset(36f, -18f),
                        end = Offset(36f, 90f),
                    ),
                ) {
                    moveTo(-18f, -18f)
                    horizontalLineToRelative(108f)
                    verticalLineToRelative(108f)
                    horizontalLineToRelative(-108f)
                    close()
                }
            }
            group(
                clipPathData = PathData {
                    moveTo(-18f, -18f)
                    horizontalLineToRelative(108f)
                    verticalLineToRelative(108f)
                    horizontalLineToRelative(-108f)
                    close()
                },
            ) {
                path(
                    fill = Brush.linearGradient(
                        colorStops = arrayOf(
                            0f to Color(0xFF12ED9D),
                            1f to Color(0xFF24DB73),
                        ),
                        start = Offset(36f, 39.985f),
                        end = Offset(36f, 73.477f),
                    ),
                ) {
                    moveTo(36f, 39f)
                    curveTo(72.451f, 39f, 102f, 68.549f, 102f, 105f)
                    curveTo(102f, 141.451f, 72.451f, 171f, 36f, 171f)
                    curveTo(-0.451f, 171f, -30f, 141.451f, -30f, 105f)
                    curveTo(-30f, 68.549f, -0.451f, 39f, 36f, 39f)
                    close()
                }
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 0.03f,
                    stroke = SolidColor(Color.Black),
                    strokeAlpha = 0.03f,
                    strokeLineWidth = 2.52353f,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(47.466f, 41.253f)
                    curveTo(49.235f, 41.253f, 50.783f, 42.2f, 51.631f, 43.611f)
                    curveTo(52.078f, 44.354f, 52.776f, 45.09f, 53.513f, 45.605f)
                    lineTo(53.83f, 45.811f)
                    curveTo(55.241f, 46.658f, 56.189f, 48.206f, 56.189f, 49.975f)
                    curveTo(56.189f, 51.744f, 55.241f, 53.291f, 53.83f, 54.139f)
                    curveTo(52.981f, 54.649f, 52.141f, 55.489f, 51.631f, 56.339f)
                    curveTo(50.783f, 57.75f, 49.235f, 58.696f, 47.466f, 58.696f)
                    curveTo(45.697f, 58.696f, 44.15f, 57.75f, 43.302f, 56.339f)
                    curveTo(42.792f, 55.489f, 41.953f, 54.649f, 41.103f, 54.139f)
                    curveTo(39.692f, 53.291f, 38.744f, 51.744f, 38.744f, 49.975f)
                    curveTo(38.744f, 48.317f, 39.577f, 46.853f, 40.844f, 45.978f)
                    lineTo(41.103f, 45.811f)
                    curveTo(41.952f, 45.3f, 42.792f, 44.461f, 43.302f, 43.612f)
                    verticalLineTo(43.611f)
                    curveTo(44.15f, 42.2f, 45.697f, 41.253f, 47.466f, 41.253f)
                    close()
                    moveTo(21.747f, 43.575f)
                    curveTo(23.21f, 41.042f, 26.867f, 41.042f, 28.329f, 43.575f)
                    horizontalLineTo(28.33f)
                    lineTo(33.749f, 52.961f)
                    curveTo(35.212f, 55.495f, 33.383f, 58.661f, 30.457f, 58.661f)
                    horizontalLineTo(19.619f)
                    curveTo(16.694f, 58.661f, 14.866f, 55.495f, 16.328f, 52.961f)
                    lineTo(21.747f, 43.575f)
                    close()
                    moveTo(47.466f, 43.23f)
                    curveTo(46.422f, 43.23f, 45.503f, 43.786f, 44.996f, 44.628f)
                    verticalLineTo(44.629f)
                    curveTo(44.319f, 45.756f, 43.248f, 46.828f, 42.12f, 47.505f)
                    curveTo(41.278f, 48.011f, 40.721f, 48.93f, 40.721f, 49.975f)
                    curveTo(40.721f, 51.02f, 41.278f, 51.939f, 42.12f, 52.446f)
                    curveTo(43.248f, 53.123f, 44.319f, 54.194f, 44.996f, 55.321f)
                    curveTo(45.503f, 56.164f, 46.422f, 56.721f, 47.466f, 56.721f)
                    curveTo(48.445f, 56.721f, 49.315f, 56.232f, 49.837f, 55.476f)
                    lineTo(49.937f, 55.321f)
                    curveTo(50.614f, 54.194f, 51.685f, 53.123f, 52.813f, 52.446f)
                    verticalLineTo(52.445f)
                    curveTo(53.655f, 51.938f, 54.212f, 51.02f, 54.212f, 49.975f)
                    curveTo(54.212f, 48.93f, 53.656f, 48.012f, 52.813f, 47.505f)
                    curveTo(51.685f, 46.828f, 50.613f, 45.756f, 49.936f, 44.628f)
                    curveTo(49.429f, 43.786f, 48.51f, 43.23f, 47.466f, 43.23f)
                    close()
                    moveTo(26.618f, 44.564f)
                    curveTo(25.938f, 43.385f, 24.269f, 43.348f, 23.528f, 44.453f)
                    lineTo(23.458f, 44.564f)
                    lineTo(18.039f, 53.95f)
                    curveTo(17.338f, 55.166f, 18.215f, 56.686f, 19.619f, 56.686f)
                    horizontalLineTo(30.457f)
                    curveTo(31.861f, 56.686f, 32.739f, 55.166f, 32.037f, 53.95f)
                    lineTo(26.618f, 44.564f)
                    close()
                    moveTo(43.67f, 19.177f)
                    horizontalLineTo(51.263f)
                    curveTo(53.672f, 19.177f, 55.626f, 21.13f, 55.626f, 23.539f)
                    verticalLineTo(31.133f)
                    curveTo(55.626f, 33.542f, 53.672f, 35.495f, 51.263f, 35.495f)
                    horizontalLineTo(43.67f)
                    curveTo(41.261f, 35.495f, 39.307f, 33.542f, 39.307f, 31.133f)
                    verticalLineTo(23.539f)
                    curveTo(39.307f, 21.13f, 41.261f, 19.177f, 43.67f, 19.177f)
                    close()
                    moveTo(18.379f, 21.169f)
                    curveTo(21.785f, 17.763f, 27.308f, 17.763f, 30.714f, 21.169f)
                    curveTo(34.12f, 24.575f, 34.12f, 30.097f, 30.714f, 33.503f)
                    curveTo(27.308f, 36.909f, 21.785f, 36.909f, 18.379f, 33.503f)
                    curveTo(14.973f, 30.097f, 14.973f, 24.575f, 18.379f, 21.169f)
                    close()
                    moveTo(29.317f, 22.567f)
                    curveTo(26.764f, 20.014f, 22.677f, 19.935f, 20.028f, 22.327f)
                    lineTo(19.777f, 22.567f)
                    curveTo(17.225f, 25.119f, 17.145f, 29.207f, 19.537f, 31.855f)
                    lineTo(19.777f, 32.106f)
                    curveTo(22.411f, 34.74f, 26.682f, 34.74f, 29.317f, 32.106f)
                    curveTo(31.951f, 29.472f, 31.951f, 25.201f, 29.317f, 22.567f)
                    close()
                    moveTo(43.67f, 21.153f)
                    curveTo(42.352f, 21.153f, 41.284f, 22.221f, 41.283f, 23.539f)
                    verticalLineTo(31.133f)
                    curveTo(41.284f, 32.451f, 42.352f, 33.52f, 43.67f, 33.52f)
                    horizontalLineTo(51.263f)
                    curveTo(52.581f, 33.52f, 53.65f, 32.451f, 53.65f, 31.133f)
                    verticalLineTo(23.539f)
                    curveTo(53.649f, 22.221f, 52.581f, 21.153f, 51.263f, 21.153f)
                    horizontalLineTo(43.67f)
                    close()
                }
                path(
                    fill = SolidColor(Color.Black),
                    fillAlpha = 0.09f,
                    strokeAlpha = 0.09f,
                ) {
                    moveTo(52.95f, 46.882f)
                    curveTo(52.95f, 46.296f, 52.64f, 45.78f, 52.163f, 45.493f)
                    curveTo(50.859f, 44.71f, 49.639f, 43.49f, 48.855f, 42.185f)
                    curveTo(48.569f, 41.709f, 48.053f, 41.398f, 47.466f, 41.398f)
                    curveTo(46.88f, 41.398f, 46.364f, 41.709f, 46.078f, 42.185f)
                    curveTo(45.294f, 43.49f, 44.074f, 44.71f, 42.77f, 45.493f)
                    curveTo(42.293f, 45.78f, 41.982f, 46.296f, 41.982f, 46.882f)
                    curveTo(41.982f, 47.468f, 42.293f, 47.984f, 42.77f, 48.271f)
                    curveTo(44.074f, 49.054f, 45.294f, 50.274f, 46.078f, 51.579f)
                    curveTo(46.364f, 52.055f, 46.88f, 52.366f, 47.466f, 52.366f)
                    curveTo(48.053f, 52.366f, 48.569f, 52.055f, 48.855f, 51.579f)
                    curveTo(49.639f, 50.274f, 50.859f, 49.054f, 52.163f, 48.271f)
                    curveTo(52.64f, 47.984f, 52.95f, 47.468f, 52.95f, 46.882f)
                    close()
                    moveTo(20.655f, 39.851f)
                    curveTo(22.603f, 36.477f, 27.474f, 36.477f, 29.423f, 39.851f)
                    lineTo(34.841f, 49.237f)
                    curveTo(36.79f, 52.612f, 34.354f, 56.83f, 30.458f, 56.83f)
                    horizontalLineTo(19.62f)
                    curveTo(15.723f, 56.83f, 13.287f, 52.612f, 15.236f, 49.237f)
                    lineTo(20.655f, 39.851f)
                    close()
                    moveTo(25.526f, 42.101f)
                    curveTo(25.309f, 41.726f, 24.768f, 41.726f, 24.552f, 42.101f)
                    lineTo(19.132f, 51.487f)
                    curveTo(18.916f, 51.862f, 19.187f, 52.331f, 19.62f, 52.331f)
                    horizontalLineTo(30.458f)
                    curveTo(30.89f, 52.331f, 31.161f, 51.862f, 30.945f, 51.487f)
                    lineTo(25.526f, 42.101f)
                    close()
                    moveTo(17.487f, 17.184f)
                    curveTo(21.386f, 13.285f, 27.707f, 13.285f, 31.606f, 17.184f)
                    curveTo(35.505f, 21.083f, 35.505f, 27.404f, 31.606f, 31.303f)
                    curveTo(27.707f, 35.201f, 21.386f, 35.201f, 17.487f, 31.303f)
                    curveTo(13.588f, 27.404f, 13.588f, 21.083f, 17.487f, 17.184f)
                    close()
                    moveTo(52.388f, 20.447f)
                    curveTo(52.388f, 19.825f, 51.884f, 19.322f, 51.263f, 19.322f)
                    horizontalLineTo(43.67f)
                    curveTo(43.049f, 19.322f, 42.545f, 19.825f, 42.545f, 20.447f)
                    verticalLineTo(28.04f)
                    curveTo(42.545f, 28.661f, 43.049f, 29.165f, 43.67f, 29.165f)
                    horizontalLineTo(51.263f)
                    curveTo(51.884f, 29.165f, 52.388f, 28.661f, 52.388f, 28.04f)
                    verticalLineTo(20.447f)
                    close()
                    moveTo(28.424f, 20.365f)
                    curveTo(26.283f, 18.224f, 22.81f, 18.224f, 20.669f, 20.365f)
                    curveTo(18.527f, 22.507f, 18.527f, 25.979f, 20.669f, 28.121f)
                    curveTo(22.81f, 30.262f, 26.283f, 30.263f, 28.424f, 28.121f)
                    curveTo(30.566f, 25.979f, 30.566f, 22.507f, 28.424f, 20.365f)
                    close()
                    moveTo(57.45f, 46.882f)
                    curveTo(57.45f, 49.113f, 56.255f, 51.062f, 54.48f, 52.128f)
                    curveTo(53.807f, 52.532f, 53.116f, 53.223f, 52.712f, 53.896f)
                    curveTo(51.646f, 55.67f, 49.697f, 56.866f, 47.466f, 56.866f)
                    curveTo(45.236f, 56.866f, 43.286f, 55.67f, 42.221f, 53.896f)
                    curveTo(41.817f, 53.223f, 41.125f, 52.532f, 40.453f, 52.128f)
                    curveTo(38.678f, 51.062f, 37.483f, 49.113f, 37.483f, 46.882f)
                    curveTo(37.483f, 44.651f, 38.678f, 42.702f, 40.453f, 41.636f)
                    curveTo(41.125f, 41.232f, 41.817f, 40.541f, 42.221f, 39.868f)
                    curveTo(43.286f, 38.094f, 45.236f, 36.898f, 47.466f, 36.898f)
                    curveTo(49.697f, 36.898f, 51.646f, 38.094f, 52.712f, 39.868f)
                    curveTo(53.116f, 40.541f, 53.807f, 41.232f, 54.48f, 41.636f)
                    curveTo(56.255f, 42.702f, 57.45f, 44.651f, 57.45f, 46.882f)
                    close()
                    moveTo(56.888f, 28.04f)
                    curveTo(56.888f, 31.146f, 54.369f, 33.664f, 51.263f, 33.664f)
                    horizontalLineTo(43.67f)
                    curveTo(40.564f, 33.664f, 38.045f, 31.146f, 38.045f, 28.04f)
                    verticalLineTo(20.447f)
                    curveTo(38.045f, 17.34f, 40.564f, 14.822f, 43.67f, 14.822f)
                    horizontalLineTo(51.263f)
                    curveTo(54.369f, 14.822f, 56.888f, 17.34f, 56.888f, 20.447f)
                    verticalLineTo(28.04f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4.49963f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(19.078f, 17.79f)
                    curveTo(16.058f, 20.81f, 16.058f, 25.707f, 19.078f, 28.727f)
                    curveTo(22.098f, 31.747f, 26.995f, 31.747f, 30.015f, 28.727f)
                    curveTo(33.035f, 25.707f, 33.035f, 20.81f, 30.015f, 17.79f)
                    curveTo(26.995f, 14.77f, 22.098f, 14.77f, 19.078f, 17.79f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4.49963f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(40.295f, 19.462f)
                    curveTo(40.295f, 17.598f, 41.806f, 16.087f, 43.67f, 16.087f)
                    horizontalLineTo(51.263f)
                    curveTo(53.127f, 16.087f, 54.638f, 17.598f, 54.638f, 19.462f)
                    verticalLineTo(27.055f)
                    curveTo(54.638f, 28.919f, 53.127f, 30.43f, 51.263f, 30.43f)
                    horizontalLineTo(43.67f)
                    curveTo(41.806f, 30.43f, 40.295f, 28.919f, 40.295f, 27.055f)
                    verticalLineTo(19.462f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4.49963f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(22.603f, 39.992f)
                    curveTo(23.685f, 38.117f, 26.392f, 38.117f, 27.474f, 39.992f)
                    lineTo(32.893f, 49.378f)
                    curveTo(33.976f, 51.252f, 32.622f, 53.596f, 30.458f, 53.596f)
                    horizontalLineTo(19.62f)
                    curveTo(17.455f, 53.596f, 16.102f, 51.252f, 17.184f, 49.378f)
                    lineTo(22.603f, 39.992f)
                    close()
                }
                path(
                    stroke = SolidColor(Color.White),
                    strokeLineWidth = 4.49963f,
                    strokeLineCap = StrokeCap.Round,
                    strokeLineJoin = StrokeJoin.Round,
                ) {
                    moveTo(44.149f, 51.752f)
                    curveTo(44.825f, 52.878f, 46.058f, 53.631f, 47.466f, 53.631f)
                    curveTo(48.875f, 53.631f, 50.108f, 52.878f, 50.784f, 51.752f)
                    curveTo(51.377f, 50.764f, 52.333f, 49.808f, 53.322f, 49.214f)
                    curveTo(54.447f, 48.538f, 55.2f, 47.306f, 55.2f, 45.897f)
                    curveTo(55.2f, 44.488f, 54.447f, 43.256f, 53.322f, 42.58f)
                    curveTo(52.333f, 41.986f, 51.377f, 41.031f, 50.784f, 40.042f)
                    curveTo(50.108f, 38.917f, 48.875f, 38.163f, 47.466f, 38.163f)
                    curveTo(46.058f, 38.163f, 44.825f, 38.917f, 44.149f, 40.042f)
                    curveTo(43.555f, 41.031f, 42.6f, 41.986f, 41.611f, 42.58f)
                    curveTo(40.486f, 43.256f, 39.733f, 44.488f, 39.733f, 45.897f)
                    curveTo(39.733f, 47.306f, 40.486f, 48.538f, 41.611f, 49.214f)
                    curveTo(42.6f, 49.808f, 43.555f, 50.764f, 44.149f, 51.752f)
                    close()
                }
                path(
                    fill = Brush.radialGradient(
                        colorStops = arrayOf(
                            0f to Color.White.copy(alpha = 0.078431375f),
                            1f to Color.White.copy(alpha = 0f),
                        ),
                        center = Offset(0f, 0f),
                        radius = 101.823f,
                    ),
                ) {
                    moveTo(-18f, -18f)
                    horizontalLineToRelative(108f)
                    verticalLineToRelative(108f)
                    horizontalLineToRelative(-108f)
                    close()
                }
            }
        }.build()

        return _AppIcon!!
    }

@Suppress("ObjectPropertyName")
private var _AppIcon: ImageVector? = null
