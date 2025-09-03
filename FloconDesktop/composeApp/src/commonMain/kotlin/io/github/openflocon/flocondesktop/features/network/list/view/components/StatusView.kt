package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import org.jetbrains.compose.ui.tooling.preview.Preview

// Custom colors for networkStatusUi/method views to integrate better with the theme
val successTagBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for success
val successTagText = Color(0xFF28A745) // Brighter green for text

val errorTagBackground = Color(0xFFDC3545).copy(alpha = 0.3f) // Muted red for error
val errorTagText = Color(0xFFDC3545) // Brighter red for text
val exceptionTagText = Color(0xFF7B1FA2)

val loadingTagBackground = Color(0xFF6C757D).copy(alpha = 0.3f) // Muted gray for OTHER
val loadingTagText = Color(0xFF6C757D)

@Composable
fun StatusView(
    status: NetworkStatusUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        when (status.status) {
            NetworkStatusUi.Status.SUCCESS,
            NetworkStatusUi.Status.ERROR,
            NetworkStatusUi.Status.EXCEPTION -> BasicText(
                text = status.text,
                autoSize = TextAutoSize.StepBased(
                    maxFontSize = textSize,
                    minFontSize = 8.sp,
                ),
                maxLines = 1,
                style = FloconTheme.typography.labelSmall.copy(
                    color = when (status.status) {
                        NetworkStatusUi.Status.SUCCESS -> successTagText
                        NetworkStatusUi.Status.EXCEPTION -> exceptionTagText
                        NetworkStatusUi.Status.ERROR -> errorTagText
                        NetworkStatusUi.Status.LOADING -> loadingTagText
                    },
                ),
            )

            NetworkStatusUi.Status.LOADING -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                FloconLinearProgressIndicator(
                    modifier = Modifier
                        .width(50.dp),
                )
            }
        }
    }
}

@Composable
@Preview
private fun StatusView_Preview() {
    FloconTheme {
        StatusView(
            status =
            NetworkStatusUi(
                text = "200",
                status = NetworkStatusUi.Status.SUCCESS,
            ),
        )
    }
}
