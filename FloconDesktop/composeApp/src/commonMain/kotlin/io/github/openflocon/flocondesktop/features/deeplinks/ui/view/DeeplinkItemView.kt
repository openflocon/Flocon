package io.github.openflocon.flocondesktop.features.deeplinks.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.deeplinks.ui.model.DeeplinkPart
import com.florent37.flocondesktop.features.deeplinks.ui.model.DeeplinkViewState
import com.florent37.flocondesktop.features.deeplinks.ui.model.previewDeeplinkViewState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.send
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
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
                style = MaterialTheme.typography.bodySmall,
                color = FloconColors.onSurface,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp),
                    ).padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
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
                icon = Res.drawable.send,
                onClick = {
                    submit(item, values.toMap())
                },
            )
        }
        item.description?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Light,
                    fontStyle = FontStyle.Italic,
                ),
                color = FloconColors.onSurface,
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
            val isValueEmpty = value.isEmpty()
            Box(
                modifier = Modifier.background(
                    color = FloconColors.pannel.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(2.dp),
                ).padding(horizontal = 2.dp, vertical = 2.dp)
                    .width(IntrinsicSize.Min), // Cela fera que la Box enveloppe la largeur minimale de son contenu

            ) {
                Text(
                    text = part.label,
                    style = MaterialTheme.typography.bodySmall,
                    color = FloconColors.onSurface.copy(alpha = 0.45f),
                    modifier = Modifier.graphicsLayer {
                        alpha = if (isValueEmpty) 1f else 0f
                    },
                )

                BasicTextField(
                    textStyle = MaterialTheme.typography.bodySmall.copy(
                        color = FloconColors.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    value = value,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    onValueChange = {
                        value = it
                    },
                )
            }
        }

        is DeeplinkPart.Text -> {
            Text(
                part.value,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = FloconColors.onSurface,
                ),
            )
        }
    }
}

@Composable
private fun DeeplinkSendButton(
    onClick: () -> Unit,
    icon: DrawableResource,
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
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Black),
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
                FloconColors.pannel,
            ),
            submit = { _, _ -> },
            item = previewDeeplinkViewState(),
        )
    }
}
