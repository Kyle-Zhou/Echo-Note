package com.example.echonote

import LeftRoundedRadioButton
import RightRoundedRadioButton
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.SnackbarHost
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.draw.clip
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Color


@Preview
@Composable
fun AddPageScreen(navController: NavController = rememberNavController()) {
    val summarization = remember { Summarization() }
    var selectedOption by remember { mutableStateOf("Default") }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    //  TO-DO: replace with user's db
    val options = listOf("Default", "CS240", "CS241", "CS242")

    var selectedMode by remember { mutableStateOf("Record") }
    var textInput by remember { mutableStateOf("") }
    val recordedText by remember { mutableStateOf("Recorded Text will appear here") }
    val isRecordMode = selectedMode == "Record"

    var summaryText by remember { mutableStateOf("") } // Summarized text
    var isSheetVisible by remember { mutableStateOf(false) } // BottomSheetFragment
    var isLoading by remember { mutableStateOf(false) } // Loading State
    var isRecording by remember { mutableStateOf(false) } // Recoding State
    val context = LocalContext.current
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    fun hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow((context as android.app.Activity).currentFocus?.windowToken, 0)
    }

    Scaffold(
        scaffoldState = scaffoldState,
        snackbarHost = { SnackbarHost(scaffoldState.snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = colorResource(id = R.color.blue))
                .padding(padding)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 5.dp,)
            ) {
                Button(
                    onClick = {
                        // TO-DO:Logic to go back home here
                    },
                    modifier = Modifier
                        .height(35.dp)
                        .width(110.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Transparent // If needed for icon or text
                    ),
                    elevation = ButtonDefaults.elevation(0.dp), // Remove button shadow
                    shape = RoundedCornerShape(50) // Optional, if you want rounded edges
                ) {
                    Icon(
                        Icons.Filled.ArrowBackIos,
                        contentDescription = "Back to Home",
                        tint = colorResource(id = R.color.white),
                        modifier = Modifier.size(15.dp)
                    )
                    Text("Cancel", color = colorResource(id = R.color.white))
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 55.dp, start = 30.dp, end = 30.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box{
                    OutlinedButton(
                        onClick = { isDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.blue)),
                        border = null,
                        contentPadding = PaddingValues(5.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(selectedOption,
                                color = colorResource(id = R.color.white),
                                fontSize = MaterialTheme.typography.h6.fontSize)
                            Icon(
                                Icons.Filled.ArrowDropDown,
                                contentDescription = "Dropdown Arrow",
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier.size(30.dp)
                            )
                        }
                    }
                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .background(color = colorResource(id = R.color.white))
                            .padding(start = 5.dp)
                    ) {
                        options.forEach { option ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedOption = option
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier.background(color = colorResource(id = R.color.white))
                            ) {
                                Text(
                                    option,
                                    color = colorResource(id = R.color.blue),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    LeftRoundedRadioButton(
                        selected = isRecordMode,
                        text = "Record",
                        onClick = { selectedMode = "Record" }
                    )

                    RightRoundedRadioButton(
                        selected = !isRecordMode,
                        text = "Text",
                        onClick = { selectedMode = "Text" }
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            hideKeyboard()
                            if (textInput.isNotEmpty()) {
                                isLoading = true
                                isSheetVisible = true
                                summarization.getSummary(textInput) { response ->
                                    summaryText = response
                                    isLoading = false
                                }
                            } else {
                                coroutineScope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar("Please input text to summarize!")
                                }
                            }
                        },
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange)),
                        modifier = Modifier
                            .height(35.dp)
                            .width(110.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = colorResource(id = R.color.white),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text("SUBMIT", color = colorResource(id = R.color.white))
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Filled.AutoAwesome,
                                contentDescription = "Send Icon",
                                tint = colorResource(id = R.color.white),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                // Record function here - Kyle?
                if (isRecordMode) {
                    Button(
                        onClick = {
                            isRecording = !isRecording
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = if (isRecording) colorResource(id = R.color.red) else colorResource(id = R.color.blue)
                        )
                    ) {
                        Text(if (isRecording) "Recording..." else "Record", color = colorResource(id = R.color.white))
                    }
                    Text(
                        recordedText,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color = colorResource(id = R.color.white)
                    )
                } else {
                    OutlinedTextField(
                        value = textInput,
                        onValueChange = { textInput = it },
                        label = { Text("Text Input here", color = colorResource(id = R.color.white)) },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = colorResource(id = R.color.white),
                            cursorColor = colorResource(id = R.color.white),
                            focusedBorderColor = colorResource(id = R.color.white),
                            unfocusedBorderColor = colorResource(id = R.color.white),
                            placeholderColor = colorResource(id = R.color.white)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    )
                }
            }

            if (isSheetVisible) {
                BottomSheetFragment(
                    summaryText = summaryText,
                    isLoading = isLoading,
                    onClose = {
                        isSheetVisible = false
                    }
                )
            }
        }
    }
}