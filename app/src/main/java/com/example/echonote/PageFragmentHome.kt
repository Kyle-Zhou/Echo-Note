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
import com.example.echonote.models.User

@Preview
@Composable
fun HomePageScreen() {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {

        val users = remember { mutableStateListOf<User>() }
        LaunchedEffect(Unit) {
            val results = SupabaseClient.getUsers()
            users.addAll(results)
        }
        // TO-DO: change this to query for the signed in user
        val firstUser = users.firstOrNull() // initially grab the first user in the array

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Hello ${firstUser?.name ?: "..."}!",
                style = MaterialTheme.typography.h4,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
