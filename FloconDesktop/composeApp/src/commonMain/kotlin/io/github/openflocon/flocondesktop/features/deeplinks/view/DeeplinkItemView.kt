package io.github.openflocon.flocondesktop.features.deeplinks.view

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun DeeplinkItemView(
    item: DeeplinkViewState,
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    val values = remember { mutableStateMapOf<DeeplinkPart.TextField, String>() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        item.label?.let {
            Text(
                text = item.label,
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onSurface,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
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
                item.parts.fastForEach { part ->
                    TextFieldPart(
                        part = part,
                        onFieldValueChanged = { field, value ->
                            values.put(field, value)
                        },
                    )
                }
            }
            DeeplinkSendButton(
                icon = Icons.AutoMirrored.Outlined.Send,
                onClick = {
                    submit(item, values.toMap())
                },
            )
        }
        item.description?.let {
            Text(
                text = it,
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                ),
                color = FloconTheme.colorPalette.onSurface,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun TextFieldPart(
    part: DeeplinkPart,
    onFieldValueChanged: (
        DeeplinkPart.TextField,
        value: String,
    ) -> Unit,
) {
    when (part) {
        is DeeplinkPart.TextField -> {
            val onFieldValueChangedCallback by rememberUpdatedState(onFieldValueChanged)
            var value by remember { mutableStateOf("") }

            LaunchedEffect(part, value) {
                onFieldValueChangedCallback(part, value)
            }

            FloconTextField(
                value = value,
                onValueChange = { value = it },
                placeholder = defaultPlaceHolder(part.label),
                textStyle = FloconTheme.typography.bodySmall.copy(
                    color = FloconTheme.colorPalette.onSurface,
                    fontWeight = FontWeight.Bold,
                )
            )
        }

        is DeeplinkPart.Text -> {
            Text(
                part.value,
                style = FloconTheme.typography.bodySmall.copy(
                    color = FloconTheme.colorPalette.onSurface,
                ),
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
private fun DeeplinkItemViewPreview() {
    FloconTheme {
        DeeplinkItemView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.panel,
            ),
            submit = { _, _ -> },
            item = previewDeeplinkViewState(),
        )
    }
}
