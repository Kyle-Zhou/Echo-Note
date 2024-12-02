package com.example.echonote.ui.components

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Dialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Error
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.echonote.R
import com.example.echonote.data.models.FolderModel

@Composable
fun TextInputDialog(text: String, onSubmit: (String) -> Unit, onDismiss: () -> Unit, defaultTextInput: String="", labelText: String? = null) {
    var textInput by remember { mutableStateOf(defaultTextInput) }
    Dialog(
        onDismissRequest = onDismiss
    ) {
        // popup content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(color = colorResource(id = R.color.blue)))
        {
            Text(
                text,
                color = Color.White,
                textAlign = TextAlign.Center, style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )
            OutlinedTextField(
                value = textInput,
                onValueChange = {textInput = it},
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    cursorColor = Color.White,
                    focusedBorderColor = Color.White,
                    unfocusedBorderColor = Color.White,
                    placeholderColor = Color.White
                ),
                modifier = Modifier.padding(horizontal = 8.dp),
                label = { if(labelText != null) {Text(labelText, color = Color.White) } else null }
            )
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ) {
                TextButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange))
                ) {
                    Text(
                        "Dismiss",
                        color = Color.White
                    )
                }
                TextButton(
                    onClick = { onSubmit(textInput) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(
                        "Save",
                        color = colorResource(id = R.color.orange)
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmDismissDialog(text: String, onConfirm: () -> Unit, onDismiss: () -> Unit,) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        // popup content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(color = colorResource(id = R.color.blue)))
        {
            Text(
                text,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(top = 20.dp, bottom = 10.dp)
            )
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
            ) {
                TextButton(
                    onClick = { onDismiss() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange))
                ) {
                    Text(
                        "Dismiss",
                        color = Color.White
                    )
                }
                TextButton(
                    onClick = { onConfirm() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color.White)
                ) {
                    Text(
                        "Confirm",
                        color = colorResource(id = R.color.orange))
                }
            }
        }
    }
}

@Composable
fun ErrorDialog(errorMessage: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.background(color = colorResource(id = R.color.blue)))
        {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Error",
                    color = Color.White,
                    style = MaterialTheme.typography.h4,
                    modifier = Modifier.padding(end = 20.dp)
                )
                Icon(
                    imageVector = Icons.Rounded.Error,
                    contentDescription = "Error Icon",
                    tint = colorResource(id = R.color.orange),
                    modifier = Modifier.size(38.dp)
                )
            }
            Text(
                text = errorMessage,
                color = colorResource(id = R.color.white),
                textAlign = TextAlign.Left,
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            TextButton(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange)),
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                Text("Dismiss", color = Color.White)
            }
        }
    }
}


@Composable
fun FolderModelDialog(text:String, folderModel: FolderModel, onConfirm: (Long) -> Unit, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = colorResource(id = R.color.blue),
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .background(color = colorResource(id = R.color.blue))
                    .heightIn(max = 400.dp)
                    .padding(16.dp)
            ) {
                Text(
                    text = text,
                    color = Color.White,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.padding(bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    items(folderModel.folders.size) { index ->
                        val folder = folderModel.folders[index]
                        TextButton(
                            onClick = { onConfirm(folder.id) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = folder.title,
                                color = Color.White,
                                style = MaterialTheme.typography.h6
                            )
                        }
                    }
                }
                TextButton(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.orange)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Dismiss", color = Color.White)
                }
            }
        }
    }
}
