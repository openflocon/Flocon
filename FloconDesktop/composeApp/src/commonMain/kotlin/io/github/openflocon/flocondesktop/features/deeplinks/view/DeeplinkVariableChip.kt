package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeeplinkVariableChip(
    variable: DeeplinkVariableViewState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember(variable.name) { mutableStateOf(variable.value) }

    Row(
        modifier = modifier.background(
            color = FloconTheme.colors.bg000,
            shape = RoundedCornerShape(4.dp),
        )
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
    ) {
        Text(
            text = "${variable.name}:",
            style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
            color = FloconTheme.colors.ink,
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
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            value = suggestion
                                            onValueChange(suggestion)
                                            isExpanded = false
                                        }
                                        .padding(
                                            vertical = FloconTheme.spacing.extraSmall,
                                            horizontal = FloconTheme.spacing.small
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
            color = FloconTheme.colors.bg100,
            shape = RoundedCornerShape(2.dp),
        ).padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
    ) {
        Text(
            text = placeholder,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink.copy(alpha = 0.4f),
            modifier = Modifier.graphicsLayer { alpha = if (isValueEmpty) 1f else 0f },
        )
        BasicTextField(
            modifier = Modifier.fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colors.ink,
                fontWeight = FontWeight.Bold,
            ),
            readOnly = readOnly,
            cursorBrush = SolidColor(FloconTheme.colors.ink),
        )
    }
}

@Composable
@Preview
private fun DeeplinkVariablesPanelPreview() {
    FloconTheme {
        Column(
            modifier = Modifier.background(FloconTheme.colors.bg100)
                .fillMaxWidth()
                .padding(FloconTheme.spacing.small)
        ) {
            DeeplinkVariableChip(
                variable = DeeplinkVariableViewState(
                    name = "userId",
                    description = "The user id",
                    value = "",
                    mode = DeeplinkVariableViewState.Mode.Input,
                ),
                onValueChange = {}
            )
            DeeplinkVariableChip(
                variable = DeeplinkVariableViewState(
                    name = "env",
                    description = null,
                    value = "staging",
                    mode =
                    DeeplinkVariableViewState.Mode.AutoComplete(
                        listOf("dev", "staging", "prod")
                    ),
                ),
                onValueChange = {}
            )
            DeeplinkVariableChip(
                variable = DeeplinkVariableViewState(
                    name = "token",
                    description = null,
                    value = "",
                    mode = DeeplinkVariableViewState.Mode.Input,
                ),
                onValueChange = {}
            )
        }
    }
}
