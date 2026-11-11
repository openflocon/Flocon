package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * The link as it will be sent: the variables are green when they have a value and orange when they miss one,
 * the fields show what was typed, or their name while they are empty.
 */
@Composable
internal fun DeeplinkLinkText(
    parts: List<DeeplinkPart>,
    fieldValues: Map<DeeplinkPart.TextField, String>,
    modifier: Modifier = Modifier,
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
                        SpanStyle(
                            color = if (part.isResolved) resolved else missing,
                            fontWeight = FontWeight.Bold,
                        )
                    ) { append(part.value) }
                }
            }
        },
        style = FloconTheme.typography.bodyMedium.copy(
            fontFamily = FontFamily.Monospace,
            color = FloconTheme.colors.ink,
        ),
        modifier = modifier,
    )
}

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
