import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.example.echonote.R

@Composable
fun LeftRoundedRadioButton(selected: Boolean, text: String, onClick: () -> Unit) {
    val backgroundColor = if (selected) colorResource(id = R.color.white) else colorResource(id = R.color.blue)
    val textColor = if (selected) colorResource(id = R.color.blue) else colorResource(id = R.color.white)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .background(backgroundColor)
            .border(1.dp, colorResource(id = R.color.white), RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 14.dp)
            .width(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = textColor)
    }
}

@Composable
fun RightRoundedRadioButton(selected: Boolean, text: String, onClick: () -> Unit) {
    val backgroundColor = if (selected) colorResource(id = R.color.white) else colorResource(id = R.color.blue)
    val textColor = if (selected) colorResource(id = R.color.blue) else colorResource(id = R.color.white)

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .background(backgroundColor)
            .border(1.dp, colorResource(id = R.color.white), RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 14.dp)
            .width(60.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, color = textColor)
        }
    }
}