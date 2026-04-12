package com.zyvault.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = ZyvaultOrange,
    background = ZyvaultBlack,
    surface = ZyvaultSurface,
    onPrimary = ZyvaultWhite,
    onBackground = ZyvaultWhite,
    onSurface = ZyvaultWhite
)

@Composable
fun ZyvaultTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = androidx.compose.material3.Typography(),
        content = content
    )
}
