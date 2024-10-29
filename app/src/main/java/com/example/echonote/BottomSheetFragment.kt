package com.example.echonote

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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun BottomSheetFragment(summaryText: String, isLoading: Boolean, onClose: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    // Initialize Markwon with LaTeX support
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(JLatexMathPlugin.create(20f, 20f))
            .build()
    }

    ModalBottomSheetLayout(
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(colorResource(id = R.color.white_variant))
                    .padding(16.dp)
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
                        Button(
                            onClick = {
                                onClose()
                                coroutineScope.launch { sheetState.hide() }
                            },
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.blue)
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text("Echo Me", color = colorResource(id = R.color.white))
                        }
                    }
                }
            }
        },
        sheetState = sheetState
    ) {
        coroutineScope.launch { sheetState.show() }
    }
}
