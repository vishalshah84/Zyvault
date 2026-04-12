package com.zyvault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvault.app.ui.theme.*

data class NotificationData(
    val id: Int,
    val title: String,
    val description: String,
    val time: String,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType {
    ALERT, INFO, SUCCESS, WARNING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationCenterScreen(onBack: () -> Unit) {
    val notifications = remember {
        mutableStateListOf<NotificationData>()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", style = ZyvaultType.titleLarge, color = ZyvaultWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ZyvaultWhite)
                    }
                },
                actions = {
                    IconButton(onClick = { notifications.clear() }) {
                        Icon(Icons.Outlined.DeleteOutline, contentDescription = "Clear all", tint = ZyvaultMuted)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ZyvaultBlack
                )
            )
        },
        containerColor = ZyvaultBlack
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = ZyvaultDim
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("All caught up!", style = ZyvaultType.bodyLarge, color = ZyvaultMuted)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(notifications) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationData) {
    val indicatorColor = when (notification.type) {
        NotificationType.ALERT -> ZyvaultDanger
        NotificationType.WARNING -> ZyvaultOrange
        NotificationType.SUCCESS -> ZyvaultSuccess
        NotificationType.INFO -> ZyvaultMuted
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .clip(CircleShape)
                .background(if (notification.isRead) Color.Transparent else indicatorColor)
                .then(
                    if (notification.isRead) Modifier.background(ZyvaultBorder, CircleShape) else Modifier
                )
        )

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = ZyvaultType.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = ZyvaultWhite
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.description,
                style = ZyvaultType.bodySmall,
                color = ZyvaultMuted,
                lineHeight = 20.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = notification.time,
                style = ZyvaultType.nano,
                color = ZyvaultDim
            )
        }
    }
}
