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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.displayTextOf
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.valueForInput
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkSuggestionUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.persistentListOf

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
    // a picked suggestion with a label is displayed with it, the value stays what goes in the link
    val displayedText = remember(value, part.suggestions) { part.suggestions.displayTextOf(value) }
    val suggestions = remember(value, part.suggestions) {
        // the field holds a value, which can be the one of a labelled suggestion: show every suggestion then
        val selected = part.suggestions?.firstOrNull { it.value == value }
        part.suggestions
            ?.filter { selected != null || it.value.contains(value, ignoreCase = true) || it.text.contains(value, ignoreCase = true) }
            .orEmpty()
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
                .deeplinkInputBox(missing = value.isEmpty()),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (displayedText.isEmpty()) {
                Text(
                    text = part.label,
                    style = FloconTheme.typography.listItem,
                    color = FloconTheme.colors.inkFaint,
                )
            }
            BasicTextField(
                value = displayedText,
                onValueChange = {
                    onValueChange(part.suggestions.valueForInput(it))
                    isExpanded = true
                },
                singleLine = true,
                textStyle = FloconTheme.typography.listItem.copy(color = FloconTheme.colors.ink),
                cursorBrush = SolidColor(FloconTheme.colors.ink),
                modifier = Modifier
                    .fillMaxWidth()
                    // the anchor does not open the list on a tap: open it when the field gets the focus
                    .onFocusChanged { if (it.isFocused) isExpanded = true },
            )
        }
        if (suggestions.isNotEmpty()) {
            ExposedDropdownMenu(
                modifier = Modifier.widthIn(min = 200.dp),
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
            ) {
                suggestions.forEach { suggestion ->
                    DeeplinkSuggestionItem(
                        suggestion = suggestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onValueChange(suggestion.value)
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
            part = DeeplinkPart.TextField(
                label = "productId",
                suggestions = persistentListOf(
                    previewDeeplinkSuggestionUiModel(value = "4821", label = "Running shoes", isDefault = true),
                    previewDeeplinkSuggestionUiModel(value = "9930"),
                ),
            ),
            value = "4821",
            onValueChange = {},
            modifier = Modifier.background(FloconTheme.colors.bg100),
        )
    }
}
