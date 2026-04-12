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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zyvault.app.data.model.Document
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.VaultViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

data class DocItem(
    val name: String,
    val expiry: String,
    val status: String,
    val color: Color,
    val categoryTag: String,
    val categoryTagColor: Color,
    val category: String
)

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

@Composable
fun VaultScreen(viewModel: VaultViewModel = viewModel()) {
    val documents by viewModel.documents.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val context = LocalContext.current
    var selectedCat by remember { mutableStateOf("All") }
    val categories = listOf("All", "IDs", "Insurance", "Education", "Legal")

    val filtered = if (selectedCat == "All") documents else documents.filter { it.category == selectedCat }

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
                // Simulate OCR delay
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
                        "Zyvault AI is identifying document fields",
                        style = ZyvaultType.bodyMedium,
                        color = ZyvaultMuted
                    )
                    
                    LaunchedEffect(Unit) {
                        tempPhotoUri?.let { uri ->
                            viewModel.uploadDocument(uri, context)
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
            viewModel.uploadDocument(it, context)
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
                            "Add Document",
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
                ZyvaultTopBar()
            }

            // Search bar
            item {
                Box(modifier = Modifier.slideUpEntrance(0)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenPadding)
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                            .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                            .padding(horizontal = 14.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = ZyvaultMuted,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            "Search documents...",
                            style = ZyvaultType.bodyMedium,
                            color = ZyvaultDim
                        )
                    }
                }
            }

            // Category pills
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = Spacing.screenPadding, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.pillGap)
                ) {
                    items(categories) { cat ->
                        val isActive = selectedCat == cat
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isActive) ZyvaultOrange else ZyvaultSurface,
                                    RoundedCornerShape(Spacing.pillRadius)
                                )
                                .border(
                                    1.dp,
                                    if (isActive) Color.Transparent else ZyvaultBorder,
                                    RoundedCornerShape(Spacing.pillRadius)
                                )
                                .clickable { selectedCat = cat }
                                .padding(horizontal = 18.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = cat,
                                style = ZyvaultType.bodySmall.copy(fontWeight = FontWeight.Medium),
                                color = if (isActive) ZyvaultWhite else ZyvaultMuted
                            )
                        }
                    }
                }
            }

            if (isLoading && documents.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = ZyvaultOrange)
                    }
                }
            }

            if (filtered.isEmpty() && !isLoading) {
                item {
                    EmptyState(
                        icon = Icons.Outlined.Description,
                        title = "No documents found",
                        description = "Upload your first document by tapping the + button below."
                    )
                }
            }

            // Document count
            item {
                Text(
                    text = "${filtered.size} documents",
                    style = ZyvaultType.bodySmall,
                    color = ZyvaultOrange,
                    modifier = Modifier
                        .padding(horizontal = Spacing.screenPadding, vertical = Spacing.tinyGap)
                        .fadeEntrance(delay = 100)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Document cards
            itemsIndexed(filtered) { index, doc ->
                Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 40)) {
                    TapScale {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Spacing.screenPadding, vertical = 5.dp)
                                .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                        ) {
                            // Left status bar
                            val statusColor = when(doc.status) {
                                "Valid" -> ZyvaultSuccess
                                "Expiring" -> ZyvaultWarning
                                "Urgent" -> ZyvaultDanger
                                else -> ZyvaultSuccess
                            }
                            Box(
                                modifier = Modifier
                                    .width(Spacing.statusBarWidth)
                                    .height(80.dp)
                                    .background(
                                        statusColor,
                                        RoundedCornerShape(topStart = Spacing.cardRadius, bottomStart = Spacing.cardRadius)
                                    )
                            )
                            Row(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 14.dp, vertical = 14.dp),
                                horizontalArrangement = Arrangement.spacedBy(Spacing.innerGap),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Category badge
                                val categoryTag = when(doc.category) {
                                    "IDs" -> "ID"
                                    "Insurance" -> "INS"
                                    "Education" -> "EDU"
                                    "Legal" -> "LGL"
                                    else -> "DOC"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(Spacing.iconSize)
                                        .background(
                                            ZyvaultOrange.copy(alpha = 0.12f),
                                            RoundedCornerShape(Spacing.cardRadius)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = categoryTag,
                                        style = ZyvaultType.caption.copy(fontWeight = FontWeight.Bold),
                                        color = ZyvaultOrange
                                    )
                                }

                                // Name + expiry
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        doc.name,
                                        style = ZyvaultType.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                                        color = ZyvaultWhite
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        doc.expiryDate,
                                        style = ZyvaultType.caption,
                                        color = ZyvaultMuted
                                    )
                                }

                                // Status + QR
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadge(text = doc.status, color = statusColor)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Icon(
                                        Icons.Outlined.QrCode2,
                                        contentDescription = "Share QR",
                                        tint = ZyvaultDim,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 32.dp, end = 20.dp)
        ) {
            FloatingActionButton(
                onClick = { showAddDoc = true },
                containerColor = ZyvaultOrange,
                contentColor = ZyvaultWhite,
                shape = CircleShape,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    Icons.Outlined.Add,
                    contentDescription = "Add Document",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}
