package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.echonote.R
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

@Composable
fun <JsonElement> PageFragmentItem(
    navController: NavHostController,
    folderTitle: String,
    itemTitle: String,
    summary: JsonElement
) {
    val summaryText = (summary as? JsonObject)?.get("summary") as? JsonPrimitive
    val summaryContent = summaryText?.content ?: "No summary available"
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(backgroundColor = colorResource(id = R.color.white))
            ) {
                Text(text = "Back", color = Color.Black)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = folderTitle,
                style = MaterialTheme.typography.h4,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = itemTitle,
                style = MaterialTheme.typography.h5,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = summaryContent,
                style = MaterialTheme.typography.body1,
                color = Color.White
            )
        }
    }
}
