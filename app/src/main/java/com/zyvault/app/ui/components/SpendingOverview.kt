package com.zyvault.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvault.app.ui.theme.*

data class SpendingCategory(
    val name: String,
    val percentage: Int,
    val color: Color
)

@Composable
fun SpendingOverviewCard(modifier: Modifier = Modifier, totalSpent: Double = 0.0) {
    val categories = if (totalSpent > 0) listOf(
        SpendingCategory("Housing", 35, Color(0xFFFF8F00)), // ZyvaultOrange
        SpendingCategory("Food", 25, Color(0xFFFFCC00)),    // ZyvaultWarning
        SpendingCategory("Transport", 20, Color(0xFF0A84FF)), // ZyvaultInfo
        SpendingCategory("Insurance", 15, Color(0xFF34C759)), // ZyvaultSuccess
        SpendingCategory("Other", 5, Color(0xFF8E8E93))      // ZyvaultMuted
    ) else emptyList()

    if (totalSpent <= 0) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "No spending data available",
                style = ZyvaultType.bodyMedium,
                color = ZyvaultMuted
            )
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
            .padding(vertical = 24.dp, horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Donut Chart
            Box(
                modifier = Modifier.size(150.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(110.dp)) {
                    var startAngle = -90f
                    categories.forEach { category ->
                        val sweepAngle = (category.percentage / 100f) * 360f
                        drawArc(
                            color = category.color,
                            startAngle = startAngle,
                            sweepAngle = sweepAngle - 3f, // Gap for a modern look
                            useCenter = false,
                            style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                        )
                        startAngle += sweepAngle
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$${String.format(java.util.Locale.US, "%,.0f", totalSpent)}",
                        style = ZyvaultType.titleLarge.copy(fontSize = 20.sp),
                        color = ZyvaultWhite
                    )
                    Text(
                        text = "Total Spent",
                        style = ZyvaultType.nano,
                        color = ZyvaultMuted
                    )
                }
            }

            // Legend
            Column(
                modifier = Modifier.padding(start = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                categories.forEach { category ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(category.color)
                        )
                        Text(
                            text = category.name,
                            style = ZyvaultType.bodySmall,
                            color = ZyvaultMuted,
                            modifier = Modifier.width(80.dp)
                        )
                        Text(
                            text = "${category.percentage}%",
                            style = ZyvaultType.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = ZyvaultWhite
                            )
                        )
                    }
                }
            }
        }
    }
}
