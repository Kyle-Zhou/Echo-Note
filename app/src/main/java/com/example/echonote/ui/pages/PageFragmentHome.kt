package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.echonote.R
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import com.example.echonote.data.controller.FolderController
import com.example.echonote.data.controller.FolderControllerEvent
import com.example.echonote.data.controller.ItemController
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.persistence.SupabaseClient
import com.example.echonote.ui.components.ErrorDialog
import com.example.echonote.ui.components.FolderCard
import com.example.echonote.ui.components.TextInputDialog
import com.example.echonote.ui.models.ViewFolderModel
import com.example.echonote.utils.EmptyArgumentEchoNoteException
import com.example.echonote.utils.IllegalArgumentEchoNoteException
import kotlinx.coroutines.launch

// Utility to get the current time
fun currentMoment() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

@Composable
fun HomePageScreen(navController: NavHostController, folderModel: FolderModel) {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
        val name = SupabaseClient.getName()
        val viewModel by remember { mutableStateOf(ViewFolderModel(folderModel)) }
        val folderController by remember { mutableStateOf(FolderController(folderModel)) }
        var showNewFolderDialog by remember { mutableStateOf(false) }
        val coroutineScope = rememberCoroutineScope()
        var errorMessage by remember { mutableStateOf("") }
        val itemController by remember { mutableStateOf(ItemController()) }

        LaunchedEffect(Unit) {
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
                        } catch (_: EmptyArgumentEchoNoteException) {
                            errorMessage = "Folder name cannot be empty"
                        } catch (e: IllegalArgumentEchoNoteException) {
                            errorMessage = "${e.message}"
                        } catch (e: Exception) {
                            println("Error creating: $e")
                        }
                        println(folderModel.folders)
                    }
                    showNewFolderDialog = false
                }, {showNewFolderDialog = false})
            }

            // TODO: Low priority but for performance we shouldn't be using column if we have lots of data
//            but this is the simplest method of doing it. Otherwise, the move has weird behaviour.
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                viewModel.folders.forEach {
                    folder -> FolderCard(folder, navController, folderController, folderModel, itemController)
                }
            }
        }
    }
}
