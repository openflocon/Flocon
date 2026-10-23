package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.auto_update
import flocondesktop.composeapp.generated.resources.database_run_query
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.theme.FloconColors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentSetOf
import org.jetbrains.compose.resources.stringResource

private val QueryCardShape = RoundedCornerShape(12.dp)

interface SqlColorPalette {
    val keywordColor: Color
    val stringColor: Color
    val numberColor: Color
    val functionColor: Color
    val textColor: Color
    val comments: Color
}

// the colors of the design system: keywords in accent, numbers in the network color, strings in the table one
@Immutable
class FloconSqlPalette(colors: FloconColors) : SqlColorPalette {
    override val keywordColor = colors.accent
    override val stringColor = colors.catTable
    override val numberColor = colors.catNetwork
    override val functionColor = colors.catNetwork
    override val textColor = colors.inkMuted
    override val comments = colors.inkFaint
}

class ColorsTransformation(
    val colorPalette: SqlColorPalette,
) : VisualTransformation {

    private fun buildAnnotatedStringWithColors(text: AnnotatedString): AnnotatedString = buildAnnotatedString {
        val sqlKeywords = listOf(
            "SELECT", "FROM", "WHERE", "AND", "OR", "INSERT", "INTO", "VALUES",
            "UPDATE", "SET", "DELETE", "CREATE", "TABLE", "DROP", "ALTER",
            "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AS", "DISTINCT",
            "GROUP", "BY", "ORDER", "LIMIT", "HAVING"
        )

        // Regex to split the SQL text into tokens:
        // - block comments /* ... */
        // - string literals '...'
        // - numbers
        // - words and whitespace
        // - any other characters
        val regex = Regex(
            """(/\*[\s\S]*?\*/|'[^']*'|\b\d+\b|\b\w+\b|\s+|.)""",
            RegexOption.MULTILINE
        )

        val matches = regex.findAll(text.text)
        for (match in matches) {
            val token = match.value
            val upper = token.uppercase()

            when {
                // Block comment: /* ... */
                token.startsWith("/*") && token.endsWith("*/") -> withStyle(
                    SpanStyle(color = colorPalette.comments)
                ) { append(token) }

                // String literal: '...'
                token.startsWith("'") && token.endsWith("'") -> withStyle(
                    SpanStyle(color = colorPalette.stringColor)
                ) { append(token) }

                // Numeric literal
                token.matches(Regex("\\d+")) -> withStyle(
                    SpanStyle(color = colorPalette.numberColor)
                ) { append(token) }

                // SQL keyword
                sqlKeywords.contains(upper.trim()) -> withStyle(
                    SpanStyle(color = colorPalette.keywordColor, fontWeight = FontWeight.Bold)
                ) { append(token) }

                // Default text color
                else -> withStyle(
                    SpanStyle(color = colorPalette.textColor)
                ) { append(token) }
            }
        }
    }

    override fun filter(text: AnnotatedString): TransformedText = TransformedText(
        buildAnnotatedStringWithColors(text),
        OffsetMapping.Identity
    )
}

@Composable
fun DatabaseQuery(
    query: String,
    autoUpdate: Boolean,
    favoritesTitles: ImmutableSet<String>,
    lastQueries: ImmutableList<String>,
    updateQuery: (query: String) -> Unit,
    onAction: (action: DatabaseTabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    Column(
        modifier = modifier
            .background(
                color = colors.bg100,
                shape = QueryCardShape
            )
            .border(1.dp, colors.line, QueryCardShape)
            .padding(FloconTheme.spacing.medium),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        DatabaseQueryToolbar(
            favoritesTitles = favoritesTitles,
            onAction = onAction,
            lastQueries = lastQueries,
            modifier = Modifier.fillMaxWidth(),
            isQueryEmpty = query.isBlank(),
        )

        FloconTextField(
            value = query,
            onValueChange = updateQuery,
            singleLine = false,
            minLines = 5,
            maxLines = 10,
            textStyle = FloconTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            containerColor = colors.bg200,
            visualTransformation = remember(colors) { ColorsTransformation(FloconSqlPalette(colors)) },
            modifier = Modifier.fillMaxWidth()
                .border(1.dp, colors.line, FloconTheme.shapes.medium)
                .onKeyEvent { keyEvent ->
                    // detect CMD + Enter
                    if (keyEvent.type == KeyEventType.KeyDown &&
                        keyEvent.key == androidx.compose.ui.input.key.Key.Enter &&
                        (keyEvent.isMetaPressed || keyEvent.isCtrlPressed)
                    ) {
                        onAction(DatabaseTabAction.ExecuteQuery(query))

                        // Return 'true' to indicate that the event was consumed
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val isEnabled = query.isNotBlank()
            FloconButton(
                onClick = {
                    if (isEnabled) {
                        onAction(DatabaseTabAction.ExecuteQuery(query))
                    }
                },
                containerColor = colors.accent,
                modifier = Modifier
                    .height(FloconTheme.sizes.controlHeight)
                    .graphicsLayer {
                        if (!isEnabled) alpha = 0.45f
                    }
            ) {
                val contentColor = colors.accentInk
                Row(
                    Modifier.padding(horizontal = FloconTheme.spacing.small),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                    Text(
                        stringResource(Res.string.database_run_query),
                        color = contentColor,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FloconCheckbox(
                    checked = autoUpdate,
                    onCheckedChange = {
                        onAction(DatabaseTabAction.UpdateAutoUpdate(it))
                    },
                )
                Text(
                    stringResource(Res.string.auto_update),
                    color = colors.inkMuted,
                    style = FloconTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Preview
@Composable
private fun DatabaseQueryPreview() {
    FloconTheme {
        DatabaseQuery(
            modifier = Modifier.fillMaxWidth(),
            query = "SELECT * FROM TABLE_NAME",
            updateQuery = {},
            autoUpdate = true,
            onAction = {},
            favoritesTitles = persistentSetOf(),
            lastQueries = persistentListOf(),
        )
    }
}
