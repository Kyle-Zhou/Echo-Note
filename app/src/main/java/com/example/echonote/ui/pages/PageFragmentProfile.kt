package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.echonote.R
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.components.ErrorDialog
import com.example.echonote.ui.components.TextInputDialog
import kotlinx.coroutines.launch

@Composable
fun ProfilePageScreen(onLogout: () -> Unit) {

    var showEditNameDialog by remember { mutableStateOf(false) }

    // error handling/warning
    val coroutineScope = rememberCoroutineScope()
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    if (showErrorDialog) {
        ErrorDialog(errorMessage) { showErrorDialog = false }
    }

    Surface(
        color = colorResource(id = R.color.blue),
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Edit Profile",
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // edit name button
            Button(
                onClick = { showEditNameDialog = true },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Edit Name",
                        style = MaterialTheme.typography.h6,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "Arrow Icon",
                        tint = Color.White
                    )
                }
            }

            // edit name pop up
            if(errorMessage.isNotEmpty()) {
                ErrorDialog(errorMessage) { errorMessage = "" }
            }
            if(showEditNameDialog) {
                TextInputDialog("Enter new name:", {
                    coroutineScope.launch {
                        try {
                            if (it.isBlank()) {
                                errorMessage = "Name cannot be empty"
                                throw IllegalArgumentException("Name cannot be empty")
                            }
                            SupabaseClient.editUserName(it)
                        } catch (e: Exception) {
                            println("Error changing name: $e")
                        }
                    }
                    showEditNameDialog = false
                }, {showEditNameDialog = false})
            }

            // logout button
            Button(
                onClick = {
                    coroutineScope.launch {
                        try {
                            SupabaseClient.logoutUser()
                            onLogout()
                        } catch (e: Exception) {
                            errorMessage = "Logout failed. ${e.localizedMessage}"
                            showErrorDialog = true
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Transparent),
                elevation = ButtonDefaults.elevation(0.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Logout",
                        style = MaterialTheme.typography.h6,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowForwardIos,
                        contentDescription = "Arrow Icon",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
