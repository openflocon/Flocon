package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.common.ui.FloconTheme
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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

private val graphQlQueryMethodBackground = Color(0XAAE235A9).copy(alpha = 0.8f) // Muted gray for OTHER
private val graphQlQueryMethodText = Color(0XAAFFFFFF)

private val grpcMethodBackground = Color(0xff71CCCB)
private val grpcMethodText = Color(0xff244B5A)

@Composable
fun MethodView(
    method: NetworkMethodUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
) {
    val (backgroundColor, textColor) =
        when (method) {
            is NetworkMethodUi.Http.DELETE -> deleteMethodBackground to deleteMethodText
            is NetworkMethodUi.Http.GET -> getMethodBackground to getMethodText
            is NetworkMethodUi.OTHER -> otherMethodBackground to otherMethodText
            is NetworkMethodUi.Http.POST -> postMethodBackground to postMethodText
            is NetworkMethodUi.Http.PUT -> putMethodBackground to putMethodText
            is NetworkMethodUi.GraphQl.QUERY -> graphQlQueryMethodBackground to graphQlQueryMethodText
            is NetworkMethodUi.GraphQl.MUTATION -> graphQlQueryMethodBackground to graphQlQueryMethodText
            is NetworkMethodUi.Grpc -> grpcMethodBackground to grpcMethodText
        }

    NetworkTag(
        text = method.text,
        backgroundColor = backgroundColor,
        textColor = textColor,
        textSize = textSize,
        modifier = modifier,
        icon = method.icon,
    )
}

@Composable
fun NetworkTag(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
    icon: DrawableResource?,
) {
    Row(
        modifier =
        modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(20.dp), // Pill shape
            ).padding(horizontal = 4.dp, vertical = 2.dp),
        // Padding inside the tag
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        icon?.let {
            Image(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(10.dp),
                colorFilter = ColorFilter.tint(textColor),
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = textSize,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
@Preview
private fun MethodView_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Http.GET)
    }
}

@Composable
@Preview
private fun MethodView_GraphQlQuery_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.GraphQl.QUERY)
    }
}

@Composable
@Preview
private fun MethodView_Ggrpc_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Grpc)
    }
}
