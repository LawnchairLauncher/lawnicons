/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package app.lawnchair.lawnicons.ui.components.core.placeholder

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.drawOutline
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.node.invalidateDraw
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.LayoutDirection
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Contains default values used by [Modifier.placeholder] and [PlaceholderHighlight].
 */
object PlaceholderDefaults {
    /**
     * The default [InfiniteRepeatableSpec] to use for [fade].
     */
    val fadeAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(delayMillis = 200, durationMillis = 600),
            repeatMode = RepeatMode.Reverse,
        )
    }

    /**
     * The default [InfiniteRepeatableSpec] to use for [shimmer].
     */
    val shimmerAnimationSpec: InfiniteRepeatableSpec<Float> by lazy {
        infiniteRepeatable(
            animation = tween(durationMillis = 1700, delayMillis = 200),
            repeatMode = RepeatMode.Restart,
        )
    }

    /**
     * Returns the value used as the `color` parameter value on [Modifier.placeholder].
     *
     * @param backgroundColor The current background color of the layout. Defaults to
     * `MaterialTheme.colors.surface`.
     * @param contentColor The content color to be used on top of [backgroundColor].
     * @param contentAlpha The alpha component to set on [contentColor] when compositing the color
     * on top of [backgroundColor]. Defaults to `0.1f`.
     */
    @Composable
    fun color(
        backgroundColor: Color = MaterialTheme.colorScheme.surface,
        contentColor: Color = contentColorFor(backgroundColor),
        contentAlpha: Float = 0.1f,
    ): Color = contentColor.copy(contentAlpha).compositeOver(backgroundColor)
}

/**
 * Draws some skeleton UI which is typically used whilst content is 'loading'.
 *
 * A version of this modifier which uses appropriate values for Material themed apps is available
 * in the 'Placeholder Material' library.
 *
 * You can provide a [PlaceholderHighlight] which runs an highlight animation on the placeholder.
 * The [shimmer] and [fade] implementations are provided for easy usage.
 *
 * A cross-fade transition will be applied to the content and placeholder UI when the [visible]
 * value changes. The transition can be customized via the [contentFadeTransitionSpec] and
 * [placeholderFadeTransitionSpec] parameters.
 *
 * You can find more information on the pattern at the Material Theming
 * [Placeholder UI](https://material.io/design/communication/launch-screen.html#placeholder-ui)
 * guidelines.
 *
 * @param visible whether the placeholder should be visible or not.
 * @param color the color used to draw the placeholder UI.
 * @param shape desired shape of the placeholder. Defaults to [RectangleShape].
 * @param highlight optional highlight animation.
 * @param placeholderFadeTransitionSpec The transition spec to use when fading the placeholder
 * on/off screen. The boolean parameter defined for the transition is [visible].
 * @param contentFadeTransitionSpec The transition spec to use when fading the content
 * on/off screen. The boolean parameter defined for the transition is [visible].
 */
@Composable
fun Modifier.placeholder(
    visible: Boolean,
    color: Color = PlaceholderDefaults.color(),
    shape: Shape = MaterialTheme.shapes.small,
    highlight: PlaceholderHighlight? = null,
    placeholderFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
    contentFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float> = { spring() },
): Modifier = this then PlaceholderElement(
    visible = visible,
    color = color,
    shape = shape,
    highlight = highlight,
    placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
    contentFadeTransitionSpec = contentFadeTransitionSpec,
)

private data class PlaceholderElement(
    private val visible: Boolean,
    private val color: Color,
    private val shape: Shape,
    private val highlight: PlaceholderHighlight?,
    private val placeholderFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
    private val contentFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
) : ModifierNodeElement<PlaceholderNode>() {
    override fun create(): PlaceholderNode = PlaceholderNode(
        visible = visible,
        color = color,
        shape = shape,
        highlight = highlight,
        placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
        contentFadeTransitionSpec = contentFadeTransitionSpec,
    )

    override fun update(node: PlaceholderNode) {
        node.update(
            visible = visible,
            color = color,
            shape = shape,
            highlight = highlight,
            placeholderFadeTransitionSpec = placeholderFadeTransitionSpec,
            contentFadeTransitionSpec = contentFadeTransitionSpec,
        )
    }

    override fun InspectorInfo.inspectableProperties() {
        name = "placeholder"
        value = visible
        properties["visible"] = visible
        properties["color"] = color
        properties["highlight"] = highlight
        properties["shape"] = shape
    }
}

private class PlaceholderNode(
    private var visible: Boolean,
    private var color: Color,
    private var shape: Shape,
    private var highlight: PlaceholderHighlight?,
    private var placeholderFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
    private var contentFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
) : Modifier.Node(),
    DrawModifierNode {
    // Values used for caching purposes
    private var lastSize: Size? = null
    private var lastLayoutDirection: LayoutDirection? = null
    private var lastOutline: Outline? = null

    // The current highlight animation progress
    private val highlightProgress = Animatable(0f)

    private val placeholderAlpha = Animatable(if (visible) 1f else 0f)
    private val contentAlpha = Animatable(if (visible) 0f else 1f)

    private val paint = Paint()

    fun update(
        visible: Boolean,
        color: Color,
        shape: Shape,
        highlight: PlaceholderHighlight?,
        placeholderFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
        contentFadeTransitionSpec: Transition.Segment<Boolean>.() -> FiniteAnimationSpec<Float>,
    ) {
        val visibleChanged = this.visible != visible
        this.visible = visible
        this.color = color
        this.shape = shape
        val highlightChanged = this.highlight != highlight
        this.highlight = highlight
        this.placeholderFadeTransitionSpec = placeholderFadeTransitionSpec
        this.contentFadeTransitionSpec = contentFadeTransitionSpec

        if (visibleChanged && isAttached) {
            launchAnimation()
        }
        if (highlightChanged && isAttached) {
            launchHighlightAnimation()
        }
        invalidateDraw()
    }

    override fun onAttach() {
        launchAnimation()
        launchHighlightAnimation()
    }

    // This is our crossfade transition
    private var animationJob: Job? = null
    private fun launchAnimation() {
        animationJob?.cancel()
        animationJob = coroutineScope.launch {
            val segment = object : Transition.Segment<Boolean> {
                override val initialState: Boolean = !visible
                override val targetState: Boolean = visible
            }
            val pSpec = placeholderFadeTransitionSpec(segment)
            val cSpec = contentFadeTransitionSpec(segment)

            launch {
                placeholderAlpha.animateTo(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = pSpec,
                )
            }
            launch {
                contentAlpha.animateTo(
                    targetValue = if (visible) 0f else 1f,
                    animationSpec = cSpec,
                )
            }
        }
    }

    private var highlightJob: Job? = null
    private fun launchHighlightAnimation() {
        highlightJob?.cancel()

        // Run the optional animation spec and update the progress if the placeholder is visible
        val spec = highlight?.animationSpec
        if (spec != null) {
            highlightJob = coroutineScope.launch {
                highlightProgress.snapTo(0f)
                highlightProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = spec,
                )
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val pAlpha = placeholderAlpha.value
        val cAlpha = contentAlpha.value

        // Draw the composable content first
        if (cAlpha in 0.01f..0.99f) {
            // If the content alpha is between 1% and 99%, draw it in a layer with
            // the alpha applied
            paint.alpha = cAlpha
            withLayer(paint) {
                drawContent()
            }
        } else if (cAlpha >= 0.99f) {
            // If the content alpha is > 99%, draw it with no alpha
            drawContent()
        }

        if (pAlpha in 0.01f..0.99f) {
            // If the placeholder alpha is between 1% and 99%, draw it in a layer with
            // the alpha applied
            paint.alpha = pAlpha
            withLayer(paint) {
                lastOutline = drawPlaceholder(
                    shape = shape,
                    color = color,
                    highlight = highlight,
                    progress = highlightProgress.value,
                    lastOutline = lastOutline,
                    lastLayoutDirection = lastLayoutDirection,
                    lastSize = lastSize,
                )
            }
        } else if (pAlpha >= 0.99f) {
            // If the placeholder alpha is > 99%, draw it with no alpha
            lastOutline = drawPlaceholder(
                shape = shape,
                color = color,
                highlight = highlight,
                progress = highlightProgress.value,
                lastOutline = lastOutline,
                lastLayoutDirection = lastLayoutDirection,
                lastSize = lastSize,
            )
        }

        // Keep track of the last size & layout direction
        lastSize = size
        lastLayoutDirection = layoutDirection
    }

    private fun DrawScope.drawPlaceholder(
        shape: Shape,
        color: Color,
        highlight: PlaceholderHighlight?,
        progress: Float,
        lastOutline: Outline?,
        lastLayoutDirection: LayoutDirection?,
        lastSize: Size?,
    ): Outline? {
        // shortcut to avoid Outline calculation and allocation
        if (shape === RectangleShape) {
            // Draw the initial background color
            drawRect(color = color)

            if (highlight != null) {
                drawRect(
                    brush = highlight.brush(progress, size),
                    alpha = highlight.alpha(progress),
                )
            }
            // We didn't create an outline so return null
            return null
        }

        // Otherwise we need to create an outline from the shape
        val outline = lastOutline.takeIf {
            size == lastSize && layoutDirection == lastLayoutDirection
        } ?: shape.createOutline(size, layoutDirection, this)

        // Draw the placeholder color
        drawOutline(outline = outline, color = color)

        if (highlight != null) {
            drawOutline(
                outline = outline,
                brush = highlight.brush(progress, size),
                alpha = highlight.alpha(progress),
            )
        }

        // Return the outline we used
        return outline
    }

    private inline fun ContentDrawScope.withLayer(
        paint: Paint,
        drawBlock: ContentDrawScope.() -> Unit,
    ) = drawIntoCanvas { canvas ->
        canvas.saveLayer(size.toRect(), paint)
        drawBlock()
        canvas.restore()
    }
}
