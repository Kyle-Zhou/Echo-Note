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
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.data.controller.FolderController
import com.example.echonote.data.controller.FolderControllerEvent
import com.example.echonote.data.controller.ItemController
import com.example.echonote.data.entities.Folder
import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.models.ViewItemModel
import com.example.echonote.ui.pages.currentMoment
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import com.example.echonote.data.models.FolderModel


private enum class FolderCardDropdownItem {RENAME, CHANGE_DESC, DELETE, NONE}

@Composable
fun FolderCard(folder: Folder, navController: NavHostController, folderController: FolderController, folderModel: FolderModel, itemController: ItemController) {
    var expanded by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val folderId: Long = folder.id
    val itemModel by remember(folderId) { mutableStateOf(ItemModel(SupabaseClient, ::currentMoment, folderId)) }
    var dropdownOption by remember { mutableStateOf(FolderCardDropdownItem.NONE) }
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }
    val viewModel by remember(folderId, itemModel) { mutableStateOf(ViewItemModel(itemModel)) }
    itemController.attach(itemModel)

    LaunchedEffect(folderId) {
        itemModel.init()
    }

    fun defaultDismiss() { dropdownOption = FolderCardDropdownItem.NONE }

    when (dropdownOption) {
        FolderCardDropdownItem.RENAME -> TextInputDialog("Rename", onSubmit = {
            coroutineScope.launch {
                try {
                    folderController.invoke(FolderControllerEvent.RENAME, id = folderId, title = it)
                } catch(_: EmptyArgumentEchoNoteException) {
                    errorMessage = "Folder name cannot be empty"
                } catch (e: IllegalArgumentEchoNoteException) {
                    errorMessage = "${e.message}"
                } catch (e: Exception) {
                    println(e)
                }
            }
            dropdownOption = FolderCardDropdownItem.NONE
        },
            onDismiss = ::defaultDismiss, defaultTextInput = folder.title)
        FolderCardDropdownItem.CHANGE_DESC -> TextInputDialog("Change Description",
            onSubmit = {
                coroutineScope.launch {
                    try {
                        folderController.invoke(
                            FolderControllerEvent.CHANGE_DESC,
                            id = folderId,
                            description = it
                        )
                    } catch (e: IllegalArgumentEchoNoteException) {
                        errorMessage = "${e.message}"
                    } catch (e: Exception) {
                        println("Error when changing description: $e")
                    }
                }
                dropdownOption = FolderCardDropdownItem.NONE
            }, onDismiss = ::defaultDismiss, defaultTextInput = folder.description?:"")
        FolderCardDropdownItem.DELETE -> ConfirmDismissDialog("Are you sure you want to delete this folder?",
            onConfirm = {
                val previousExpanded = expanded
                expanded = false
                coroutineScope.launch {
                    try{
                        folderController.invoke(FolderControllerEvent.DEL, id = folderId)
                    } catch (_: IllegalStateEchoNoteException) {
                        errorMessage = "You cannot delete all your folders"
                    } catch (e: Exception) {
                        println("Failed to delete folder: $e")
                        expanded = previousExpanded
                    }
                }
                dropdownOption = FolderCardDropdownItem.NONE
            }, onDismiss = ::defaultDismiss)
        FolderCardDropdownItem.NONE -> {}
    }

    if(errorMessage.isNotEmpty()) {
        ErrorDialog(errorMessage) { errorMessage = "" }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.White,
                    modifier = Modifier
                        .rotate(if (expanded) 90f else 0f)
                        .align(Alignment.CenterVertically)
                        .padding(end = if (!expanded) 8.dp else 0.dp)
                )
                Column(modifier = Modifier.weight(1f).padding(start = if (expanded) 16.dp else 8.dp)){
                    Text(
                        text = folder.title,
                        style = MaterialTheme.typography.h6,
                        color = Color.White,
                    )
                    if(folder.description != null && folder.description?.isNotEmpty() == true){
                        Text(
                            text = folder.description ?: "",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.White,
                        )
                    }
                }
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
                                // Rename folder popup
                                dropdownOption = FolderCardDropdownItem.RENAME
                                isDropdownExpanded = false
                            },
                        ) {
                            Text(
                                "Rename Title",
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                // Change folder description popup
                                dropdownOption = FolderCardDropdownItem.CHANGE_DESC
                                isDropdownExpanded = false
                            },
                        ) {
                            Text(
                                "Change Description",
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        DropdownMenuItem(
                            onClick = {
                                // Delete folder popup
                                dropdownOption = FolderCardDropdownItem.DELETE
                                isDropdownExpanded = false
                            },
                        ) {
                            Text(
                                "Delete Folder",
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                viewModel.items.forEach { item ->
                    println("Item in FolderCard: ${item.id}")
                    ItemCard(item, itemController, navController, folder, folderModel)
                }
            }
        }
    }
}