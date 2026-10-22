package io.github.openflocon.flocondesktop.features.network.list.view.components

import androidx.compose.desktop.ui.tooling.preview.Preview
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
import io.github.openflocon.library.designsystem.components.FloconSolidPill
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// each method has its own pair: deep fill + light text in dark theme, pale fill + dark text in light theme
private data class MethodColors(val background: Color, val content: Color)

@Composable
private fun NetworkMethodUi.colors(): MethodColors {
    val colors = FloconTheme.colors
    return when (this) {
        is NetworkMethodUi.Http.GET -> MethodColors(colors.methodGet, colors.onMethodGet)

        is NetworkMethodUi.Http.POST -> MethodColors(colors.methodPost, colors.onMethodPost)

        is NetworkMethodUi.Http.PUT -> MethodColors(colors.methodPut, colors.onMethodPut)

        is NetworkMethodUi.Http.DELETE -> MethodColors(colors.methodDelete, colors.onMethodDelete)

        is NetworkMethodUi.Http.PATCH -> MethodColors(colors.methodPatch, colors.onMethodPatch)

        is NetworkMethodUi.GraphQl.QUERY,
        is NetworkMethodUi.GraphQl.MUTATION -> MethodColors(colors.methodGraphQl, colors.onMethodGraphQl)

        is NetworkMethodUi.Grpc -> MethodColors(colors.methodGrpc, colors.onMethodGrpc)

        is NetworkMethodUi.WebSocket -> MethodColors(colors.methodWebSocket, colors.onMethodWebSocket)

        is NetworkMethodUi.OTHER -> MethodColors(colors.methodOther, colors.onMethodOther)
    }
}

@Composable
fun MethodView(
    method: NetworkMethodUi,
    textSize: TextUnit = FloconTheme.typography.labelMedium.fontSize,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = method.colors()
    val icon = method.icon

    FloconSolidPill(
        text = method.text,
        background = colors.background,
        contentColor = colors.content,
        modifier = modifier.then(if (onClick != null) Modifier.clip(FloconTheme.shapes.small).clickable(onClick = onClick) else Modifier),
        leading = icon?.let {
            {
                Image(
                    painter = painterResource(it),
                    contentDescription = null,
                    modifier = Modifier.size(10.dp),
                    colorFilter = ColorFilter.tint(colors.content),
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

@Composable
@Preview
private fun MethodView_POST_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Http.POST)
    }
}

@Composable
@Preview
private fun MethodView_PUT_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Http.PUT)
    }
}

@Composable
@Preview
private fun MethodView_PATCH_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Http.PATCH)
    }
}

@Composable
@Preview
private fun MethodView_DELETE_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.Http.DELETE)
    }
}

@Composable
@Preview
private fun MethodView_WebSocket_Preview() {
    FloconTheme {
        MethodView(method = NetworkMethodUi.WebSocket)
    }
}
