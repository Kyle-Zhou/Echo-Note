package com.example.echonote.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.echonote.data.entities.Item
import com.example.echonote.data.entities.User
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.ui.draw.rotate
import com.example.echonote.data.models.FolderModel
import com.example.echonote.data.models.ItemModel
import com.example.echonote.data.persistence.IPersistence
import com.example.echonote.data.persistence.SupabaseClient

// Utility to get the current time
fun currentMoment() = Clock.System.now().toLocalDateTime(TimeZone.UTC)

@Composable
fun HomePageScreen(navController: NavHostController) {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {

        //        TODO: Improve this code
        val users = remember { mutableStateListOf<User>() }
        var folderModel by remember {mutableStateOf<FolderModel?>(null)}
        LaunchedEffect(Unit) {
            val results = SupabaseClient.loadUsers()
            SupabaseClient.setCurrentUser(1)
            users.addAll(results)
            folderModel = FolderModel(SupabaseClient, ::currentMoment)
            SupabaseClient.getCurrentSession()
        }

//        var errorMessage by remember { mutableStateOf("") }

        // Mock values link this with actual db
//        val users = listOf(
//            User(1, "Gen", "gen@gmail.com"),
//            User(2, "Jane", "jane@gmail.com")
//        )
//        val mockFolders = listOf(
//            Folder(1, 1, "CS241", "Foundations of Sequential Programs", currentMoment(), currentMoment()),
//            Folder(2, 1, "CS346", "Application Development", currentMoment(), currentMoment())
//        )
//        val mockItems = listOf(
//            Item(1, 1, "Lecture 1", Json.parseToJsonElement("""{"summary":"bruh bruh bruh bruh"}"""), currentMoment(), currentMoment()),
//            Item(2, 1, "Lecture 2", Json.parseToJsonElement("""{"summary":"bruh bruh bruh bruh"}"""), currentMoment(), currentMoment()),
//            Item(3, 2, "Tutorial 1", Json.parseToJsonElement("""{"summary":"bruh bruh bruh bruh"}"""), currentMoment(), currentMoment())
//        )

        //        // TODO: change this to query for the signed in user
        val firstUser = users.firstOrNull() // initially grab the first user in the array

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Hello ${firstUser?.name ?: "..."}! 👋",
                style = MaterialTheme.typography.h5,
                color = Color.White,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Iterate through folders and render dropdown menus
            (folderModel?.folders?:emptyList<Folder>()).forEach { folder ->
                FolderDropdown(folder = folder, navController = navController)
            }
        }
    }
}

@Composable
fun FolderDropdown(folder: Folder, navController: NavHostController) {
    var expanded by remember { mutableStateOf(false) }
    var itemModel by remember { mutableStateOf<ItemModel?>(null) }
    val folderId: Long = folder.id

    LaunchedEffect(Unit) {
        itemModel = ItemModel(SupabaseClient, ::currentMoment, folderId)
    }
    val items = itemModel?.items?:emptyList<Item>()

    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = { expanded = !expanded },
            colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowForwardIos,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = Color.Black,
                    modifier = Modifier
                        .rotate(if (expanded) 90f else 0f)
                        .align(Alignment.CenterVertically)
                        .padding(end = if (!expanded) 8.dp else 0.dp)
                )
                Text(
                    text = folder.title,
                    style = MaterialTheme.typography.h6,
                    color = Color.Black,
                    modifier = Modifier.weight(1f).padding(start = if (expanded) 8.dp else 0.dp)
                )
            }
        }
        if (expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                items.forEach { item ->
                    Button(
                        onClick = {
                            val summary = item.summary.toString()
                            navController.navigate("item/${folder.title}/${item.title}?summary=$summary")
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = item.title, style = MaterialTheme.typography.subtitle1, color = Color.Black)
                    }
                }
            }
        }
    }
}
