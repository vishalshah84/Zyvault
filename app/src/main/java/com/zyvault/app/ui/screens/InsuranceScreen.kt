package com.zyvault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zyvault.app.ui.components.EmptyState
import com.zyvault.app.ui.components.SectionLabel
import com.zyvault.app.ui.components.ZyvaultTopBar
import com.zyvault.app.ui.theme.*
import com.zyvault.app.ui.viewmodel.InsurancePolicy
import com.zyvault.app.ui.viewmodel.InsuranceViewModel
import java.util.Locale

@Composable
fun InsuranceScreen(
    viewModel: InsuranceViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            ZyvaultTopBar()
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.screenPadding)
                    .background(ZyvaultCard, RoundedCornerShape(Spacing.cardRadius))
                    .border(1.dp, ZyvaultBorder, RoundedCornerShape(Spacing.cardRadius))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Monthly Premiums", style = ZyvaultType.bodySmall, color = ZyvaultMuted)
                Spacer(modifier = Modifier.height(6.dp))
                val totalPremium = uiState.policies.sumOf { it.premium }
                Text(
                    "$${String.format(Locale.US, "%,.2f", totalPremium)}",
                    style = ZyvaultType.heroLarge,
                    color = ZyvaultWhite
                )
            }
        }

        val categories = listOf("House", "Health", "Vehicle")
        categories.forEach { category ->
            item {
                Spacer(modifier = Modifier.height(Spacing.sectionGap))
                SectionLabel(category)
            }

            val filteredPolicies = uiState.policies.filter { it.type.equals(category, ignoreCase = true) }

            if (filteredPolicies.isEmpty()) {
                item {
                    EmptyState(
                        icon = getIconForType(category),
                        title = "No $category policies",
                        description = "Upload your $category insurance documents to the vault to track coverage."
                    )
                }
            } else {
                items(filteredPolicies) { policy ->
                    InsurancePolicyCard(policy)
                }
            }
        }
    }
}

@Composable
fun InsurancePolicyCard(policy: InsurancePolicy) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.screenPadding, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = ZyvaultCard),
        shape = RoundedCornerShape(Spacing.cardRadius),
        border = androidx.compose.foundation.BorderStroke(1.dp, ZyvaultBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        getIconForType(policy.type),
                        contentDescription = null,
                        tint = ZyvaultOrange,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(policy.provider, style = ZyvaultType.bodyLarge, color = ZyvaultWhite)
                        Text("Policy: ${policy.policyNumber}", style = ZyvaultType.caption, color = ZyvaultMuted)
                    }
                }
                Text(
                    "Active",
                    style = ZyvaultType.micro,
                    color = ZyvaultSuccess,
                    modifier = Modifier
                        .background(ZyvaultSuccess.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Coverage", style = ZyvaultType.micro, color = ZyvaultMuted)
                    Text("$${String.format(Locale.US, "%,.0f", policy.coverageAmount)}", style = ZyvaultType.bodyMedium, color = ZyvaultWhite)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Expires", style = ZyvaultType.micro, color = ZyvaultMuted)
                    Text(policy.expiryDate, style = ZyvaultType.bodyMedium, color = ZyvaultWhite)
                }
            }
        }
    }
}

fun getIconForType(type: String): ImageVector {
    return when (type.lowercase()) {
        "house" -> Icons.Default.Home
        "health" -> Icons.Default.MedicalServices
        "vehicle" -> Icons.Default.DirectionsCar
        else -> Icons.Outlined.Shield
    }
}
