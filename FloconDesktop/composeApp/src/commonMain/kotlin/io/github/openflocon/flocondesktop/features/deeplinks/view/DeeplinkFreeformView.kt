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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconTonalButton
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
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

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FloconTextFieldWithoutM3(
                value = value,
                onValueChange = { value = it },
                placeholder = defaultPlaceHolder("freeform link"),
                textStyle = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                containerColor = FloconTheme.colorPalette.secondary,
                modifier = Modifier.weight(1f),
            )
            FloconIconTonalButton(
                onClick = {
                    submit(
                        freeformItem,
                        mapOf(
                            DeeplinkPart.TextField("freeform_link") to value,
                        ),
                    )
                },
                containerColor = FloconTheme.colorPalette.secondary
            ) {
                FloconIcon(
                    imageVector = Icons.AutoMirrored.Outlined.Send,
                )
            }
        }
    }
}

@Composable
@Preview
private fun DeeplinkFreeformItemViewPreview() {
    FloconTheme {
        DeeplinkFreeformItemView(
            modifier = Modifier.background(
                FloconTheme.colorPalette.primary,
            ),
            submit = { _, _ -> },
        )
    }
}
