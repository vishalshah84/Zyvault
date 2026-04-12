package com.zyvault.app.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.lifecycle.viewmodel.compose.viewModel
import com.plaid.link.OpenPlaidLink
import com.plaid.link.linkTokenConfiguration
import com.plaid.link.result.LinkExit
import com.plaid.link.result.LinkSuccess
import com.zyvault.app.ui.components.*
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.FinanceViewModel
import java.util.Locale

@Composable
fun FinanceScreen(
    onTransferClick: () -> Unit = {},
    viewModel: FinanceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Plaid Link Launcher
    val linkLauncher = rememberLauncherForActivityResult(OpenPlaidLink()) { result ->
        when (result) {
            is LinkSuccess -> viewModel.onPlaidSuccess(result)
            is LinkExit -> { /* Handle exit if needed */ }
        }
    }

    fun launchPlaid() {
        val token = uiState.linkToken ?: return
        val config = linkTokenConfiguration {
            token = token
        }
        linkLauncher.launch(config)
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
                Icon(
                    Icons.Outlined.AccountBalance,
                    contentDescription = "Plaid Link",
                    tint = ZyvaultWhite,
                    modifier = Modifier.clickable { launchPlaid() }
                )
            }
        }

        // Total balance card
        item {
            Box(modifier = Modifier.scaleEntrance(delay = 100)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                        .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total balance", style = ZyvaultType.bodySmall, color = ZyvaultMuted)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "$${String.format(Locale.US, "%,.2f", uiState.totalBalance)}",
                        style = ZyvaultType.heroLarge,
                        color = ZyvaultWhite
                    )
                }
            }
        }

        // Send Money button
        item {
            Spacer(modifier = Modifier.height(14.dp))
            Box(modifier = Modifier.slideUpEntrance(0, baseDelay = 100)) {
                Button(
                    onClick = onTransferClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.screenPadding)
                        .height(54.dp),
                    shape = RoundedCornerShape(Spacing.buttonRadius),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ZyvaultOrange,
                        contentColor = ZyvaultWhite
                    )
                ) {
                    Icon(Icons.Outlined.Send, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Send Money", style = ZyvaultType.buttonLarge)
                }
            }
        }

        // Bank accounts section
        item {
            Spacer(modifier = Modifier.height(Spacing.sectionGap))
            SectionLabel("Bank Accounts", modifier = Modifier.fadeEntrance(delay = 200))
        }

        if (uiState.bankAccounts.isEmpty()) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyState(
                        icon = Icons.Outlined.AccountBalance,
                        title = "No bank accounts linked",
                        description = "Securely connect your bank account via Plaid to track balances and auto-sync bills."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launchPlaid() },
                        colors = ButtonDefaults.buttonColors(containerColor = ZyvaultOrange),
                        shape = RoundedCornerShape(Spacing.buttonRadius),
                        modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect with Plaid", style = ZyvaultType.buttonSmall)
                    }
                }
            }
        }

        itemsIndexed(uiState.bankAccounts) { index, bank ->
            Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 50)) {
                TapScale {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenPadding, vertical = Spacing.tinyGap)
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                            .padding(Spacing.cardPadding),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Spacing.iconSize)
                                .background(
                                    Color(bank.colorHex.toColorInt()),
                                    RoundedCornerShape(Spacing.cardRadius)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(bank.initial, color = ZyvaultWhite, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(bank.name, style = ZyvaultType.bodyMedium, color = ZyvaultWhite)
                        }
                        Text(
                            "$${String.format(Locale.US, "%,.2f", bank.balance)}",
                            style = ZyvaultType.bodyLarge.copy(fontWeight = FontWeight.Bold),
                            color = ZyvaultWhite
                        )
                    }
                }
            }
        }

        // Credit cards section
        item {
            Spacer(modifier = Modifier.height(20.dp))
            SectionLabel("Credit Cards", modifier = Modifier.fadeEntrance(delay = 300))
        }

        if (uiState.creditCards.isEmpty()) {
            item {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    EmptyState(
                        icon = Icons.Outlined.CreditCard,
                        title = "No credit cards found",
                        description = "Add your credit cards to monitor utilization and upcoming payments."
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { launchPlaid() },
                        colors = ButtonDefaults.buttonColors(containerColor = ZyvaultOrange),
                        shape = RoundedCornerShape(Spacing.buttonRadius),
                        modifier = Modifier.padding(horizontal = Spacing.screenPadding)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Connect with Plaid", style = ZyvaultType.buttonSmall)
                    }
                }
            }
        }

        itemsIndexed(uiState.creditCards) { index, card ->
            val utilization = if (card.limit > 0) (card.balance / card.limit).toFloat() else 0f
            val utilizationPct = (utilization * 100).toInt()
            val barColor = if (card.dueSoon) ZyvaultDanger else ZyvaultOrange

            var animateBar by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                kotlinx.coroutines.delay((index * 100 + 400).toLong())
                animateBar = true
            }
            val animatedWidth by animateFloatAsState(
                targetValue = if (animateBar) utilization else 0f,
                animationSpec = tween(durationMillis = 800, easing = EaseOutCubic),
                label = "barFill"
            )

            Box(modifier = Modifier.slideUpEntrance(index, baseDelay = 80)) {
                TapScale {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.screenPadding, vertical = 5.dp)
                            .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(barColor, RoundedCornerShape(topStart = Spacing.cardRadius, topEnd = Spacing.cardRadius))
                        )
                        Column(modifier = Modifier.padding(Spacing.cardPadding)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column {
                                    Text("${card.name} ····${card.lastFour}", style = ZyvaultType.bodyLarge, color = ZyvaultWhite)
                                    Text(card.dueText, style = ZyvaultType.bodySmall, color = if (card.dueSoon) ZyvaultDanger else ZyvaultMuted)
                                }
                                Text("$${String.format(Locale.US, "%,.2f", card.balance)}", style = ZyvaultType.heroMedium.copy(fontSize = 20.sp), color = ZyvaultWhite)
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Box(modifier = Modifier.fillMaxWidth().height(4.dp).background(ZyvaultBorder, RoundedCornerShape(2.dp))) {
                                Box(modifier = Modifier.fillMaxWidth(animatedWidth).height(4.dp).background(barColor, RoundedCornerShape(2.dp)))
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("$utilizationPct% used", style = ZyvaultType.micro, color = ZyvaultMuted)
                        }
                    }
                }
            }
        }
    }
}
