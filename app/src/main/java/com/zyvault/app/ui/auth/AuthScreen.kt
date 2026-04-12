package com.zyvault.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.zyvault.app.data.model.User
import com.zyvault.app.ui.components.BrandText
import com.zyvault.app.ui.components.ZyvaultLogo
import com.zyvault.app.ui.theme.*

@Composable
fun AuthScreen(onAuthSuccess: () -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZyvaultBlack)
            .padding(24.dp)
            .systemBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ZyvaultLogo(size = 80.dp)
        Spacer(modifier = Modifier.height(16.dp))
        BrandText(size = 28.sp)
        
        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = if (isLogin) "Welcome Back" else "Create Account",
            style = ZyvaultType.titleLarge,
            color = ZyvaultWhite
        )
        Text(
            text = if (isLogin) "Sign in to continue" else "Join the Zyvault community",
            style = ZyvaultType.bodySmall,
            color = ZyvaultMuted
        )

        Spacer(modifier = Modifier.height(32.dp))

        if (!isLogin) {
            AuthTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Outlined.Person
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        AuthTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            icon = Icons.Outlined.Email
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            icon = Icons.Outlined.Lock,
            isPassword = true
        )

        if (error != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(error!!, color = ZyvaultDanger, style = ZyvaultType.caption)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (email.isEmpty() || password.isEmpty()) {
                    error = "Please fill all fields"
                    return@Button
                }
                isLoading = true
                error = null
                
                if (isLogin) {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            isLoading = false
                            if (task.isSuccessful) {
                                onAuthSuccess()
                            } else {
                                error = task.exception?.message ?: "Login failed"
                            }
                        }
                } else {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val user = auth.currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener {
                                        // Create Firestore user document
                                        val firestore = FirebaseFirestore.getInstance()
                                        val newUser = User(
                                            uid = user.uid,
                                            name = name,
                                            email = email,
                                            plan = "Personal",
                                            documentCount = 0,
                                            bankAccountCount = 0,
                                            billsDueCount = 0,
                                            totalSaved = 0.0
                                        )
                                        
                                        firestore.collection("users").document(user.uid).set(newUser)
                                            .addOnCompleteListener {
                                                isLoading = false
                                                onAuthSuccess()
                                            }
                                    }
                            } else {
                                isLoading = false
                                error = task.exception?.message ?: "Registration failed"
                            }
                        }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(Spacing.buttonRadius),
            colors = ButtonDefaults.buttonColors(containerColor = ZyvaultOrange),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = ZyvaultWhite, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    if (isLogin) "Sign In" else "Register",
                    style = ZyvaultType.buttonLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        TextButton(onClick = { isLogin = !isLogin }) {
            Text(
                text = if (isLogin) "Don't have an account? Register" else "Already have an account? Sign In",
                color = ZyvaultOrange,
                style = ZyvaultType.bodySmall
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isPassword: Boolean = false
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        label = { Text(label, color = ZyvaultMuted) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = ZyvaultOrange) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = ZyvaultCard,
            unfocusedContainerColor = ZyvaultCard,
            disabledContainerColor = ZyvaultCard,
            focusedIndicatorColor = ZyvaultOrange,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = ZyvaultWhite,
            unfocusedTextColor = ZyvaultWhite,
            cursorColor = ZyvaultOrange
        ),
        shape = RoundedCornerShape(Spacing.cardRadius),
        singleLine = true
    )
}
