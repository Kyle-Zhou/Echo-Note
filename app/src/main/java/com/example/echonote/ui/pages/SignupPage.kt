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
fun SignupPageScreen(
    onSignupSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
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
        Text(text = "Sign Up", style = MaterialTheme.typography.h4, modifier = Modifier.padding(bottom = 24.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password input with outline
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            isError = confirmPasswordError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
            singleLine = true,
            trailingIcon = {
                if (confirmPasswordError.isNotEmpty()) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                }
            },
            visualTransformation = PasswordVisualTransformation()
        )
        if (confirmPasswordError.isNotEmpty()) {
            Text(text = confirmPasswordError, color = MaterialTheme.colors.error, style = MaterialTheme.typography.body2)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button
        Button(
            onClick = {
                // Perform validation and call signup success
                emailError = ""
                passwordError = ""
                confirmPasswordError = ""
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    if (email.isEmpty()) emailError = "Email is required"
                    if (password.isEmpty()) passwordError = "Password is required"
                    if (confirmPassword.isEmpty()) confirmPasswordError = "Confirm Password is required"
                } else if (password != confirmPassword) {
                    confirmPasswordError = "Passwords do not match"
                } else {
                    // Launch a coroutine to call the suspend function
                    coroutineScope.launch {
                        try {
                            SupabaseClient.signupUser(email, password)
                            onSignupSuccess("Signup Successful")
                        } catch (e: Exception) {
                            // Handle error (e.g., network error or validation failure)
                            errorMessage = "Signup failed. ${e.localizedMessage}"
                            showErrorDialog = true

                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Sign Up")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigate to login screen
        TextButton(onClick = { onNavigateToLogin() }) {
            Text(text = "Already have an account? Login")
        }
    }
}
