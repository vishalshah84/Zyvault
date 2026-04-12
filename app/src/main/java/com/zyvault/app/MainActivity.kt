package com.zyvault.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.google.firebase.auth.FirebaseAuth
import com.stripe.android.identity.IdentityVerificationSheet
import com.zyvault.app.ui.auth.AuthScreen
import com.zyvault.app.ui.navigation.Tab
import com.zyvault.app.ui.navigation.ZyvaultBottomBar
import com.zyvault.app.ui.screens.*
import com.zyvault.app.ui.theme.ZyvaultBlack
import com.zyvault.app.ui.theme.ZyvaultTheme

class MainActivity : ComponentActivity() {
    private lateinit var identityVerificationSheet: IdentityVerificationSheet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Initialize Stripe Identity Verification Sheet
        identityVerificationSheet = IdentityVerificationSheet.create(
            this,
            IdentityVerificationSheet.Configuration(
                brandLogo = android.R.drawable.ic_dialog_info // Placeholder logo
            )
        ) { result ->
            when (result) {
                is IdentityVerificationSheet.VerificationFlowResult.Completed -> {
                    Toast.makeText(this, "Verification Submitted!", Toast.LENGTH_SHORT).show()
                }
                is IdentityVerificationSheet.VerificationFlowResult.Canceled -> {
                    Toast.makeText(this, "Verification Canceled", Toast.LENGTH_SHORT).show()
                }
                is IdentityVerificationSheet.VerificationFlowResult.Failed -> {
                    Toast.makeText(this, "Verification Failed: ${result.throwable.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

        setContent {
            ZyvaultTheme {
                ZyvaultApp(onLaunchStripe = { clientSecret ->
                    identityVerificationSheet.present(clientSecret)
                })
            }
        }
    }
}

@Composable
fun ZyvaultApp(onLaunchStripe: (String) -> Unit) {
    var showSplash by remember { mutableStateOf(true) }
    var currentTab by remember { mutableStateOf(Tab.Home) }
    var isAuthenticated by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser != null) }
    var currentScreen by remember { mutableStateOf("main") }

    if (showSplash) {
        SplashScreen(onGetStarted = { showSplash = false })
    } else if (!isAuthenticated) {
        AuthScreen(onAuthSuccess = { isAuthenticated = true })
    } else {
        Box(modifier = Modifier.fillMaxSize()) {
            when (currentScreen) {
                "transfer" -> TransferScreen(onBack = { currentScreen = "main" })
                "insurance" -> InsuranceScreen(onBack = { currentScreen = "main" })
                "upgrade" -> UpgradeScreen(onBack = { currentScreen = "main" })
                "notifications" -> NotificationCenterScreen(onBack = { currentScreen = "main" })
                "verify" -> IdentityVerificationScreen(
                    onBack = { currentScreen = "main" },
                    onComplete = { currentScreen = "main" },
                    onLaunchStripe = onLaunchStripe
                )
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(ZyvaultBlack)
                            .systemBarsPadding()
                    ) {
                        // Content area
                        Box(modifier = Modifier.weight(1f)) {
                            when (currentTab) {
                                Tab.Home -> HomeScreen(
                                    onInsuranceClick = { currentScreen = "insurance" },
                                    onNotificationsClick = { currentScreen = "notifications" }
                                )
                                Tab.Vault -> VaultScreen()
                                Tab.Finance -> FinanceScreen(onTransferClick = { currentScreen = "transfer" })
                                Tab.Insurance -> InsuranceScreen()
                                Tab.Bills -> BillsScreen()
                                Tab.Profile -> ProfileScreen(
                                    onLogout = { isAuthenticated = false },
                                    onUpgradeClick = { currentScreen = "upgrade" },
                                    onVerifyClick = { currentScreen = "verify" }
                                )
                            }
                        }

                        // Bottom navigation
                        ZyvaultBottomBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                }
            }
        }
    }
}
