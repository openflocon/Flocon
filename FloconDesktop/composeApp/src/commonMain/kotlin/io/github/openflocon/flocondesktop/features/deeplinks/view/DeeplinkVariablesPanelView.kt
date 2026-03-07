package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryEditable
import androidx.compose.material3.ExposedDropdownMenuBox
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeeplinkVariableChip(
    variable: DeeplinkVariableViewState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember(variable.name) { mutableStateOf(variable.value) }

    Row(
        modifier = modifier.background(
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

        when (val mode = variable.mode) {
            DeeplinkVariableViewState.Mode.Input -> {
                VariableInputField(
                    value = value,
                    placeholder = variable.name,
                    onValueChange = {
                        value = it
                        onValueChange(it)
                    },
                )
            }

            is DeeplinkVariableViewState.Mode.AutoComplete -> {
                var isExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                ) {
                    VariableInputField(
                        value = value,
                        placeholder = variable.name,
                        onValueChange = {
                            value = it
                            onValueChange(it)
                            isExpanded = mode.suggestions.isNotEmpty()
                        },
                        readOnly = true,
                        modifier = Modifier.menuAnchor(PrimaryEditable),
                    )

                    if (mode.suggestions.isNotEmpty()) {
                        ExposedDropdownMenu(
                            modifier = Modifier.widthIn(min = 150.dp),
                            expanded = isExpanded,
                            onDismissRequest = { isExpanded = false },
                        ) {
                            mode.suggestions.forEach { suggestion ->
                                Text(
                                    text = suggestion,
                                    style = FloconTheme.typography.bodySmall,
                                    modifier = Modifier.clickable {
                                        value = suggestion
                                        onValueChange(suggestion)
                                        isExpanded = false
                                    }
                                        .padding(
                                            vertical = 4.dp,
                                            horizontal = 8.dp
                                        )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VariableInputField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false
) {
    val isValueEmpty = value.isEmpty()

    Box(
        modifier = modifier.background(
            color = FloconTheme.colorPalette.primary,
            shape = RoundedCornerShape(2.dp),
        )
            .padding(horizontal = 4.dp, vertical = 2.dp)
            .width(IntrinsicSize.Min),
    ) {
        Text(
            text = placeholder,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.graphicsLayer { alpha = if (isValueEmpty) 1f else 0f },
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            maxLines = 1,
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colorPalette.onSurface,
                fontWeight = FontWeight.Bold,
            ),
            readOnly = readOnly,
            cursorBrush = SolidColor(FloconTheme.colorPalette.onSurface),
        )
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
                        value = "",
                        mode = DeeplinkVariableViewState.Mode.Input,
                    ),
                    DeeplinkVariableViewState(
                        name = "env",
                        description = null,
                        value = "staging",
                        mode =
                            DeeplinkVariableViewState.Mode.AutoComplete(
                                listOf("dev", "staging", "prod")
                            ),
                    ),
                    DeeplinkVariableViewState(
                        name = "token",
                        description = null,
                        value = "",
                        mode = DeeplinkVariableViewState.Mode.Input,
                    ),
                ),
            onVariableChanged = { _, _ -> },
        )
    }
}
