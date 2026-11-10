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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import coil3.compose.AsyncImage
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.copied_to_clipboard
import flocondesktop.composeapp.generated.resources.general_copy_all_content
import flocondesktop.composeapp.generated.resources.images_preview
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.copyToClipboard
import io.github.openflocon.library.designsystem.common.isImageUrl
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun DatabaseRowDetail(
    state: DetailResultItem,
    columns: List<String>,
    modifier: Modifier = Modifier,
) {
    val scrollState: ScrollState = rememberScrollState()

    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    val scope = rememberCoroutineScope()
    val feedbackDisplayer = koinInject<FeedbackDisplayer>()

    Box(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .background(FloconTheme.colors.bg000)
                    .padding(horizontal = 1.dp) // to display the inner border
                    .border(1.dp, FloconTheme.colors.bg100)
                    .padding(vertical = FloconTheme.spacing.medium)
                    .padding(horizontal = FloconTheme.spacing.small),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top,
            ) {
                DetailHeaderButton(
                    text = stringResource(Res.string.general_copy_all_content),
                    onClick = {
                        copyToClipboard(
                            state.toCopiableText(
                                columns = columns,
                            )
                        )
                        scope.launch {
                            feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
                        }
                    },
                    imageVector = Icons.Outlined.CopyAll,
                )
            }

            SelectionContainer(
                Modifier.fillMaxSize(),
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.doubleExtraLarge),
                ) {
                    itemsIndexed(columns) { index, column ->
                        DetailItem(
                            column = column,
                            copyValue = {
                                copyToClipboard(it)
                                scope.launch {
                                    feedbackDisplayer.displayMessage(getString(Res.string.copied_to_clipboard))
                                }
                            },
                            value = state.item.items.getOrNull(index),
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 18.dp)
                        )

                        if (index != columns.lastIndex) {
                            FloconHorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = FloconTheme.spacing.small),
                                color = FloconTheme.colors.bg000,
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
private fun DetailItem(
    column: String,
    value: String?,
    copyValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val contentColor = FloconTheme.colors.ink

    val valueOrNull = value ?: "NULL"
    val valueColor = if (value == null) {
        FloconTheme.colors.ink.copy(alpha = 0.4f)
    } else {
        FloconTheme.colors.ink
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
                    .padding(bottom = FloconTheme.spacing.tiny) // beware of this
                    .size(24.dp)
                    .padding(FloconTheme.spacing.tiny)
                    .clip(FloconTheme.shapes.small)
                    .clickable(enabled = value != null) {
                        value?.let {
                            copyValue(it)
                        }
                    }
            ) {
                Image(
                    imageVector = Icons.Outlined.CopyAll,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(valueColor),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        // copy button

        Spacer(modifier = Modifier.height(FloconTheme.spacing.tiny))

        val isImageUrl = remember(value) {
            value?.isImageUrl() == true
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = FloconTheme.spacing.extraSmall)
                .background(
                    shape = FloconTheme.shapes.medium,
                    color = FloconTheme.colors.bg000,
                )
                .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
            style = FloconTheme.typography.bodyMedium,
            text = valueOrNull,
            color = valueColor,
        )

        if (isImageUrl) {
            // Coil image
            AsyncImage(
                model = valueOrNull,
                contentDescription = stringResource(Res.string.images_preview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FloconTheme.spacing.extraSmall)
                    .height(300.dp),
                contentScale = ContentScale.Fit,
            )
        }
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
                color = FloconTheme.colors.ink,
            ).clickable(onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            style = FloconTheme.typography.bodySmall,
            text = text,
            color = FloconTheme.colors.ink,
        )
        Box(
            modifier = Modifier
                .size(12.dp)
        ) {
            Image(
                imageVector = imageVector,
                contentDescription = null,
                colorFilter = ColorFilter.tint(FloconTheme.colors.ink),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

// copy it as markdown
private fun DetailResultItem.toCopiableText(
    columns: List<String>,
): String = buildString {
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
