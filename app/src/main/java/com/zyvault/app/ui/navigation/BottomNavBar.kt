package com.zyvault.app.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvault.app.ui.theme.*

enum class Tab(
    val label: String,
    val outlinedIcon: ImageVector,
    val filledIcon: ImageVector
) {
    Home("Home", Icons.Outlined.Home, Icons.Filled.Home),
    Vault("Vault", Icons.Outlined.VerifiedUser, Icons.Filled.VerifiedUser),
    Finance("Finance", Icons.Outlined.AccountBalance, Icons.Filled.AccountBalance),
    Insurance("Insurance", Icons.Outlined.Shield, Icons.Filled.Shield),
    Bills("Bills", Icons.AutoMirrored.Outlined.ReceiptLong, Icons.AutoMirrored.Filled.ReceiptLong),
    Profile("Profile", Icons.Outlined.Person, Icons.Filled.Person)
}

@Composable
fun ZyvaultBottomBar(
    currentTab: Tab,
    onTabSelected: (Tab) -> Unit
) {
    Column {
        // Top border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Spacing.dividerHeight)
                .background(ZyvaultBorder)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .background(ZyvaultNav)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Tab.entries.forEach { tab ->
                val isActive = currentTab == tab
                val interactionSource = remember { MutableInteractionSource() }

                val animatedColor by animateColorAsState(
                    targetValue = if (isActive) ZyvaultOrange else ZyvaultWhite.copy(alpha = 0.35f),
                    animationSpec = tween(200),
                    label = "tabColor"
                )

                Column(
                    modifier = Modifier
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Orange dot indicator
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(ZyvaultOrange)
                        )
                    } else {
                        Spacer(modifier = Modifier.height(5.dp))
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Box {
                        Icon(
                            imageVector = if (isActive) tab.filledIcon else tab.outlinedIcon,
                            contentDescription = tab.label,
                            tint = animatedColor,
                            modifier = Modifier.size(22.dp)
                        )

                        /*
                        // Notification badge for Bills
                        if (tab == Tab.Bills) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 8.dp, y = (-4).dp)
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(ZyvaultDanger),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "3",
                                    color = ZyvaultWhite,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        */
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = tab.label,
                        color = animatedColor,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
