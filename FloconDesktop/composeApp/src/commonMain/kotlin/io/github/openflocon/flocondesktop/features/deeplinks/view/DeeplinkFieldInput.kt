package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.library.designsystem.FloconTheme

/** The input of a field of a deeplink, with the suggestions of the app when it declared some. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DeeplinkFieldInput(
    part: DeeplinkPart.TextField,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var isExpanded by remember { mutableStateOf(false) }
    val suggestions = remember(value, part.autoComplete) {
        part.autoComplete?.filter { it.contains(value, ignoreCase = true) }.orEmpty()
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded && suggestions.isNotEmpty(),
        onExpandedChange = { isExpanded = it },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .menuAnchor(PrimaryEditable)
                .fillMaxWidth()
                .background(FloconTheme.colors.bg200, FloconTheme.shapes.medium)
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.small),
        ) {
            if (value.isEmpty()) {
                Text(
                    text = part.label,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkFaint,
                )
            }
            BasicTextField(
                value = value,
                onValueChange = {
                    onValueChange(it)
                    isExpanded = true
                },
                singleLine = true,
                textStyle = FloconTheme.typography.bodySmall.copy(
                    color = FloconTheme.colors.ink,
                    fontWeight = FontWeight.Bold,
                ),
                cursorBrush = SolidColor(FloconTheme.colors.ink),
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (suggestions.isNotEmpty()) {
            ExposedDropdownMenu(
                modifier = Modifier.widthIn(min = 200.dp),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                suggestions.forEach { suggestion ->
                    Text(
                        text = suggestion,
                        style = FloconTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueChange(suggestion)
                                isExpanded = false
                            }
                            .padding(
                                vertical = FloconTheme.spacing.extraSmall,
                                horizontal = FloconTheme.spacing.small,
                            ),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun DeeplinkFieldInputPreview() {
    FloconTheme {
        DeeplinkFieldInput(
            part = DeeplinkPart.TextField("productId", autoComplete = listOf("4821", "9930")),
            value = "",
            onValueChange = {},
            modifier = Modifier.background(FloconTheme.colors.bg100),
        )
    }
}
