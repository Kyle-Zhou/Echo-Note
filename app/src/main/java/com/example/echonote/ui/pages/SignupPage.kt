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
import com.example.echonote.data.controller.FolderController
import com.example.echonote.data.controller.FolderControllerEvent
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.SupabaseClient
import kotlinx.coroutines.launch


@Composable
fun SignupPageScreen(
    onSignupSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope() // Creating a coroutine scope
    val folderModel by remember {mutableStateOf<FolderModel>(FolderModel(SupabaseClient, ::currentMoment))}
    val folderController by remember { mutableStateOf(FolderController(folderModel)) }

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

        // Name input with outline
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            isError = nameError.isNotEmpty(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            singleLine = true,
            trailingIcon = {
                if (nameError.isNotEmpty()) {
                    Icon(Icons.Default.Error, contentDescription = "Error", tint = MaterialTheme.colors.error)
                }
            }
        )
        if (nameError.isNotEmpty()) {
            Text(text = nameError, color = MaterialTheme.colors.error, style = MaterialTheme.typography.body2)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                nameError = ""
                emailError = ""
                passwordError = ""
                confirmPasswordError = ""
                if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    if (name.isEmpty()) nameError = "Name is required"
                    if (email.isEmpty()) emailError = "Email is required"
                    if (password.isEmpty()) passwordError = "Password is required"
                    if (confirmPassword.isEmpty()) confirmPasswordError = "Confirm Password is required"
                } else if (password != confirmPassword) {
                    confirmPasswordError = "Passwords do not match"
                } else {
                    // Launch a coroutine to call the suspend function
                    coroutineScope.launch {
                        try {
                            // Create user in supabase auth table
                            SupabaseClient.signupUser(email, password, name)
                            // Set current user to current session's uuid locally
                            val uuid = SupabaseClient.getCurrentUserID()
                            SupabaseClient.setCurrentUser(uuid)
                            // Create "Default" folder
                            folderController.invoke(FolderControllerEvent.ADD, title = "Default", description = "Hi! This is your default folder!")
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
