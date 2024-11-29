package com.example.echonote.ui.components

import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import com.example.echonote.R
import com.example.echonote.data.models.ItemModel
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

enum class SheetState {IDLE, SAVING, SAVED }

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetFragment(summaryText: String, itemModel: ItemModel, isLoading: Boolean, onClose: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    var hasBeenShown by remember { mutableStateOf(false) }
    val scaffoldState = rememberScaffoldState()

    var sheetSaveState by remember { mutableStateOf(SheetState.IDLE) }
    var showDialog by remember { mutableStateOf(false) }
    var fileName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        sheetSaveState = SheetState.IDLE
        fileName = ""
    }

    LaunchedEffect(sheetState.currentValue) {
        if (hasBeenShown && sheetState.currentValue == ModalBottomSheetValue.Hidden) {
            onClose()
        }
        if (sheetState.currentValue == ModalBottomSheetValue.Expanded) {
            hasBeenShown = true
        }
    }

    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(JLatexMathPlugin.create(20f, 20f))
            .build()
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = { showDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth(0.92f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        "Name your file",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    OutlinedTextField(
                        value = fileName,
                        onValueChange = { fileName = it },
                        label = { Text("File name") },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = colorResource(id = R.color.blue),
                            cursorColor = colorResource(id = R.color.blue),
                            focusedBorderColor = colorResource(id = R.color.blue),
                            unfocusedBorderColor = colorResource(id = R.color.blue),
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text("Cancel", color = colorResource(id = R.color.blue))
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    try {
                                        showDialog = false
                                        sheetSaveState = SheetState.SAVING
                                        val jsonSummary = buildJsonObject {
                                            put("summary", summaryText)
                                        }
                                        itemModel.add(fileName, jsonSummary)
                                        sheetSaveState = SheetState.SAVED
                                    } catch (e: Exception) {
                                        sheetSaveState = SheetState.IDLE
                                        scaffoldState.snackbarHostState.showSnackbar("Failed to save note!")
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.blue)
                            )
                        ) {
                            Text("Save", color = Color.White)
                        }
                    }
                }
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.white_variant))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Summary",
                    style = MaterialTheme.typography.h5,
                    color = colorResource(id = R.color.blue)
                )
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    AndroidView(
                        factory = { context ->
                            TextView(context).apply {
                                markwon.setMarkdown(this, summaryText)
                            }
                        },
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        when (sheetSaveState) {
                            SheetState.SAVING -> {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .padding(end = 8.dp),
                                    color = colorResource(id = R.color.blue)
                                )
                            }
                            SheetState.SAVED -> {
                                Text(
                                    text = "Note successfully saved!",
                                    color = colorResource(id = R.color.blue),
                                    modifier = Modifier.align(Alignment.CenterVertically)
                                )
                            }
                            SheetState.IDLE -> {
                                Button(
                                    onClick = {
                                        showDialog = true
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        backgroundColor = colorResource(id = R.color.blue)
                                    ),
                                    shape = RoundedCornerShape(50),
                                    enabled = !isLoading && sheetSaveState != SheetState.SAVING
                                ) {
                                    Text(
                                        text = "Save Note",
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
    ) {
        coroutineScope.launch { sheetState.show() }
    }
}
