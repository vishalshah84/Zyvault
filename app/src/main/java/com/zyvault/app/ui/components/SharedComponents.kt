package com.zyvault.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.theme.ZyvaultBlack
import com.zyvault.app.ui.theme.ZyvaultBorder
import com.zyvault.app.ui.theme.ZyvaultCard
import com.zyvault.app.ui.theme.ZyvaultDim
import com.zyvault.app.ui.theme.ZyvaultMuted
import com.zyvault.app.ui.theme.ZyvaultOrange
import com.zyvault.app.ui.theme.ZyvaultWhite
import com.zyvault.app.ui.theme.Spacing
import com.zyvault.app.ui.theme.ZyvaultType

@Composable
fun BrandText(size: TextUnit = 20.sp) {
    Text(
        text = buildAnnotatedString {
            withStyle(SpanStyle(color = ZyvaultWhite, fontWeight = FontWeight.Bold)) {
                append("Zy")
            }
            withStyle(SpanStyle(color = ZyvaultOrange, fontWeight = FontWeight.Bold)) {
                append("vault")
            }
        },
        fontSize = size,
        letterSpacing = (-0.5).sp
    )
}

/**
 * Summary stat card with consistent sizing.
 */
@Composable
fun SummaryCard(
    label: String,
    value: String,
    valueColor: Color = ZyvaultWhite,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
            .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
            .padding(horizontal = Spacing.cardPadding, vertical = 14.dp)
    ) {
        Text(
            text = label.uppercase(),
            style = ZyvaultType.micro,
            color = ZyvaultMuted
        )
        Spacer(modifier = Modifier.height(Spacing.tinyGap))
        Text(
            text = value,
            style = ZyvaultType.heroMedium,
            color = valueColor
        )
    }
}

/**
 * Status badge pill.
 */
@Composable
fun StatusBadge(text: String, color: Color) {
    Text(
        text = text,
        style = ZyvaultType.micro.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp),
        color = color,
        modifier = Modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/**
 * Section label.
 */
@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text.uppercase(),
        style = ZyvaultType.micro,
        color = ZyvaultDim,
        modifier = modifier.padding(horizontal = Spacing.screenPadding, vertical = 8.dp)
    )
}

/**
 * Tap-scale wrapper — gives any composable a subtle press animation.
 */
@Composable
fun TapScale(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "tapScale"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
    ) {
        content()
    }
}

/**
 * Consistent top bar used on every screen.
 */
@Composable
fun ZyvaultTopBar(
    trailingContent: @Composable () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.topBarHeight)
            .padding(horizontal = Spacing.screenPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            ZyvaultLogo(size = 32.dp)
            BrandText(size = 18.sp)
        }
        trailingContent()
    }
}

/**
 * Option row for adding documents/files.
 */
@Composable
fun AddDocOption(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZyvaultBlack.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .border(0.5.dp, ZyvaultBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ZyvaultOrange.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ZyvaultOrange, modifier = Modifier.size(20.dp))
        }
        Column {
            Text(title, style = ZyvaultType.bodyLarge, color = ZyvaultWhite)
            Text(subtitle, style = ZyvaultType.caption, color = ZyvaultMuted)
        }
    }
}

/**
 * Empty state placeholder.
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = ZyvaultDim
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = ZyvaultType.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = ZyvaultMuted,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            style = ZyvaultType.bodySmall,
            color = ZyvaultDim,
            textAlign = TextAlign.Center
        )
    }
}
@Composable
fun AddButton(onClick: () -> Unit = {}) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 800f),
        label = "addBtnScale"
    )

    Box(
        modifier = Modifier
            .size(40.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(ZyvaultOrange)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text("+", fontSize = 24.sp, color = ZyvaultWhite, fontWeight = FontWeight.Light)
    }
}
