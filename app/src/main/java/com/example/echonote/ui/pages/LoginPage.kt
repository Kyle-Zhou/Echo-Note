package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.echonote.data.persistence.SupabaseClient
import kotlinx.coroutines.launch

@Composable
fun LoginPageScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToSignup: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Creating a coroutine scope

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h4, modifier = Modifier.padding(bottom = 24.dp))

        // Email input with outline
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = emailError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            trailingIcon = {
                if (emailError.isNotEmpty()) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                }
            }
        )
        if (emailError.isNotEmpty()) {
            Text(text = emailError, color = MaterialTheme.colors.error, style = MaterialTheme.typography.body2)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Password input with outline
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            isError = passwordError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                if (passwordError.isNotEmpty()) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                }
            },
            visualTransformation = PasswordVisualTransformation()
        )
        if (passwordError.isNotEmpty()) {
            Text(text = passwordError, color = MaterialTheme.colors.error, style = MaterialTheme.typography.body2)
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

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to signup screen
        TextButton(onClick = { onNavigateToSignup() }) {
            Text(text = "Don't have an account? Sign Up")
        }
    }
}
