package com.zyvault.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zyvault.app.ui.components.BrandText
import com.zyvault.app.ui.components.ZyvaultLogo
import com.zyvault.app.ui.theme.*

@Composable
fun SplashScreen(onGetStarted: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Logo with scale entrance
        Box(modifier = Modifier.scaleEntrance(delay = 0, duration = 600)) {
            ZyvaultLogo(size = 120.dp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Brand text
        Box(modifier = Modifier.fadeEntrance(delay = 300)) {
            BrandText(size = 28.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tagline
        Box(modifier = Modifier.fadeEntrance(delay = 500)) {
            Text(
                text = "Your entire life.\nOne app. Always verified.",
                style = ZyvaultType.bodyMedium,
                color = ZyvaultMuted,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Get Started button
        Box(modifier = Modifier.slideUpEntrance(0, baseDelay = 0).fadeEntrance(delay = 700)) {
            Button(
                onClick = onGetStarted,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(Spacing.buttonRadiusFull),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZyvaultOrange,
                    contentColor = ZyvaultWhite
                )
            ) {
                Text(
                    text = "Get Started",
                    style = ZyvaultType.buttonLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.fadeEntrance(delay = 900)) {
            Text(
                text = "Sign in",
                style = ZyvaultType.bodyMedium,
                color = ZyvaultWhite.copy(alpha = 0.4f),
                modifier = Modifier.clickable { onGetStarted() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
