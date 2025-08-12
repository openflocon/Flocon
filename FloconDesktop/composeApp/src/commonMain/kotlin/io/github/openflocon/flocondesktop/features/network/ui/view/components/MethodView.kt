package io.github.openflocon.flocondesktop.features.network.ui.view.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

val getMethodBackground = Color(0xFF007BFF).copy(alpha = 0.3f) // Muted blue for GET
val getMethodText = Color(0xFF007BFF)

val postMethodBackground = Color(0xFF28A745).copy(alpha = 0.3f) // Muted green for POST
val postMethodText = Color(0xFF28A745)

val putMethodBackground = Color(0xFFFFC107).copy(alpha = 0.3f) // Muted yellow for PUT
val putMethodText = Color(0xFFFFC107)

val deleteMethodBackground = Color(0xFFDC3545).copy(alpha = 0.3f) // Muted red for DELETE
val deleteMethodText = Color(0xFFDC3545)

val otherMethodBackground = Color(0xFF6C757D).copy(alpha = 0.3f) // Muted gray for OTHER
val otherMethodText = Color(0xFF6C757D)

private val graphQlQueryMethodBackground =
    Color(0XAAE235A9).copy(alpha = 0.8f) // Muted gray for OTHER
private val graphQlQueryMethodText = Color(0XAAFFFFFF)

val grpcMethodBackground = Color(0xff71CCCB)
val grpcMethodText = Color(0xff244B5A)

@Composable
fun MethodView(
    method: NetworkMethodUi,
    textSize: TextUnit = 12.sp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
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
        onClick = onClick,
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
    contentPadding: PaddingValues = PaddingValues(0.dp),
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier =
        modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = backgroundColor,
            )
            .clickable(onClick = { onClick?.invoke() }, enabled = onClick != null)
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .padding(contentPadding),
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
            style = FloconTheme.typography.bodySmall,
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
