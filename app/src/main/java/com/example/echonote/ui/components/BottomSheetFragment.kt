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
import com.example.echonote.utils.IllegalArgumentEchoNoteException

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
    var sheetSaveState by remember { mutableStateOf(SheetState.IDLE) }
    var showDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        sheetSaveState = SheetState.IDLE
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

    if (errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }

    if (showDialog) {
        TextInputDialog(
            text = "Name your item",
            onSubmit = {
                coroutineScope.launch {
                    try {
                        showDialog = false
                        sheetSaveState = SheetState.SAVING
                        val jsonSummary = buildJsonObject {
                            put("summary", summaryText)
                        }
                        itemModel.add(it, jsonSummary)
                        sheetSaveState = SheetState.SAVED
                    } catch (e: IllegalArgumentEchoNoteException) {
                        sheetSaveState = SheetState.IDLE
                        errorMessage = "${e.message}"
                    } catch (_: Exception) {
                        sheetSaveState = SheetState.IDLE
                        errorMessage = "Unknown error when trying to save note"
                    }
                }
            },
            onDismiss = {showDialog = false},
            labelText = "Item title"
        )
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
                                    text = "Note saved!",
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
