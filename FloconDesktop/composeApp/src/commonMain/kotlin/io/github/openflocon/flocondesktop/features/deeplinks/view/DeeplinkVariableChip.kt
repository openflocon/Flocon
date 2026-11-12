package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExpandMore
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_value_required
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import org.jetbrains.compose.resources.stringResource

// the name before an input, so the inputs of a group line up
internal val DeeplinkInputNameWidth = 90.dp
private val ArrowSize = 16.dp

/** The box of an input of the deeplinks: [missing] draws its border in the warning color. */
@Composable
internal fun Modifier.deeplinkInputBox(missing: Boolean): Modifier = this
    .heightIn(min = FloconTheme.sizes.controlHeight)
    .clip(FloconTheme.shapes.medium)
    .background(FloconTheme.colors.bg200)
    .border(
        width = 1.dp,
        color = if (missing) FloconTheme.colors.statusWarning else FloconTheme.colors.line,
        shape = FloconTheme.shapes.medium,
    )
    .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.compact)

/** A shared variable: its name, then its value (an input, or a list when the app declared suggestions). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeeplinkVariableChip(
    variable: DeeplinkVariableViewState,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember(variable.name) { mutableStateOf(variable.value) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Text(
            text = variable.name,
            style = FloconTheme.typography.listItem,
            fontWeight = FontWeight.SemiBold,
            color = FloconTheme.colors.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.width(DeeplinkInputNameWidth),
        )

        when (val mode = variable.mode) {
            DeeplinkVariableViewState.Mode.Input -> {
                VariableInputField(
                    value = value,
                    onValueChange = {
                        value = it
                        onValueChange(it)
                    },
                    modifier = Modifier.weight(1f),
                )
            }

            is DeeplinkVariableViewState.Mode.AutoComplete -> {
                var isExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it },
                    modifier = Modifier.weight(1f),
                ) {
                    VariableInputField(
                        value = value,
                        onValueChange = {
                            value = it
                            onValueChange(it)
                            isExpanded = mode.suggestions.isNotEmpty()
                        },
                        readOnly = true,
                        showArrow = true,
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
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    showArrow: Boolean = false,
) {
    val isValueEmpty = value.isEmpty()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .deeplinkInputBox(missing = isValueEmpty),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Box(
            modifier = Modifier.weight(1f),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = stringResource(Res.string.deeplink_value_required),
                style = FloconTheme.typography.listItem,
                color = FloconTheme.colors.inkFaint,
                maxLines = 1,
                modifier = Modifier.graphicsLayer { alpha = if (isValueEmpty) 1f else 0f },
            )
            BasicTextField(
                modifier = Modifier.fillMaxWidth(),
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = FloconTheme.typography.listItem.copy(color = FloconTheme.colors.ink),
                readOnly = readOnly,
                cursorBrush = SolidColor(FloconTheme.colors.ink),
            )
        }
        if (showArrow) {
            FloconIcon(
                imageVector = Icons.Outlined.ExpandMore,
                tint = FloconTheme.colors.inkMuted,
                modifier = Modifier.size(ArrowSize),
            )
        }
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
