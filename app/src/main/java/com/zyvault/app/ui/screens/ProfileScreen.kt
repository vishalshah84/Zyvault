package com.zyvault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.HomeViewModel
import java.util.Locale

import com.zyvault.app.ui.viewmodel.FinanceViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    onUpgradeClick: () -> Unit = {},
    onVerifyClick: () -> Unit = {},
    homeViewModel: HomeViewModel = viewModel(),
    financeViewModel: FinanceViewModel = viewModel()
) {
    val userData by homeViewModel.userData.collectAsState()
    var biometric by remember { mutableStateOf(true) }
    var expiryReminders by remember { mutableStateOf(true) }
    var insuranceAlerts by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack),
        contentPadding = PaddingValues(bottom = 100.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top bar
        item {
            ZyvaultTopBar()
        }

        // Avatar section with scale entrance
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Box(modifier = Modifier.scaleEntrance(delay = 0)) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(ZyvaultOrange)
                        .clickable { onVerifyClick() },
                    contentAlignment = Alignment.Center
                ) {
                    val initials = userData?.name?.split(" ")?.let { names ->
                        if (names.size >= 2) {
                            "${names[0].firstOrNull() ?: ""}${names[1].firstOrNull() ?: ""}"
                        } else {
                            names.firstOrNull()?.take(2) ?: "ZV"
                        }
                    }?.uppercase() ?: "ZV"

                    Text(
                        initials,
                        color = ZyvaultWhite,
                        style = ZyvaultType.heroLarge.copy(fontSize = 34.sp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.fadeEntrance(delay = 150)) {
                Text(
                    userData?.name ?: user?.email ?: "Guest User",
                    style = ZyvaultType.titleLarge,
                    color = ZyvaultWhite
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Box(modifier = Modifier.fadeEntrance(delay = 250)) {
                Text(
                    text = "${userData?.plan ?: "Personal"} Plan",
                    style = ZyvaultType.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    color = ZyvaultOrange,
                    modifier = Modifier
                        .border(1.5.dp, ZyvaultOrange, RoundedCornerShape(Spacing.buttonRadiusFull))
                        .clickable { onUpgradeClick() }
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                )
            }
        }

        // Stats — unified card with dividers
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            Box(modifier = Modifier.slideUpEntrance(0)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                        .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                        .padding(vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatColumn((userData?.documentCount ?: 0).toString(), "Documents", ZyvaultWhite, Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .width(Spacing.dividerHeight)
                            .height(36.dp)
                            .background(ZyvaultBorder)
                    )
                    StatColumn((userData?.bankAccountCount ?: 0).toString(), "Accounts", ZyvaultWhite, Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .width(Spacing.dividerHeight)
                            .height(36.dp)
                            .background(ZyvaultBorder)
                    )
                    StatColumn("$${String.format(Locale.US, "%,.0f", userData?.totalSaved ?: 0.0)}", "Saved", ZyvaultSuccess, Modifier.weight(1f))
                }
            }
        }

        // Security section
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            SectionLabel("Security", modifier = Modifier.fadeEntrance(delay = 200))
        }

        item {
            Box(modifier = Modifier.slideUpEntrance(1, baseDelay = 60)) {
                TapScale {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenPadding)
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                            .padding(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Spacing.iconSize)
                                .background(
                                    ZyvaultOrange.copy(alpha = 0.15f),
                                    RoundedCornerShape(Spacing.cardRadius)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = ZyvaultOrange,
                                modifier = Modifier.size(Spacing.iconSizeSmall)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Zero-knowledge encryption",
                                style = ZyvaultType.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                color = ZyvaultWhite
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "Even Zyvault cannot read your documents",
                                style = ZyvaultType.caption,
                                color = ZyvaultMuted
                            )
                        }
                        Icon(
                            Icons.Outlined.CheckCircle,
                            contentDescription = null,
                            tint = ZyvaultSuccess,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Preferences section
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            SectionLabel("Preferences", modifier = Modifier.fadeEntrance(delay = 300))
        }

        item {
            Box(modifier = Modifier.slideUpEntrance(2, baseDelay = 80)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                ) {
                    SettingsRowWithIcon(
                        icon = Icons.Outlined.Fingerprint,
                        label = "Biometric login",
                        trailing = {
                            Switch(
                                checked = biometric,
                                onCheckedChange = { biometric = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ZyvaultWhite,
                                    checkedTrackColor = ZyvaultOrange,
                                    uncheckedThumbColor = ZyvaultWhite,
                                    uncheckedTrackColor = ZyvaultBorder
                                )
                            )
                        },
                        showDivider = true
                    )
                    SettingsRowWithIcon(
                        icon = Icons.Outlined.Notifications,
                        label = "Expiry reminders",
                        trailing = {
                            Switch(
                                checked = expiryReminders,
                                onCheckedChange = { expiryReminders = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ZyvaultWhite,
                                    checkedTrackColor = ZyvaultOrange,
                                    uncheckedThumbColor = ZyvaultWhite,
                                    uncheckedTrackColor = ZyvaultBorder
                                )
                            )
                        },
                        showDivider = true
                    )
                    SettingsRowWithIcon(
                        icon = Icons.Outlined.VerifiedUser,
                        label = "Insurance alerts",
                        trailing = {
                            Switch(
                                checked = insuranceAlerts,
                                onCheckedChange = { insuranceAlerts = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ZyvaultWhite,
                                    checkedTrackColor = ZyvaultOrange,
                                    uncheckedThumbColor = ZyvaultWhite,
                                    uncheckedTrackColor = ZyvaultBorder
                                )
                            )
                        },
                        showDivider = true
                    )
                    SettingsRowWithIcon(
                        icon = Icons.Outlined.HelpOutline,
                        label = "Help & support",
                        trailing = {
                            Icon(
                                Icons.Outlined.ChevronRight,
                                contentDescription = null,
                                tint = ZyvaultDim,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        showDivider = true
                    )
                    SettingsRowWithIcon(
                        icon = Icons.Outlined.DeleteForever,
                        label = "Reset Working Model",
                        trailing = {
                            Text(
                                "CLEAR",
                                color = ZyvaultDanger,
                                style = ZyvaultType.bodySmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.clickable { 
                                    financeViewModel.resetFinanceData()
                                    homeViewModel.fetchUserData()
                                }
                            )
                        },
                        showDivider = false
                    )
                }
            }
        }

        // Sign out button
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            Box(modifier = Modifier.fadeEntrance(delay = 400)) {
                OutlinedButton(
                    onClick = { 
                        auth.signOut()
                        onLogout()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .height(50.dp),
                    shape = RoundedCornerShape(Spacing.buttonRadius),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = androidx.compose.ui.graphics.SolidColor(ZyvaultOrange.copy(alpha = 0.5f))
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ZyvaultOrange)
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sign out", style = ZyvaultType.bodyMedium.copy(fontWeight = FontWeight.SemiBold))
                }
            }
        }

        // Version footer
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Zyvault v1.0.0 — America's super app",
                style = ZyvaultType.caption,
                color = ZyvaultDim,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
        }
    }
}

@Composable
private fun StatColumn(
    value: String,
    label: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(value, style = ZyvaultType.heroMedium, color = valueColor)
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, style = ZyvaultType.micro.copy(letterSpacing = 0.sp), color = ZyvaultMuted)
    }
}

@Composable
private fun SettingsRowWithIcon(
    icon: ImageVector,
    label: String,
    trailing: @Composable () -> Unit,
    showDivider: Boolean
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(icon, contentDescription = null, tint = ZyvaultMuted, modifier = Modifier.size(20.dp))
            Text(
                label,
                style = ZyvaultType.bodyMedium,
                color = ZyvaultWhite,
                modifier = Modifier.weight(1f)
            )
            trailing()
        }
        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenPadding)
                    .height(Spacing.dividerHeight)
                    .background(ZyvaultBorder)
            )
        }
    }
}
