package com.zyvault.app.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileUpload
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.VaultViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class BillItem(
    val name: String,
    val amount: String,
    val dueText: String,
    val bank: String,
    val status: String,
    val statusColor: Color,
    val urgencyColor: Color,
    val icon: ImageVector,
    val iconBg: Color
)

@Composable
fun BillsScreen(vaultViewModel: VaultViewModel = viewModel()) {
    val bills = emptyList<BillItem>()

    val metrics = listOf(
        Triple("$0", "Due this week", ZyvaultWhite),
        Triple("$0", "Due this month", ZyvaultWhite),
        Triple("$0", "Overdue", ZyvaultSuccess),
    )

    val context = LocalContext.current
    var showAddDoc by remember { mutableStateOf(false) }
    var isOcrProcessing by remember { mutableStateOf(false) }

    // Camera setup
    var tempPhotoUri by remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            tempPhotoUri?.let { uri ->
                isOcrProcessing = true
                showAddDoc = false
            }
        }
    }

    if (isOcrProcessing) {
        Dialog(
            onDismissRequest = { isOcrProcessing = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ZyvaultBlack.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = ZyvaultOrange)
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Extracting Data...",
                        style = ZyvaultType.titleLarge,
                        color = ZyvaultWhite
                    )
                    Text(
                        "Zyvault AI is identifying bill fields",
                        style = ZyvaultType.bodyMedium,
                        color = ZyvaultMuted
                    )

                    LaunchedEffect(Unit) {
                        tempPhotoUri?.let { uri ->
                            vaultViewModel.uploadDocument(uri, context)
                        }
                        isOcrProcessing = false
                    }
                }
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            vaultViewModel.uploadDocument(it, context)
            showAddDoc = false
        }
    }

    fun createTempPictureUri(context: Context): Uri {
        val tempFile = File.createTempFile(
            "JPEG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}_",
            ".jpg",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            tempFile
        )
    }

    if (showAddDoc) {
        Dialog(
            onDismissRequest = { showAddDoc = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .wrapContentHeight(),
                shape = RoundedCornerShape(20.dp),
                color = ZyvaultCard
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Add Bill",
                            style = ZyvaultType.titleLarge.copy(fontSize = 20.sp),
                            color = ZyvaultWhite
                        )
                        Icon(
                            Icons.Outlined.Close,
                            contentDescription = "Close",
                            tint = ZyvaultMuted,
                            modifier = Modifier
                                .size(24.dp)
                                .clickable { showAddDoc = false }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = ZyvaultWhite.copy(alpha = 0.1f), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        AddDocOption(
                            icon = Icons.Outlined.CameraAlt,
                            title = "Scan with Camera",
                            subtitle = "Auto-detect edges, enhance & OCR",
                            onClick = {
                                val uri = createTempPictureUri(context)
                                tempPhotoUri = uri
                                cameraLauncher.launch(uri)
                            }
                        )
                        AddDocOption(
                            icon = Icons.Outlined.FileUpload,
                            title = "Upload File",
                            subtitle = "PDF, JPG, PNG up to 10MB",
                            onClick = {
                                filePickerLauncher.launch("*/*")
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(ZyvaultBlack)) {
        LazyColumn(contentPadding = PaddingValues(bottom = 100.dp)) {
            // Top bar
            item {
                ZyvaultTopBar { 
                    Icon(
                        Icons.Outlined.Add,
                        contentDescription = "Add Bill",
                        tint = ZyvaultWhite,
                        modifier = Modifier.clickable { showAddDoc = true }
                    )
                }
            }

            // Metrics row
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .slideUpEntrance(0),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.cardGap)
                ) {
                    metrics.forEachIndexed { i, (value, label, color) ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                                .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                                .padding(horizontal = 12.dp, vertical = 14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                value,
                                style = ZyvaultType.heroMedium.copy(fontSize = 20.sp),
                                color = color
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                label,
                                style = ZyvaultType.nano,
                                color = ZyvaultMuted
                            )
                        }
                    }
                }
            }

            // Smart alert banner
            /*
            item {
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.slideUpEntrance(1, baseDelay = 60)) {
                    TapScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.screenPadding)
                                .background(ZyvaultOrange.copy(alpha = 0.12f), RoundedCornerShape(Spacing.cardRadius))
                                .padding(Spacing.cardPadding),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.innerGap)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(Spacing.statusBarWidth)
                                    .height(36.dp)
                                    .background(ZyvaultOrange, RoundedCornerShape(2.dp))
                            )
                            Icon(
                                Icons.Outlined.Warning,
                                contentDescription = null,
                                tint = ZyvaultOrange,
                                modifier = Modifier.size(20.dp)
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    "Chase may be short",
                                    style = ZyvaultType.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                    color = ZyvaultWhite
                                )
                                Text(
                                    "3 bills totaling \$1,723 due this week",
                                    style = ZyvaultType.caption,
                                    color = ZyvaultMuted
                                )
                            }
                        }
                    }
                }
            }
            */

            // Section label
            if (bills.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionLabel("Upcoming Bills", modifier = Modifier.fadeEntrance(delay = 200))
                }
            }

            // Bill cards
            if (bills.isEmpty()) {
                item {
                    EmptyState(
                        icon = Icons.AutoMirrored.Outlined.ReceiptLong,
                        title = "No bills tracked",
                        description = "Scan a paper bill or upload a PDF to automatically track due dates and amounts."
                    )
                }
            }

            itemsIndexed(bills) { index, bill ->
                Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 40)) {
                    TapScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.screenPadding, vertical = 5.dp)
                                .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                        ) {
                            // Left urgency bar
                            Box(
                                modifier = Modifier
                                    .width(Spacing.statusBarWidth)
                                    .height(100.dp)
                                    .background(
                                        bill.urgencyColor,
                                        RoundedCornerShape(topStart = Spacing.cardRadius, bottomStart = Spacing.cardRadius)
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.innerGap),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon circle
                                Box(
                                    modifier = Modifier
                                        .size(Spacing.iconSize)
                                        .background(bill.iconBg, RoundedCornerShape(Spacing.cardRadius)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        bill.icon,
                                        contentDescription = null,
                                        tint = ZyvaultOrange,
                                        modifier = Modifier.size(Spacing.iconSizeSmall)
                                    )
                                }

                                // Name + due + bank
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        bill.name,
                                        style = ZyvaultType.bodyLarge,
                                        color = ZyvaultWhite
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        bill.dueText,
                                        style = ZyvaultType.caption,
                                        color = ZyvaultMuted
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "From ${bill.bank}",
                                        style = ZyvaultType.micro.copy(
                                            fontStyle = FontStyle.Italic,
                                            letterSpacing = 0.sp,
                                            fontWeight = FontWeight.Normal
                                        ),
                                        color = ZyvaultDim
                                    )
                                }

                                // Amount + status + pay
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        bill.amount,
                                        style = ZyvaultType.bodyLarge.copy(
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = ZyvaultWhite
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    StatusBadge(text = bill.status, color = bill.statusColor)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .border(1.5.dp, ZyvaultOrange, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 16.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            "Pay",
                                            style = ZyvaultType.buttonSmall,
                                            color = ZyvaultOrange
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
