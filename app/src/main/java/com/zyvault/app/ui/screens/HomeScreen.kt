package com.zyvault.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.HomeViewModel
import java.util.Calendar
import java.util.Locale

data class ActivityItem(
    val icon: ImageVector,
    val iconBg: Color,
    val iconTint: Color,
    val name: String,
    val subtitle: String,
    val status: String,
    val statusColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onInsuranceClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val userData by viewModel.userData.collectAsState()
    val userName = userData?.name ?: "User"

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greeting = when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        else -> "Good evening"
    }

    var selectedStat by remember { mutableStateOf<String?>(null) }

    val stats = listOf(
        Triple("Docs", (userData?.documentCount ?: 0).toString(), ZyvaultWhite),
        Triple("Banks", (userData?.bankAccountCount ?: 0).toString(), ZyvaultWhite),
        Triple("Bills Due", (userData?.billsDueCount ?: 0).toString(), ZyvaultOrange),
        Triple("Saved", "$${String.format(Locale.US, "%,.0f", userData?.totalSaved ?: 0.0)}", ZyvaultSuccess),
    )

    val activities = emptyList<ActivityItem>()

    val sheetState = rememberModalBottomSheetState()
    if (selectedStat != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedStat = null },
            sheetState = sheetState,
            containerColor = ZyvaultBlack,
            dragHandle = { BottomSheetDefaults.DragHandle(color = ZyvaultMuted) }
        ) {
            StatDetailView(selectedStat!!)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top bar
        item {
            ZyvaultTopBar {
                Box(
                    modifier = Modifier
                        .fadeEntrance(delay = 200)
                        .clickable { onNotificationsClick() }
                        .padding(4.dp)
                ) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = "Notifications",
                        tint = ZyvaultWhite,
                        modifier = Modifier.size(24.dp)
                    )
                    if ((userData?.notificationCount ?: 0) > 0) {
                        Box(
                            modifier = Modifier
                                .size(9.dp)
                                .clip(CircleShape)
                                .background(ZyvaultOrange)
                                .align(Alignment.TopEnd)
                                .offset(x = 1.dp, y = (-1).dp)
                        )
                    }
                }
            }
        }

        // Greeting
        item {
            Column(
                modifier = Modifier
                    .padding(horizontal = Spacing.screenPadding)
                    .fadeEntrance(delay = 100)
            ) {
                Text(
                    text = greeting,
                    style = ZyvaultType.bodySmall,
                    color = ZyvaultMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = userName,
                    style = ZyvaultType.titleLarge,
                    color = ZyvaultWhite
                )
            }
        }

        // Stats row
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.screenPadding, vertical = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(stats.size) { index ->
                    val (label, value, color) = stats[index]
                    Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 50)) {
                        TapScale(onClick = { selectedStat = label }) {
                            SummaryCard(
                                label = label,
                                value = value,
                                valueColor = color,
                                modifier = Modifier
                                    .width(120.dp)
                            )
                        }
                    }
                }
            }
        }

        // Alert banner
        /*
        item {
            TapScale(
                onClick = onInsuranceClick,
                modifier = Modifier.slideUpEntrance(4, baseDelay = 50)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .background(ZyvaultOrange.copy(alpha = 0.12f), RoundedCornerShape(Spacing.cardRadius))
                        .padding(Spacing.cardPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .width(Spacing.statusBarWidth)
                            .height(36.dp)
                            .background(ZyvaultOrange, RoundedCornerShape(2.dp))
                    )
                    Spacer(modifier = Modifier.width(Spacing.innerGap))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Car insurance expires in 12 days",
                            style = ZyvaultType.bodyMedium,
                            color = ZyvaultWhite
                        )
                        Text(
                            text = "Compare plans and save \$142/mo",
                            style = ZyvaultType.caption,
                            color = ZyvaultMuted
                        )
                    }
                    Icon(
                        Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = ZyvaultMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
        */

    if (activities.isNotEmpty()) {
        // Recent label
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            SectionLabel("Recent Activity", modifier = Modifier.fadeEntrance(delay = 250))
        }

        // Activity rows
        itemsIndexed(activities) { index, activity ->
            Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 60)) {
                TapScale {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenPadding, vertical = Spacing.tinyGap)
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(Spacing.innerGap)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Spacing.iconSize)
                                .background(activity.iconBg, RoundedCornerShape(Spacing.cardRadius)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                activity.icon,
                                contentDescription = null,
                                tint = activity.iconTint,
                                modifier = Modifier.size(Spacing.iconSizeSmall)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = activity.name,
                                style = ZyvaultType.bodyMedium,
                                color = ZyvaultWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = activity.subtitle,
                                style = ZyvaultType.caption,
                                color = ZyvaultMuted
                            )
                        }
                        Text(
                            text = activity.status,
                            style = ZyvaultType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                            color = activity.statusColor
                        )
                    }
                }
            }
        }
    } else {
        item {
            EmptyState(
                icon = Icons.Outlined.History,
                title = "No recent activity",
                description = "Your recent transactions and document updates will appear here."
            )
        }
    }
    }
}

@Composable
fun StatDetailView(label: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = label,
            style = ZyvaultType.titleLarge,
            color = ZyvaultWhite
        )
        Spacer(modifier = Modifier.height(20.dp))

        when (label) {
            "Docs" -> {
                EmptyState(
                    icon = Icons.Outlined.Description,
                    title = "No documents yet",
                    description = "Upload your first document to see it here."
                )
            }
            "Banks" -> {
                EmptyState(
                    icon = Icons.Outlined.AccountBalance,
                    title = "No accounts linked",
                    description = "Link your bank accounts in the Finance tab."
                )
            }
            "Bills Due" -> {
                EmptyState(
                    icon = Icons.Outlined.FlashOn,
                    title = "No bills due",
                    description = "Add bills in the Bills tab to track them."
                )
            }
            "Saved" -> {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Money Added", color = ZyvaultMuted)
                        Text("$0.00", color = ZyvaultWhite, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Money Spent", color = ZyvaultMuted)
                        Text("$0.00", color = ZyvaultDanger, fontWeight = FontWeight.Bold)
                    }
                    HorizontalDivider(color = ZyvaultBorder)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Remaining (Saved)", color = ZyvaultOrange, fontWeight = FontWeight.Bold)
                        Text("$0.00", color = ZyvaultSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(title: String, value: String, icon: ImageVector, subtitle: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZyvaultCard, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(ZyvaultOrange.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = ZyvaultOrange, modifier = Modifier.size(18.dp))
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = ZyvaultType.bodyMedium, color = ZyvaultWhite)
            if (subtitle != null) {
                Text(subtitle, style = ZyvaultType.nano, color = ZyvaultOrange)
            }
        }
        Text(value, style = ZyvaultType.bodySmall, color = ZyvaultWhite, fontWeight = FontWeight.Bold)
    }
}
