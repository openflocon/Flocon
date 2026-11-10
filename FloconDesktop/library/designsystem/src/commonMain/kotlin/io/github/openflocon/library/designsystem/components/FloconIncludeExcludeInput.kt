package io.github.openflocon.library.designsystem.components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Adds include / exclude rules: two segments to pick the side, then a text field where Enter adds the rule.
 */
@Composable
fun FloconIncludeExcludeInput(
    includeLabel: String,
    excludeLabel: String,
    placeholder: String,
    onSubmit: (text: String, exclude: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    var text by remember { mutableStateOf("") }
    var exclude by remember { mutableStateOf(false) }
    fun submit() {
        if (text.isNotBlank()) {
            onSubmit(text.trim(), exclude)
            text = ""
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .height(FloconTheme.sizes.pageBarControlHeight)
                .clip(FloconTheme.shapes.medium)
                .border(1.dp, colors.line, FloconTheme.shapes.medium)
                .padding(FloconTheme.spacing.tiny),
        ) {
            Segment(label = includeLabel, sign = "+", signColor = colors.statusSuccess, selected = !exclude, onClick = { exclude = false })
            Segment(label = excludeLabel, sign = "−", signColor = colors.statusError, selected = exclude, onClick = { exclude = true })
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .height(FloconTheme.sizes.pageBarControlHeight)
                .clip(FloconTheme.shapes.medium)
                .border(1.dp, colors.line, FloconTheme.shapes.medium)
                .padding(horizontal = FloconTheme.spacing.medium),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (text.isEmpty()) {
                Text(
                    text = placeholder,
                    style = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
                    color = colors.inkFaint,
                    maxLines = 1,
                )
            }
            BasicTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                textStyle = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace, color = colors.ink),
                cursorBrush = SolidColor(colors.accent),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { submit() }),
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyUp) {
                            submit()
                            true
                        } else {
                            false
                        }
                    },
            )
        }
    }
}

@Composable
private fun Segment(
    label: String,
    sign: String,
    signColor: Color,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val colors = FloconTheme.colors
    Row(
        modifier = Modifier
            .fillMaxHeight()
            .clip(FloconTheme.shapes.small)
            .then(if (selected) Modifier.background(colors.bg300) else Modifier)
            .clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        Text(text = sign, style = FloconTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = signColor)
        Text(
            text = label,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = if (selected) colors.ink else colors.inkMuted,
            maxLines = 1,
        )
    }
}

@Preview
@Composable
private fun FloconIncludeExcludeInputPreview() {
    FloconTheme {
        FloconIncludeExcludeInput(
            includeLabel = "Include",
            excludeLabel = "Exclude",
            placeholder = "Code or range: 404, 500-503",
            onSubmit = { _, _ -> },
            modifier = Modifier
                .background(FloconTheme.colors.bg100)
                .padding(FloconTheme.spacing.medium),
        )
    }
}
