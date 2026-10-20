package io.github.openflocon.flocondesktop.features.network.list.view.components

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
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconOutlinedPill
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import androidx.compose.desktop.ui.tooling.preview.Preview

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

val patchMethodBackground = Color(0xFF6f42c1).copy(alpha = 0.3f) // Muted purple for PATCH
val patchMethodText = Color(0xFF6f42c1)

val websocketMethodBackground = Color(0xFF17A2B8).copy(alpha = 0.3f) // Muted cyan for WEBSOCKET
val websocketMethodText = Color(0xFF17A2B8)

val grpcMethodBackground = Color(0xff71CCCB)
val grpcMethodText = Color(0xff244B5A)

// the color of the method in the design system, null: neutral
@Composable
private fun NetworkMethodUi.color(): Color? {
    val colors = FloconTheme.colors
    return when (this) {
        is NetworkMethodUi.Http.GET -> colors.catNetwork
        is NetworkMethodUi.Http.POST -> colors.catAnalytics
        is NetworkMethodUi.Http.PUT -> colors.accent
        is NetworkMethodUi.Http.DELETE -> colors.catCrash
        is NetworkMethodUi.Http.PATCH -> colors.catDeeplink
        is NetworkMethodUi.GraphQl.QUERY,
        is NetworkMethodUi.GraphQl.MUTATION -> colors.catDeeplink

        is NetworkMethodUi.Grpc -> colors.catTable
        is NetworkMethodUi.WebSocket -> colors.catDatabase
        is NetworkMethodUi.OTHER -> null
    }
}

@Composable
fun MethodView(
    method: NetworkMethodUi,
    textSize: TextUnit = FloconTheme.typography.labelMedium.fontSize,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val color = method.color()
    val icon = method.icon

    FloconOutlinedPill(
        text = method.text,
        color = color,
        modifier = modifier.then(if (onClick != null) Modifier.clip(FloconTheme.shapes.small).clickable(onClick = onClick) else Modifier),
        leading = icon?.let {
            {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    colorFilter = ColorFilter.tint(color ?: FloconTheme.colors.inkMuted),
                )
            }
        },
    )
}

@Composable
fun NetworkTag(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    textSize: TextUnit = FloconTheme.typography.labelMedium.fontSize,
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
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny)
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
            Spacer(modifier = Modifier.width(FloconTheme.spacing.extraSmall))
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
