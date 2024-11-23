import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.data.entities.Folder
import com.example.echonote.data.entities.Item
import com.example.echonote.data.persistence.SupabaseClient
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import kotlinx.serialization.json.Json
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import kotlinx.serialization.Serializable

@Composable
fun PageFragmentItem(
    navController: NavHostController,
    folderId: Long,
    itemId: Long,
) {
    var folder by remember { mutableStateOf<Folder?>(null) }
    var items by remember { mutableStateOf<List<Item>>(emptyList()) }
    var selectedItem by remember { mutableStateOf<Item?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var summaryText by remember { mutableStateOf("No summary available.") }

    LaunchedEffect(folderId) {
        try {
            folder = SupabaseClient.getFolder(folderId)
            items = SupabaseClient.loadItems(folderId)
            selectedItem = items.find { it.id == itemId }
            summaryText = getSummaryText(selectedItem)
        } catch (e: Exception) {
            errorMessage = e.localizedMessage ?: "An error occurred while fetching data."
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(color = Color.White)
            }
        }
    } else if (errorMessage != null) {
        Surface(color = colorResource(id = R.color.red), modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = errorMessage ?: "Unknown error",
                    color = Color.White,
                    style = MaterialTheme.typography.h5
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(onClick = { navController.popBackStack() }) {
                    Text("Back")
                }
            }
        }
    } else if (folder != null && selectedItem != null) {
        val context = LocalContext.current
        val markwon = remember {
            Markwon.builder(context)
                .usePlugin(JLatexMathPlugin.create(20f, 20f))
                .build()
        }

        Surface(color = colorResource(id = R.color.blue), modifier = Modifier.fillMaxSize()) {
            Box(modifier = Modifier.fillMaxSize()) {
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 20.dp, start = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Color.Transparent
                    ),
                    elevation = ButtonDefaults.elevation(0.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Icon(
                        Icons.Filled.ArrowBackIos,
                        contentDescription = "Back to Home",
                        tint = colorResource(id = R.color.white),
                        modifier = Modifier.size(15.dp)
                    )
                    Text("Back", color = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = 70.dp, start = 30.dp, end = 30.dp)
                ) {
                    Text(
                        text = folder!!.title,
                        style = MaterialTheme.typography.h4,
                        color = Color.White
                    )
                    folder!!.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.White
                        )
                    }
                    var isDropdownExpanded by remember { mutableStateOf(false) }
                    Box {
                        Button(
                            onClick = { isDropdownExpanded = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.White),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedItem!!.title,
                                    color = Color.Black,
                                    style = MaterialTheme.typography.subtitle1
                                )
                                Icon(
                                    imageVector = Icons.Filled.ArrowForwardIos,
                                    contentDescription = if (isDropdownExpanded) "Collapse" else "Expand",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .rotate(if (isDropdownExpanded) 90f else 0f)
                                        .align(Alignment.CenterVertically)
                                        .padding(end = if (!isDropdownExpanded) 8.dp else 0.dp)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            items.forEach { item ->
                                DropdownMenuItem(
                                    onClick = {
                                        selectedItem = item
                                        summaryText = getSummaryText(selectedItem)
                                        isDropdownExpanded = false
                                    }
                                ) {
                                    Text(text = item.title)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        AndroidView(
                            factory = { context ->
                                TextView(context).apply {
                                    setTextColor(android.graphics.Color.WHITE)
                                    textSize = 18f
                                    markwon.setMarkdown(this, summaryText)
                                }
                            },
                            update = { textView ->
                                markwon.setMarkdown(textView, summaryText)
                            } // Force AndroidView to re-render with updated summaryText
                        )
                        Spacer(modifier = Modifier.height(50.dp)) // Avoid overlapping with Add button
                    }
                }
            }
        }
    }
}

@Serializable
data class SummaryData(
    val summary: String
)

// get summary text for an item
private fun getSummaryText(item: Item?): String {
    if (item != null) {
        val json = Json { ignoreUnknownKeys = true }
        val summaryString = item.summary.toString()
        val summaryData = runCatching { json.decodeFromString<SummaryData>(summaryString) }.getOrNull()
        return summaryData?.summary?.replace("\\n", "\n") ?: "No summary available."
    }
    return "Item not Found."
}