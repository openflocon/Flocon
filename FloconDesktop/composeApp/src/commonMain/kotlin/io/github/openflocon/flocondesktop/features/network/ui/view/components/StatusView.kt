package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.network.ui.model.NetworkStatusUi
import org.jetbrains.compose.ui.tooling.preview.Preview

// Custom colors for networkStatusUi/method views to integrate better with the theme
val successTagBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for success
val successTagText = Color(0xFF28A745) // Brighter green for text

val errorTagBackground = Color(0xFFDC3545).copy(alpha = 0.3f) // Muted red for error
val errorTagText = Color(0xFFDC3545) // Brighter red for text

@Composable
fun StatusView(
    status: NetworkStatusUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = status.code.toString(),
        textAlign = TextAlign.Center,
        fontSize = textSize,
        color =
        when (status.isSuccess) {
            true -> successTagText
            false -> errorTagText
        },
        style = MaterialTheme.typography.labelSmall, // Use typography for consistency
    )
}

@Composable
@Preview
private fun StatusView_Preview() {
    FloconTheme {
        StatusView(
            status =
            NetworkStatusUi(
                code = 200,
                isSuccess = true,
            ),
        )
    }
}
