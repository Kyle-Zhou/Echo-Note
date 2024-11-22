package com.example.echonote.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import com.example.echonote.R

@Composable
fun TextInputDialog(text: String, onSubmit: (String) -> Unit, onDismiss: () -> Unit, defaultTextInput: String="") {
    var textInput by remember { mutableStateOf(defaultTextInput) }
    Dialog(onDismissRequest = { onDismiss() }) {
        // popup content
        Box(
            modifier = Modifier
                .background(color = colorResource(id = R.color.blue))
        ) {
            Column() {
                Text(text, color = colorResource(id = R.color.white))
                OutlinedTextField(value = textInput, onValueChange = {textInput = it}, colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = colorResource(id = R.color.white),
                    cursorColor = colorResource(id = R.color.white),
                    focusedBorderColor = colorResource(id = R.color.white),
                    unfocusedBorderColor = colorResource(id = R.color.white),
                    placeholderColor = colorResource(id = R.color.white)
                ))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange))) { Text("Dismiss") }
                    TextButton(onClick = { onSubmit(textInput) }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white))) { Text("Save") }
                }
            }
        }
    }
}

@Composable
fun ConfirmDismissDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit,) {
    Dialog(onDismissRequest = { onDismiss() }) {
        // popup content
        Box(
            modifier = Modifier
                .background(color = colorResource(id = R.color.blue))
        ) {
            Column() {
                Text(text, color = colorResource(id = R.color.white))
                Row (verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = { onDismiss() }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange))) { Text("Dismiss") }
                    TextButton(onClick = { onConfirm() }, colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white))) { Text("Confirm") }
                }
            }
        }
    }
}