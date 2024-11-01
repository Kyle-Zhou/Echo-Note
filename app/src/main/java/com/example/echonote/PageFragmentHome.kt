package com.example.echonote

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echonote.data.entities.User
import com.example.echonote.data.entities.Item
import com.example.echonote.data.entities.Folder
import com.example.echonote.data.persistence.SupabaseClient
import kotlinx.datetime.*

@Preview
@Composable
fun HomePageScreen() {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {

        val users = remember { mutableStateListOf<User>() }
        val folders = remember {mutableStateListOf<Folder>()}
        LaunchedEffect(Unit) {
            val results = SupabaseClient.loadUsers()
            users.addAll(results)
            val folderResults = SupabaseClient.loadFolders()
            folders.addAll(folderResults)
        }
        // TO-DO: change this to query for the signed in user
        val firstUser = users.firstOrNull() // initially grab the first user in the array

//        Test to make sure that it builds properly
        val clock = Clock.System
        val currentMoment = clock.now().toLocalDateTime(TimeZone.UTC)
        Item(1, 1, "Test", "Summary", currentMoment, currentMoment)
        Folder(1, 1, "Test", "Summary", currentMoment, currentMoment)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
//            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello ${firstUser?.name ?: "..."}!",
                style = MaterialTheme.typography.h3,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            for (folder in folders) {
//                TODO: Move this to a separate composable with state and better ui
                Text(
                    text="${folder.id} ${folder.title}",
                    style = MaterialTheme.typography.h4,
                    color = Color.White,
//                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

        }
        }

}
