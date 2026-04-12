package com.zyvault.app.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Zyvault Typography Scale
 *
 * Consistent sizing hierarchy used across all screens:
 * - heroLarge:   36-38sp — balance totals, big numbers
 * - heroMedium:  22-24sp — stat values, card values
 * - titleLarge:  22sp    — user name, section titles
 * - titleMedium: 18sp    — brand text, sub-headers
 * - bodyLarge:   15sp    — bill names, primary content
 * - bodyMedium:  14sp    — doc names, settings labels
 * - bodySmall:   13sp    — status text, descriptions, alerts
 * - caption:     12sp    — subtitles, due dates, secondary info
 * - micro:       11sp    — section labels, tags, badges
 * - nano:        10sp    — metric labels, smallest text
 */
object ZyvaultType {

    val heroLarge = TextStyle(
        fontSize = 38.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    )

    val heroMedium = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    )

    val titleLarge = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.3).sp
    )

    val titleMedium = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = (-0.5).sp
    )

    val bodyLarge = TextStyle(
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold
    )

    val bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium
    )

    val bodySmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal
    )

    val caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal
    )

    val micro = TextStyle(
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 1.2.sp
    )

    val nano = TextStyle(
        fontSize = 10.sp,
        fontWeight = FontWeight.Normal
    )

    val buttonLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )

    val buttonSmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold
    )
}
