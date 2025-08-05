package io.github.openflocon.flocondesktop.features.deeplinks.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.deeplinks.ui.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.ui.model.DeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

private val freeformItem = DeeplinkViewState(
    description = null,
    label = null,
    parts = listOf(
        DeeplinkPart.TextField("freeform_link"),
    ),
)

@Composable
fun DeeplinkFreeformItemView(
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }
    val isValueEmpty = value.isEmpty()

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // TODO a label
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 2.dp, vertical = 2.dp)
                        .fillMaxWidth(),

                    ) {
                    Text(
                        text = "freeform link",
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.45f),
                        modifier = Modifier.graphicsLayer {
                            alpha = if (isValueEmpty) 1f else 0f
                        },
                    )
                    BasicTextField(
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = FloconTheme.typography.bodySmall.copy(
                            color = FloconTheme.colorPalette.onSurface,
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        value = value,
                        cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
                        onValueChange = {
                            value = it
                        },
                    )
                }
            }
            DeeplinkSendButton(
                icon = Icons.AutoMirrored.Outlined.Send,
                onClick = {
                    submit(
                        freeformItem,
                        mapOf(
                            DeeplinkPart.TextField("freeform_link") to value,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun DeeplinkSendButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(all = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
@Preview
private fun DeeplinkFreeformItemViewPreview() {
    FloconTheme {
        DeeplinkFreeformItemView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.panel,
            ),
            submit = { _, _ -> },
        )
    }
}
