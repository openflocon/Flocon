package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import io.github.openflocon.flocondesktop.features.deeplinks.mapper.freeformVariableRanges
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.library.designsystem.FloconTheme

// the soft background behind a variable, like the status pills
private const val VariableBackgroundAlpha = 0.14f

/** The monospaced style of a link. */
@Composable
internal fun deeplinkLinkTextStyle(): TextStyle = FloconTheme.typography.bodyLarge.copy(
    fontFamily = FontFamily.Monospace,
    color = FloconTheme.colors.ink,
)

/**
 * The link as it will be sent: the variables are green when they have a value and yellow when they miss one,
 * the fields show what was typed, or their name while they are empty.
 */
@Composable
internal fun DeeplinkLinkText(
    parts: List<DeeplinkPart>,
    fieldValues: Map<DeeplinkPart.TextField, String>,
    modifier: Modifier = Modifier,
    style: TextStyle = deeplinkLinkTextStyle(),
) {
    val resolved = FloconTheme.colors.statusSuccess
    val missing = FloconTheme.colors.statusWarning
    val muted = FloconTheme.colors.inkMuted

    Text(
        text = buildAnnotatedString {
            parts.forEach { part ->
                when (part) {
                    is DeeplinkPart.Text -> append(part.value)

                    is DeeplinkPart.TextField -> {
                        val value = fieldValues[part].orEmpty()
                        if (value.isEmpty()) {
                            withStyle(SpanStyle(color = muted)) { append("[${part.label}]") }
                        } else {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(value) }
                        }
                    }

                    is DeeplinkPart.Variable -> withStyle(
                        variableStyle(color = if (part.isResolved) resolved else missing),
                    ) { append(part.value) }
                }
            }
        },
        style = style,
        modifier = modifier,
    )
}

/** Colors the declared variables written {name} in the free link, as [DeeplinkLinkText] does. */
@Composable
internal fun rememberFreeformLinkHighlight(variables: List<DeeplinkVariableViewState>): VisualTransformation {
    val resolved = FloconTheme.colors.statusSuccess
    val missing = FloconTheme.colors.statusWarning
    return remember(variables, resolved, missing) {
        VisualTransformation { text ->
            val ranges = freeformVariableRanges(text.text, variables)
            val styled = AnnotatedString(
                text = text.text,
                spanStyles = ranges.map { (range, isResolved) ->
                    AnnotatedString.Range(
                        item = variableStyle(color = if (isResolved) resolved else missing),
                        start = range.first,
                        end = range.last + 1,
                    )
                },
            )
            TransformedText(styled, OffsetMapping.Identity)
        }
    }
}

private fun variableStyle(color: Color) = SpanStyle(
    color = color,
    fontWeight = FontWeight.SemiBold,
    background = color.copy(alpha = VariableBackgroundAlpha),
)

@Composable
@Preview
private fun DeeplinkLinkTextPreview() {
    FloconTheme {
        DeeplinkLinkText(
            parts = listOf(
                DeeplinkPart.Text("myapp://product/"),
                DeeplinkPart.TextField("productId"),
                DeeplinkPart.Text("?user="),
                DeeplinkPart.Variable(value = "{userId}", name = "userId", isResolved = false),
                DeeplinkPart.Text("&env="),
                DeeplinkPart.Variable(value = "staging", name = "env"),
            ),
            fieldValues = emptyMap(),
            modifier = Modifier.background(FloconTheme.colors.bg100),
        )
    }
}
