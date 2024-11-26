package com.example.echonote.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.data.entities.Folder
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import com.example.echonote.data.controller.FolderController
import com.example.echonote.data.controller.FolderControllerEvent
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.components.ConfirmDismissDialog
import com.example.echonote.ui.components.ErrorDialog
import com.example.echonote.ui.components.TextInputDialog
import com.example.echonote.ui.models.ViewFolderModel
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import com.example.echonote.utils.IllegalStateEchoNoteException
import kotlinx.coroutines.launch

// Utility to get the current time
fun currentMoment() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

@Composable
fun HomePageScreen(navController: NavHostController) {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
        val name = SupabaseClient.getName()
        val folderModel by remember {mutableStateOf<FolderModel>(FolderModel(SupabaseClient, ::currentMoment))}
        val viewModel by remember { mutableStateOf(ViewFolderModel(folderModel)) }
        val folderController by remember { mutableStateOf(FolderController(folderModel)) }
        var showNewFolderDialog by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        var errorMessage by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            val uuid = SupabaseClient.getCurrentUserID()
            SupabaseClient.setCurrentUser(uuid)
            folderModel.init()
            SupabaseClient.logCurrentSession()
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hello ${name}! 👋",
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {showNewFolderDialog = true},
                modifier = Modifier.padding(bottom = 4.dp)
                ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.AddCircleOutline,
                        contentDescription = "Add Folder Icon",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 14.dp)
                        )
                    Text(text = "Create a New Folder",
                        style = MaterialTheme.typography.h6,
                        color = Color.White)
                }
            }

            if(errorMessage.isNotEmpty()) {
                ErrorDialog(errorMessage) { errorMessage = "" }
            }

            if(showNewFolderDialog) {
                TextInputDialog("Create a new folder", {
                    coroutineScope.launch {
                        try {
                            folderController.invoke(FolderControllerEvent.ADD, title = it)
                        } catch (e: IllegalArgumentException) {
                            println("Error when adding folder: $e")
                            errorMessage = "Title must be unique"
                        } catch (e: Exception) {
                            println("Error creating: $e")
                        }
                        println(folderModel.folders)
                    }
                    showNewFolderDialog = false
                }, {showNewFolderDialog = false})
            }

            // Iterate through folders and render dropdown menus
            LazyColumn {
                items(viewModel.folders.size) {index -> FolderCard(viewModel.folders[index], navController, folderController)}
            }
        }
    }
}

enum class FolderCardDropdownItem {RENAME, CHANGE_DESC, DELETE, NONE}

@Composable
fun FolderCard(folder: Folder, navController: NavHostController, folderController: FolderController) {
    var expanded by remember { mutableStateOf(false) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val folderId: Long = folder.id
    var itemModel by remember { mutableStateOf<ItemModel>(ItemModel(SupabaseClient, ::currentMoment, folderId)) }
    var dropdownOption by remember { mutableStateOf(FolderCardDropdownItem.NONE) }
    val coroutineScope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
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
                } catch (_: IllegalArgumentEchoNoteException) {
                    errorMessage = "This title already exists."
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
                try{
                    folderController.invoke(
                        FolderControllerEvent.CHANGE_DESC,
                        id = folderId,
                        description = it
                    )
                } catch (e: Exception) {
                    println("Error when changing description: $e")
                }
            }
            dropdownOption = FolderCardDropdownItem.NONE
            }, onDismiss = ::defaultDismiss, defaultTextInput = folder.description?:"")
        FolderCardDropdownItem.DELETE -> ConfirmDismissDialog("Are you sure you want to delete this folder?",
            onConfirm = {
            coroutineScope.launch {
                try{
                    folderController.invoke(FolderControllerEvent.DEL, id = folderId)
                } catch (_: IllegalStateEchoNoteException) {
                    errorMessage = "You cannot delete all your folders"
                } catch (e: Exception) {
                    println("Failed to delete folder: $e")
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
                itemModel.items.forEach { item ->
                    Button(
                        onClick = {
                            val summary = item.summary.toString()
                            navController.navigate("item/${folder.title}/${item.title}?summary=$summary")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                        ){
                            Text(text = item.title,
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.White,
                                textAlign = TextAlign.Left,
                                modifier = Modifier.padding(start = 32.dp)
                            )
                        }

                    }
                }
            }
        }
    }
}
