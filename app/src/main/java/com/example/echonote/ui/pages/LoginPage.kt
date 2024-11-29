package com.example.echonote.ui.pages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.echonote.R
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.components.ErrorDialog
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun LoginPageScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        ErrorDialog(errorMessage) { showErrorDialog = false }
    }

    val scrollState = rememberScrollState()
    val logoImage = painterResource(id = R.drawable.logo)
    Surface(
        color = MaterialTheme.colors.primary,
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = logoImage,
                contentDescription = "Logo",
                modifier = Modifier
                    .padding(top = 70.dp, bottom = 10.dp )
                    .size(100.dp)
            )
            Text(
                text = "Echo Note",
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            Spacer(modifier = Modifier.height(30.dp))

            Surface(
                color = MaterialTheme.colors.surface,
                shape = RoundedCornerShape(16.dp),
                elevation = 8.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back👋",
                        color = MaterialTheme.colors.primary,
                        style = MaterialTheme.typography.h5,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp, bottom = 15.dp)
                    )

                    // Email Input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        isError = emailError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        trailingIcon = {
                            if (emailError.isNotEmpty()) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                            }
                        }
                    )
                    if (emailError.isNotEmpty()) {
                        Text(
                            text = emailError,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password input with outline
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        isError = passwordError.isNotEmpty(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        trailingIcon = {
                            if (passwordError.isNotEmpty()) {
                                Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                            }
                        }
                    )
                    if (passwordError.isNotEmpty()) {
                        Text(
                            text = passwordError,
                            color = MaterialTheme.colors.error,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.align(Alignment.Start)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Login Button
                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                if (email.isEmpty()) emailError = "Email is required"
                                if (password.isEmpty()) passwordError = "Password is required"
                            } else {
                                // Reset back to empty strings -> removes errors when valid input is given
                                emailError = ""
                                passwordError = ""
                                // Launch a coroutine to call the suspend function
                                coroutineScope.launch {
                                    try {
                                        SupabaseClient.loginUser(email, password)
                                        onLoginSuccess("Login Successful")
                                    } catch (e: Exception) {
                                        errorMessage = "Login failed. ${e.localizedMessage}"
                                        showErrorDialog = true
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Login")
                    }

                    // Navigate to signup screen
                    TextButton(onClick = { onNavigateToSignup() }) {
                        Text(text = "Don't have an account? Sign Up")
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
