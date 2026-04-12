package com.zyvault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.zyvault.app.ui.components.BrandText
import com.zyvault.app.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun UpgradeScreen(onBack: () -> Unit) {
    var selectedPlan by remember { mutableStateOf("Family") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

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
            Spacer(modifier = Modifier.weight(1f))
            BrandText(size = 20.sp)
            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.width(48.dp))
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(
                start = Spacing.screenPadding,
                end = Spacing.screenPadding,
                bottom = 32.dp
            ),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Text(
                    "Choose Your Plan",
                    style = ZyvaultType.heroMedium,
                    color = ZyvaultWhite,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    "Secure your family's financial future with zero-knowledge encryption.",
                    style = ZyvaultType.bodySmall,
                    color = ZyvaultMuted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            item {
                PlanCard(
                    name = "Personal",
                    price = "$4.99",
                    features = listOf("1 User", "10GB Secure Storage", "Basic Bill Tracking"),
                    isSelected = selectedPlan == "Personal",
                    onClick = { selectedPlan = "Personal" }
                )
            }

            item {
                PlanCard(
                    name = "Family",
                    price = "$9.99",
                    features = listOf("5 Users", "100GB Secure Storage", "Insurance Marketplace", "Priority Support"),
                    isSelected = selectedPlan == "Family",
                    isPopular = true,
                    onClick = { selectedPlan = "Family" }
                )
            }

            item {
                PlanCard(
                    name = "Business",
                    price = "$29.99",
                    features = listOf("Unlimited Users", "1TB Secure Storage", "Custom Permissions", "Dedicated Manager"),
                    isSelected = selectedPlan == "Business",
                    onClick = { selectedPlan = "Business" }
                )
            }
        }

        // Action Button
        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                isLoading = true
                scope.launch {
                    try {
                        FirebaseFirestore.getInstance().collection("users")
                            .document(userId)
                            .update("plan", selectedPlan)
                        onBack()
                    } catch (e: Exception) {
                        // Handle error
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(Spacing.screenPadding)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZyvaultOrange),
            shape = RoundedCornerShape(Spacing.buttonRadius),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = ZyvaultWhite, modifier = Modifier.size(24.dp))
            } else {
                Text("Subscribe to $selectedPlan", style = ZyvaultType.buttonLarge)
            }
        }
    }
}

@Composable
fun PlanCard(
    name: String,
    price: String,
    features: List<String>,
    isSelected: Boolean,
    isPopular: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Spacing.cardRadius),
        color = if (isSelected) ZyvaultOrange.copy(alpha = 0.1f) else ZyvaultCard,
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, ZyvaultOrange)
        } else {
            androidx.compose.foundation.BorderStroke(1.dp, ZyvaultBorder)
        }
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(name, style = ZyvaultType.titleLarge, color = ZyvaultWhite)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(price, style = ZyvaultType.heroMedium, color = ZyvaultWhite)
                        Text("/mo", style = ZyvaultType.bodySmall, color = ZyvaultMuted, modifier = Modifier.padding(bottom = 4.dp))
                    }
                }
                if (isPopular) {
                    Surface(
                        color = ZyvaultOrange,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "POPULAR",
                            style = ZyvaultType.micro.copy(fontWeight = FontWeight.Bold),
                            color = ZyvaultWhite,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = ZyvaultSuccess, modifier = Modifier.size(16.dp))
                    Text(feature, style = ZyvaultType.bodySmall, color = ZyvaultWhite.copy(alpha = 0.8f))
                }
            }
        }
    }
}
