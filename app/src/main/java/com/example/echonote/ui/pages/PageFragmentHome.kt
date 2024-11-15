package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echonote.R
import com.example.echonote.data.entities.*
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.SupabaseClient
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import kotlinx.datetime.*
import kotlinx.serialization.json.Json

fun currentMoment(): LocalDateTime {
    return Clock.System.now().toLocalDateTime(TimeZone.UTC)
}

@Preview
@Composable
fun HomePageScreen() {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
//        TODO: Improve this code
        val users = remember { mutableStateListOf<User>() }
        val folderModel = remember {mutableStateOf<FolderModel?>(null)}
        val itemsModel = remember { mutableStateOf<ItemModel?>(null) }
        LaunchedEffect(Unit) {
            val results = SupabaseClient.loadUsers()
            SupabaseClient.setCurrentUser(1)
            users.addAll(results)
            folderModel.value = FolderModel(SupabaseClient, ::currentMoment)
            itemsModel.value = ItemModel(SupabaseClient, ::currentMoment, 1)
            SupabaseClient.getCurrentSession()
        }
        // TODO: change this to query for the signed in user
        val firstUser = users.firstOrNull() // initially grab the first user in the array
        var errorMessage by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
//            verticalArrangement = Arrangement.Center
        ) {
            if(errorMessage.isNotEmpty()) {
                Text(text=errorMessage)
            }
            Text(
                text = "Hello ${firstUser?.name ?: "..."}!",
                style = MaterialTheme.typography.h3,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(text = "Folders:",
                style = MaterialTheme.typography.h3,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            val folders = folderModel.value?.folders ?: emptyList<Folder>()
            for (folder in folders) {
//                TODO: Move this to a separate composable with state and better ui
                Text(
                    text="${folder.id} ${folder.title}",
                    style = MaterialTheme.typography.h4,
                    color = Color.White,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
            Text(text = "Items:",
                style = MaterialTheme.typography.h3,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            val items = itemsModel.value?.items ?: emptyList<Item>()
            for (item in items) {
//                TODO: Move this to a separate composable with state and better ui
                Text(
                    text="${item.id} ${item.title}",
                    style = MaterialTheme.typography.h4,
                    color = Color.White,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
