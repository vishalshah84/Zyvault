package com.zyvault.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.zyvault.app.data.api.RetrofitClient
import com.zyvault.app.data.api.TransferRequest
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.util.Locale

@Composable
fun TransferScreen(
    onBack: () -> Unit,
    financeViewModel: FinanceViewModel = viewModel()
) {
    val uiState by financeViewModel.uiState.collectAsState()
    val bankAccounts = uiState.bankAccounts
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var amount by remember { mutableStateOf("0") }
    var selectedFromAccount by remember { mutableStateOf<String?>(null) }
    var selectedToAccount by remember { mutableStateOf<String?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack)
            .statusBarsPadding()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.screenPadding, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.Close, contentDescription = "Close", tint = ZyvaultWhite)
            }
            Text(
                "Transfer Money",
                style = ZyvaultType.titleLarge,
                color = ZyvaultWhite,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = Spacing.screenPadding),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Amount Input
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Enter Amount", style = ZyvaultType.bodySmall, color = ZyvaultMuted)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "$${if (amount == "0") "0" else amount}",
                        style = ZyvaultType.heroLarge.copy(fontSize = 48.sp),
                        color = ZyvaultWhite
                    )
                }
            }

            // From/To Selection
            item {
                if (bankAccounts.isEmpty()) {
                    EmptyState(
                        icon = Icons.Default.AccountBalance,
                        title = "No accounts available",
                        description = "Add accounts in the Finance tab to enable transfers."
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                            .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val fromBank = bankAccounts.find { it.name == selectedFromAccount }
                        AccountSelector(
                            label = "From",
                            accountName = selectedFromAccount ?: "Select Account",
                            balance = fromBank?.let { "$${String.format(Locale.US, "%,.2f", it.balance)}" } ?: "",
                            color = fromBank?.let { Color(it.colorHex.toColorInt()) } ?: ZyvaultOrange,
                            onClick = { /* In a real app, show a bottom sheet picker */ 
                                selectedFromAccount = bankAccounts.firstOrNull()?.name
                            }
                        )
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(ZyvaultBorder)
                        ) {
                            Icon(
                                Icons.Default.SwapVert,
                                contentDescription = null,
                                tint = ZyvaultOrange,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .background(ZyvaultCard)
                                    .padding(horizontal = 8.dp)
                            )
                        }

                        val toBank = bankAccounts.find { it.name == selectedToAccount }
                        AccountSelector(
                            label = "To",
                            accountName = selectedToAccount ?: "Select Account",
                            balance = toBank?.let { "$${String.format(Locale.US, "%,.2f", it.balance)}" } ?: "",
                            color = toBank?.let { Color(it.colorHex.toColorInt()) } ?: ZyvaultOrange,
                            onClick = { 
                                selectedToAccount = bankAccounts.lastOrNull()?.name
                            }
                        )
                    }
                }
            }

            // Keypad
            item {
                NumberKeypad(
                    onNumberClick = { 
                        if (amount == "0") amount = it else amount += it
                    },
                    onDeleteClick = {
                        amount = if (amount.length > 1) amount.dropLast(1) else "0"
                    }
                )
            }
        }

        // Bottom Action
        Button(
            enabled = bankAccounts.isNotEmpty() && !isProcessing && amount != "0",
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                val fromId = bankAccounts.find { it.name == selectedFromAccount }?.id ?: ""
                val toId = bankAccounts.find { it.name == selectedToAccount }?.id ?: ""
                
                isProcessing = true
                coroutineScope.launch {
                    try {
                        val response = RetrofitClient.plaidApi.createTransfer(
                            TransferRequest(userId, fromId, toId, amount.toDouble())
                        )
                        if (response.success) {
                            Toast.makeText(context, "Transfer Successful!", Toast.LENGTH_LONG).show()
                            financeViewModel.fetchFinanceData()
                            onBack()
                        } else {
                            Toast.makeText(context, "Transfer Failed: ${response.status}", Toast.LENGTH_LONG).show()
                        }
                    } catch (_: Exception) {
                        Toast.makeText(context, "Network Error", Toast.LENGTH_SHORT).show()
                    } finally {
                        isProcessing = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.screenPadding)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ZyvaultOrange,
                disabledContainerColor = ZyvaultBorder
            ),
            shape = RoundedCornerShape(Spacing.buttonRadius)
        ) {
            if (isProcessing) {
                CircularProgressIndicator(color = ZyvaultWhite, modifier = Modifier.size(24.dp))
            } else {
                Text("Transfer $${amount}", style = ZyvaultType.buttonLarge)
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
            }
        }
    }
}

@Composable
fun AccountSelector(label: String, accountName: String, balance: String, color: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(if (accountName == "Select Account") "?" else accountName.take(1), color = ZyvaultWhite, fontWeight = FontWeight.Bold)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = ZyvaultType.micro, color = ZyvaultMuted)
            Text(accountName, style = ZyvaultType.bodyMedium, color = ZyvaultWhite)
        }
        Text(balance, style = ZyvaultType.bodySmall, color = ZyvaultMuted)
        Icon(Icons.Default.ExpandMore, contentDescription = null, tint = ZyvaultMuted)
    }
}

@Composable
fun NumberKeypad(onNumberClick: (String) -> Unit, onDeleteClick: () -> Unit) {
    val keys = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", ".", "0", "DEL")
    
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        for (i in 0 until 4) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                for (j in 0 until 3) {
                    val key = keys[i * 3 + j]
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .clickable { 
                                if (key == "DEL") onDeleteClick() else onNumberClick(key)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (key == "DEL") {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = null, tint = ZyvaultWhite)
                        } else {
                            Text(key, style = ZyvaultType.heroMedium, color = ZyvaultWhite)
                        }
                    }
                }
            }
        }
    }
}
