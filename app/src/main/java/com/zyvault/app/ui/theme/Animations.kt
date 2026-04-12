package com.zyvault.app.ui.theme

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer

/**
 * Slide-up + fade-in entrance for list items.
 * Call with the item's index for stagger.
 */
@Composable
fun Modifier.slideUpEntrance(
    index: Int,
    baseDelay: Int = 30,
    duration: Int = 400
): Modifier {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay((index * baseDelay).toLong())
        visible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = duration, easing = EaseOutCubic),
        label = "alpha"
    )
    val animatedOffset by animateFloatAsState(
        targetValue = if (visible) 0f else 40f,
        animationSpec = tween(durationMillis = duration, easing = EaseOutCubic),
        label = "offset"
    )

    return this
        .graphicsLayer {
            alpha = animatedAlpha
            translationY = animatedOffset
        }
}

/**
 * Scale + fade entrance for hero elements (logo, avatar, balance).
 */
@Composable
fun Modifier.scaleEntrance(
    delay: Int = 0,
    duration: Int = 500
): Modifier {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = duration, easing = EaseOutCubic),
        label = "alpha"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        animationSpec = tween(durationMillis = duration, easing = EaseOutCubic),
        label = "scale"
    )

    return this
        .graphicsLayer {
            alpha = animatedAlpha
            scaleX = animatedScale
            scaleY = animatedScale
        }
}

/**
 * Fade-in only entrance (for labels, subtle elements).
 */
@Composable
fun Modifier.fadeEntrance(
    delay: Int = 0,
    duration: Int = 350
): Modifier {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delay.toLong())
        visible = true
    }

    val animatedAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = duration, easing = EaseOutCubic),
        label = "alpha"
    )

    return this.graphicsLayer { alpha = animatedAlpha }
}

/**
 * Easing curves
 */
val EaseOutCubic = CubicBezierEasing(0.33f, 1f, 0.68f, 1f)
val EaseOutBack = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
