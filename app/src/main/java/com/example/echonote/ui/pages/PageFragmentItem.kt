import android.util.Log
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import com.example.echonote.R
import com.example.echonote.data.entities.SummaryData
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Composable
fun PageFragmentItem(
    navController: NavHostController,
    folderTitle: String,
    itemTitle: String,
    summary: JsonElement
) {
    val json = Json { ignoreUnknownKeys = true }
    val summaryString = summary.toString()
    val summaryData = json.decodeFromString<SummaryData>(summaryString)
    val summaryText = summaryData.summary.replace("\\n", "\n")
    Log.e("my summaryText", summaryText)

    val context = LocalContext.current
    val markwon = remember {
        Markwon.builder(context)
            .usePlugin(JLatexMathPlugin.create(20f, 20f))
            .build()
    }

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
            AndroidView(
                factory = { context ->
                    TextView(context).apply {
                        setTextColor(android.graphics.Color.WHITE)
                        textSize = 18f
                        markwon.setMarkdown(this, summaryText)
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
