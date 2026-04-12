package com.zyvault.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.zyvault.app.ui.theme.ZyvaultOrange
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ZyvaultLogo(size: Dp = 40.dp) {
    Canvas(modifier = Modifier.size(size)) {
        val width = size.toPx()
        val centerX = width / 2f
        val centerY = width / 2f
        
        // 1. Core orange circle
        val coreRadius = width * 0.16f
        drawCircle(
            color = ZyvaultOrange,
            radius = coreRadius,
            center = Offset(centerX, centerY)
        )
        
        // 2. Smaller white circle inside
        val whiteRadius = width * 0.07f
        drawCircle(
            color = Color.White,
            radius = whiteRadius,
            center = Offset(centerX, centerY)
        )
        
        // 3. Rays
        // Cardinal rays: Orange, Long, Rounded
        val cardinalStart = coreRadius + (width * 0.04f)
        val cardinalEnd = width * 0.48f
        val cardinalStroke = width * 0.07f
        
        // Diagonal rays: Gray (50% opacity), Shorter, Rounded
        val diagonalStart = coreRadius + (width * 0.04f)
        val diagonalEnd = width * 0.32f
        val diagonalStroke = width * 0.05f
        val diagonalColor = com.zyvault.app.ui.theme.ZyvaultMuted.copy(alpha = 0.5f)

        for (i in 0 until 8) {
            val isCardinal = i % 2 == 0
            val angleDeg = i * 45f - 90f // Start from top
            val angleRad = Math.toRadians(angleDeg.toDouble()).toFloat()
            
            val startDist = if (isCardinal) cardinalStart else diagonalStart
            val endDist = if (isCardinal) cardinalEnd else diagonalEnd
            val strokeWidth = if (isCardinal) cardinalStroke else diagonalStroke
            val color = if (isCardinal) ZyvaultOrange else diagonalColor
            
            val startX = centerX + cos(angleRad) * startDist
            val startY = centerY + sin(angleRad) * startDist
            val endX = centerX + cos(angleRad) * endDist
            val endY = centerY + sin(angleRad) * endDist
            
            drawLine(
                color = color,
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}
