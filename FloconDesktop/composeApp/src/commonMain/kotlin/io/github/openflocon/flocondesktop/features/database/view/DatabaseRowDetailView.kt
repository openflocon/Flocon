package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.composeunstyled.Text
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.koin.compose.koinInject

@Composable
fun DatabaseRowDetailView(
    state: DetailResultItem,
    modifier: Modifier = Modifier,
    columns: List<String>,
) {
    val scrollState: ScrollState = rememberScrollState()

    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    val feedbackDisplayer = koinInject<FeedbackDisplayer>()

    Box(
        modifier
            .background(FloconTheme.colorPalette.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .background(FloconTheme.colorPalette.surface)
                    .padding(horizontal = 1.dp) // to display the inner border
                    .border(1.dp, FloconTheme.colorPalette.primary)
                    .padding(vertical = 12.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
            ) {
                DetailHeaderButton(
                    text = "Copy all content",
                    onClick = {
                        copyToClipboard(
                            state.toCopiableText(
                                columns = columns,
                            )
                        )
                        feedbackDisplayer.displayMessage("Copied")
                    },
                    imageVector = Icons.Outlined.CopyAll,
                )
            }

            SelectionContainer(
                Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
                ) {
                    itemsIndexed(columns) { index, column ->
                        DetailItemView(
                            column = column,
                            copyValue = {
                                copyToClipboard(it)
                                feedbackDisplayer.displayMessage("Copied")
                            },
                            value = state.item.items.getOrNull(index),
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 18.dp)
                        )

                        if (index != columns.lastIndex) {
                            FloconHorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                color = FloconTheme.colorPalette.surface,
                            )
                        }
                    }
                }
            }
        }

        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun DetailItemView(
    column: String,
    value: String?,
    copyValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = FloconTheme.colorPalette.onSurface

    val valueOrNull = value ?: "NULL"
    val valueColor = if (value == null) {
        FloconTheme.colorPalette.onSurface.copy(alpha = 0.4f)
    } else {
        FloconTheme.colorPalette.onSurface
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                text = column,
                color = contentColor,
            )

            Box(
                modifier = Modifier
                    .padding(bottom = 2.dp) // beware of this
                    .size(24.dp)
                    .padding(2.dp)
                    .clip(FloconTheme.shapes.small)
                    .clickable(enabled = value != null) {
                        value?.let {
                            copyValue(it)
                        }
                    }) {
                Image(
                    imageVector = Icons.Outlined.CopyAll,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(valueColor),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // copy button

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp)
                .background(
                    shape = FloconTheme.shapes.medium,
                    color = FloconTheme.colorPalette.surface,
                )
                .padding(horizontal = 8.dp, vertical = 4.dp),
            style = FloconTheme.typography.bodyMedium,
            text = valueOrNull,
            color = valueColor,
        )
    }
}

@Composable
private fun DetailHeaderButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = FloconTheme.shapes.large
    Row(
        modifier = modifier.clip(shape)
            .border(
                width = 0.5.dp,
                shape = shape,
                color = FloconTheme.colorPalette.onPrimary,
            ).clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            style = FloconTheme.typography.bodySmall,
            text = text,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Box(
            modifier = Modifier
                .size(12.dp)
        ) {
            Image(
                imageVector = imageVector,
                contentDescription = null,
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}


// copy it as markdown
private fun DetailResultItem.toCopiableText(
    columns: List<String>,
): String {
    return buildString {
        columns.fastForEachIndexed { index, column ->
            append("### ")
            append(column)
            append(": ")
            append("\n")
            append("\n")
            appendLine(item.items.getOrNull(index) ?: "NULL")

            if (index != columns.lastIndex) {
                append("\n")
                appendLine("--------")
                append("\n")
            }
        }
    }
}
