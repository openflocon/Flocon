package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import org.jetbrains.compose.ui.tooling.preview.Preview

private val getMethodBackground = Color(0xFF007BFF).copy(alpha = 0.3f) // Muted blue for GET
private val getMethodText = Color(0xFF007BFF)

private val postMethodBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for POST
private val postMethodText = Color(0xFF28A745)

private val putMethodBackground = Color(0xFFFFC107).copy(alpha = 0.3f) // Muted yellow for PUT
private val putMethodText = Color(0xFFFFC107)

private val deleteMethodBackground = Color(0xFFDC3545).copy(alpha = 0.3f) // Muted red for DELETE
private val deleteMethodText = Color(0xFFDC3545)

private val otherMethodBackground = Color(0xFF6C757D).copy(alpha = 0.3f) // Muted gray for OTHER
private val otherMethodText = Color(0xFF6C757D)

@Composable
fun MethodView(
    method: NetworkMethodUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) =
        when (method) {
            NetworkMethodUi.DELETE -> deleteMethodBackground to deleteMethodText
            NetworkMethodUi.GET -> getMethodBackground to getMethodText
            is NetworkMethodUi.OTHER -> otherMethodBackground to otherMethodText
            NetworkMethodUi.POST -> postMethodBackground to postMethodText
            NetworkMethodUi.PUT -> putMethodBackground to putMethodText
        }

    Box(
        modifier =
        modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp), // Pill shape
            ).padding(horizontal = 4.dp),
        // Padding inside the tag
        contentAlignment = Alignment.Center, // Center content if Box is larger than text
    ) {
        Text(
            method.text,
            color = textColor,
            fontSize = textSize,
        )
    }
}

@Composable
@Preview
private fun MethodView_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.GET)
    }
}
