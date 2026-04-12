package com.zyvault.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvault.app.data.api.IdentitySessionRequest
import com.zyvault.app.data.api.RetrofitClient
import com.zyvault.app.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IdentityVerificationScreen(
    onBack: () -> Unit,
    onComplete: () -> Unit,
    onLaunchStripe: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Identity Verification", style = ZyvaultType.titleLarge, color = ZyvaultWhite) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = ZyvaultWhite)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ZyvaultBlack)
            )
        },
        containerColor = ZyvaultBlack
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Security,
                contentDescription = null,
                tint = ZyvaultOrange,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                "Verify Your Identity",
                style = ZyvaultType.heroMedium,
                color = ZyvaultWhite,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Securely verify your identity using Stripe. This process ensures the highest level of security for your vault and financial data.",
                style = ZyvaultType.bodyMedium,
                color = ZyvaultMuted,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )
            Spacer(modifier = Modifier.height(48.dp))
            
            if (isLoading) {
                CircularProgressIndicator(color = ZyvaultOrange)
            } else {
                Button(
                    onClick = {
                        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return@Button
                        isLoading = true
                        coroutineScope.launch {
                            try {
                                val response = RetrofitClient.stripeApi.createVerificationSession(
                                    IdentitySessionRequest(userId)
                                )
                                onLaunchStripe(response.clientSecret)
                                onComplete() // Close this screen as Stripe takes over
                            } catch (e: Exception) {
                                // Handle error
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ZyvaultOrange),
                    shape = RoundedCornerShape(Spacing.buttonRadius)
                ) {
                    Text("Start Secure Verification", style = ZyvaultType.buttonLarge)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ZyvaultSuccess, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Powered by Stripe", style = ZyvaultType.caption, color = ZyvaultMuted)
            }
        }
    }
}
