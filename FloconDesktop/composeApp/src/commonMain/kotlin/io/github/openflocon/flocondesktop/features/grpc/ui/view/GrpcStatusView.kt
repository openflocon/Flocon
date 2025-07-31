package io.github.openflocon.flocondesktop.features.grpc.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.grpc.ui.model.GrpcItemViewState
import org.jetbrains.compose.ui.tooling.preview.Preview

// Custom colors for networkStatusUi/method views to integrate better with the theme
val successBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for success
val successText = Color(0xFF28A745) // Brighter green for text

val errorBackground = Color(0xFFDC3545).copy(alpha = 0.3f) // Muted red for error
val errorText = Color(0xFFDC3545) // Brighter red for text

private val waitingBackground = Color(0xFF6C757D).copy(alpha = 0.3f) // Muted gray for OTHER
private val waitingText = Color(0xFF6C757D)

@Composable
fun GrpcStatusView(
    status: GrpcItemViewState.StatusViewState,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
        modifier
            .background(
                color = when (status) {
                    is GrpcItemViewState.StatusViewState.Failure -> errorBackground
                    is GrpcItemViewState.StatusViewState.Success -> successBackground
                    is GrpcItemViewState.StatusViewState.Waiting -> waitingBackground
                },
                shape = RoundedCornerShape(20.dp), // Pill shape
            ).padding(horizontal = 8.dp, vertical = 4.dp),
        // Padding inside the tag
        contentAlignment = Alignment.Center, // Center content if Box is larger than text
    ) {
        Text(
            modifier = modifier,
            text = status.text,
            textAlign = TextAlign.Center,
            fontSize = textSize,
            color = when (status) {
                is GrpcItemViewState.StatusViewState.Failure -> errorText
                is GrpcItemViewState.StatusViewState.Success -> successText
                is GrpcItemViewState.StatusViewState.Waiting -> waitingText
            },
            style = MaterialTheme.typography.labelSmall, // Use typography for consistency
        )
    }
}

@Composable
@Preview
private fun StatusView_Preview() {
    FloconTheme {
        GrpcStatusView(
            status =
            GrpcItemViewState.StatusViewState.Success("OK"),
        )
    }
}

@Composable
@Preview
private fun StatusView_Failure_Preview() {
    FloconTheme {
        GrpcStatusView(
            status =
            GrpcItemViewState.StatusViewState.Failure("Error"),
        )
    }
}

@Composable
@Preview
private fun StatusView_Waiting_Preview() {
    FloconTheme {
        GrpcStatusView(
            status =
            GrpcItemViewState.StatusViewState.Waiting("Waiting"),
        )
    }
}
