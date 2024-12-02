package com.example.echonote.ui.components

import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.echonote.R
import com.example.echonote.data.controller.ItemController
import com.example.echonote.data.entities.Item
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.window.Dialog
import com.example.echonote.data.controller.ItemControllerEvent
import com.example.echonote.data.entities.Folder
import com.example.echonote.data.models.FolderModel
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import kotlinx.coroutines.launch


private enum class ItemCardDropdownItem {DELETE, RENAME, MOVE, NONE}

@Composable
fun ItemCard(item: Item, itemController: ItemController, navController: NavController, folder: Folder, folderModel: FolderModel) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var dropdownOption by remember { mutableStateOf(ItemCardDropdownItem.NONE) }
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    val itemId = item.id

    fun defaultDismiss() { dropdownOption = ItemCardDropdownItem.NONE }

    if(errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }

    when (dropdownOption) {
        ItemCardDropdownItem.RENAME -> TextInputDialog("Rename", onSubmit = {
            coroutineScope.launch {
                try {
                    itemController.invoke(ItemControllerEvent.RENAME, folder.id, id = itemId, title = it)
                } catch(_: EmptyArgumentEchoNoteException) {
                    errorMessage = "Item name cannot be empty"
                } catch (e: IllegalArgumentEchoNoteException) {
                    errorMessage = "${e.message}"
                } catch (e: Exception) {
                    println(e)
                }
            }
            dropdownOption = ItemCardDropdownItem.NONE
        },
            onDismiss = ::defaultDismiss, defaultTextInput = item.title)
        ItemCardDropdownItem.MOVE -> {
            FolderModelDialog(
                "Select the folder to move this item into",
                folderModel, {
                coroutineScope.launch {
                    try {
                        itemController.invoke(ItemControllerEvent.MOVE, folder.id, id = itemId, folderId = it)
                    } catch (e: IllegalArgumentEchoNoteException) {
                        errorMessage = "${e.message}"
                    } catch (e: Exception) {
                        println(e)
                    }
                }
                dropdownOption = ItemCardDropdownItem.NONE
            }, onDismiss = ::defaultDismiss)
        }
        ItemCardDropdownItem.DELETE -> ConfirmDismissDialog("Are you sure you want to delete this item?",
            onConfirm = {
                coroutineScope.launch {
                    try{
                        itemController.invoke(ItemControllerEvent.DEL, folder.id, id = itemId)
                    } catch (e: Exception) {
                        println("Failed to delete folder: $e")
                    }
                }
                dropdownOption = ItemCardDropdownItem.NONE
            }, onDismiss = ::defaultDismiss)
        ItemCardDropdownItem.NONE -> {}
    }

    Button(
        onClick = {
            val folderId = folder.id
            navController.navigate("item/${folderId}/${itemId}")
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(text = item.title,
                style = MaterialTheme.typography.subtitle1,
                color = Color.White,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(start = 32.dp).weight(1f)
            )
            TextButton(onClick = { isDropdownExpanded = true },
                modifier = Modifier
                    .height(35.dp)
                    .width(50.dp),
            ){
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Modify icon",
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    tint = Color.White
                )
                DropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false },
                    modifier = Modifier
                        .background(color = colorResource(id = R.color.blue))
                        .padding(start = 5.dp)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            // Rename item popup
                            dropdownOption = ItemCardDropdownItem.RENAME
                            isDropdownExpanded = false
                        },
                    ) {
                        Text(
                            "Rename",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            // Move item popup
                            dropdownOption = ItemCardDropdownItem.MOVE
                            isDropdownExpanded = false
                        },
                    ) {
                        Text(
                            "Move",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    DropdownMenuItem(
                        onClick = {
                            // Delete item popup
                            dropdownOption = ItemCardDropdownItem.DELETE
                            isDropdownExpanded = false
                        },
                    ) {
                        Text(
                            "Delete",
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}
