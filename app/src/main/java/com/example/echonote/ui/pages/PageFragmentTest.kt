package com.example.echonote.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.echonote.R

@Preview
@Composable
fun TestPageScreen() {
    Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "This is the Test Page",
                style = MaterialTheme.typography.h4,
                color= Color.White,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}
