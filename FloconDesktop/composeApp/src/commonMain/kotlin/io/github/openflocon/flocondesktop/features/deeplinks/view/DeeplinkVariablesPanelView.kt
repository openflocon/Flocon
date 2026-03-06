package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeeplinkVariablesPanelView(
        variables: List<DeeplinkVariableViewState>,
        onVariableChanged: (name: String, value: String) -> Unit,
        modifier: Modifier = Modifier,
) {
    if (variables.isEmpty()) return

    FlowRow(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        variables.forEach { variable ->
            DeeplinkVariableChip(
                    variable = variable,
                    onValueChange = { onVariableChanged(variable.name, it) },
            )
        }
    }
}

@Composable
private fun DeeplinkVariableChip(
        variable: DeeplinkVariableViewState,
        onValueChange: (String) -> Unit,
        modifier: Modifier = Modifier,
) {
    var value by remember(variable.name) { mutableStateOf(variable.value) }
    val isValueEmpty = value.isEmpty()

    Row(
            modifier =
                    modifier.background(
                                    color = FloconTheme.colorPalette.surface,
                                    shape = RoundedCornerShape(4.dp),
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
                text = "${variable.name}:",
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                color = FloconTheme.colorPalette.onSurface,
        )

        Box(
                modifier =
                        Modifier.background(
                                        color = FloconTheme.colorPalette.primary,
                                        shape = RoundedCornerShape(2.dp),
                                )
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .width(IntrinsicSize.Min),
        ) {
            Text(
                    text = if (isValueEmpty) variable.name else value,
                    style = FloconTheme.typography.bodySmall,
                    color =
                            FloconTheme.colorPalette.onSurface.copy(
                                    alpha = if (isValueEmpty) 0.4f else 0f
                            ),
                    modifier = Modifier.graphicsLayer { alpha = if (isValueEmpty) 1f else 0f },
            )
            BasicTextField(
                    value = value,
                    onValueChange = {
                        value = it
                        onValueChange(it)
                    },
                    maxLines = 1,
                    textStyle =
                            FloconTheme.typography.bodySmall.copy(
                                    color = FloconTheme.colorPalette.onSurface,
                                    fontWeight = FontWeight.Bold,
                            ),
                    cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
            )
        }
    }
}

@Composable
@Preview
private fun DeeplinkVariablesPanelViewPreview() {
    FloconTheme {
        DeeplinkVariablesPanelView(
                modifier =
                        Modifier.background(FloconTheme.colorPalette.primary)
                                .fillMaxWidth()
                                .padding(8.dp),
                variables =
                        listOf(
                                DeeplinkVariableViewState(
                                        name = "userId",
                                        description = "The user id",
                                        value = ""
                                ),
                                DeeplinkVariableViewState(
                                        name = "env",
                                        description = null,
                                        value = "staging"
                                ),
                                DeeplinkVariableViewState(
                                        name = "token",
                                        description = null,
                                        value = ""
                                ),
                        ),
                onVariableChanged = { _, _ -> },
        )
    }
}
