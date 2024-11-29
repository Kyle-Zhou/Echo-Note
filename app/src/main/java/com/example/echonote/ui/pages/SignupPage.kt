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
import com.example.echonote.data.controller.FolderController
import com.example.echonote.data.controller.FolderControllerEvent
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.components.ErrorDialog
import kotlinx.coroutines.launch
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

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
    val coroutineScope = rememberCoroutineScope()
    val folderModel by remember { mutableStateOf(FolderModel(SupabaseClient, ::currentMoment)) }
    val folderController by remember { mutableStateOf(FolderController(folderModel)) }

    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()
    val logoImage = painterResource(id = R.drawable.logo)

    Box(modifier = Modifier.fillMaxSize()) {
        Surface(
            color = MaterialTheme.colors.primary,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Image(
                    painter = logoImage,
                    contentDescription = "Logo",
                    modifier = Modifier
                        .padding(top = 70.dp, bottom = 10.dp)
                        .size(100.dp)
                )
                Text(
                    text = "Echo Note",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(bottom = 20.dp)
                )

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
                            text = "Create Account ⚒️",
                            color = MaterialTheme.colors.primary,
                            style = MaterialTheme.typography.h5,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 10.dp, bottom = 15.dp)
                        )

                        // Name Input
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Name") },
                            isError = nameError.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            trailingIcon = {
                                if (nameError.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colors.error
                                    )
                                }
                            }
                        )
                        if (nameError.isNotEmpty()) {
                            Text(
                                text = nameError,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

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
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colors.error
                                    )
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Password Input
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
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colors.error
                                    )
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Confirm Password Input
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Confirm Password") },
                            isError = confirmPasswordError.isNotEmpty(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            trailingIcon = {
                                if (confirmPasswordError.isNotEmpty()) {
                                    Icon(
                                        Icons.Default.Error,
                                        contentDescription = "Error",
                                        tint = MaterialTheme.colors.error
                                    )
                                }
                            }
                        )
                        if (confirmPasswordError.isNotEmpty()) {
                            Text(
                                text = confirmPasswordError,
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.body2,
                                modifier = Modifier.align(Alignment.Start)
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Sign Up Button
                        Button(
                            onClick = {
                                // Validation and sign-up logic
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
                                    coroutineScope.launch {
                                        try {
                                            SupabaseClient.signupUser(email, password, name)
                                            folderController.invoke(
                                                FolderControllerEvent.ADD,
                                                title = "Default",
                                                description = "Hi! This is your default folder!"
                                            )
                                            onSignupSuccess("Signup Successful")
                                        } catch (e: Exception) {
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

                        TextButton(onClick = { onNavigateToLogin() }) {
                            Text(text = "Already have an account? Login")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        if (showErrorDialog) {
            ErrorDialog(
                errorMessage = errorMessage,
                onDismiss = { showErrorDialog = false }
            )
        }
    }
}
