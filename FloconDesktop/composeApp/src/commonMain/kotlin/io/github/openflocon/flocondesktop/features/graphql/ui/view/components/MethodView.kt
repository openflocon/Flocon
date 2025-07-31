package io.github.openflocon.flocondesktop.features.graphql.ui.view.components

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
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.graphql.ui.model.GraphQlMethodUi
import org.jetbrains.compose.ui.tooling.preview.Preview

private val getMethodBackground = Color(0xFF007BFF).copy(alpha = 0.3f) // Muted blue for GET
private val getMethodText = Color(0xFF007BFF)

private val postMethodBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for POST
private val postMethodText = Color(0xFF28A745)

private val otherMethodBackground = Color(0xFF6C757D).copy(alpha = 0.3f) // Muted gray for OTHER
private val otherMethodText = Color(0xFF6C757D)

@Composable
fun MethodView(
    method: GraphQlMethodUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) =
        when (method) {
            GraphQlMethodUi.GET -> getMethodBackground to getMethodText
            is GraphQlMethodUi.Other -> otherMethodBackground to otherMethodText
            GraphQlMethodUi.POST -> postMethodBackground to postMethodText
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
        MethodView(method = GraphQlMethodUi.GET)
    }
}
