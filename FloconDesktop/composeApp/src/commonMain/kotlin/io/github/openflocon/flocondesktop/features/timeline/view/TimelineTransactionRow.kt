package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_query_log_panel_transaction
import flocondesktop.composeapp.generated.resources.database_transaction_collapse
import flocondesktop.composeapp.generated.resources.database_transaction_expand
import flocondesktop.composeapp.generated.resources.database_transaction_queries
import io.github.openflocon.domain.timeline.models.TimelineSource
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineItemUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.previewTimelineTransactionHeader
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val ChevronSize = 18.dp
private val IndentLineWidth = 1.dp

/**
 * The queries run in the same transaction, under one row: time | chevron | what it holds | database.
 * A click shows or hides its queries.
 */
@Composable
internal fun TimelineTransactionRow(
    item: TimelineItemUiModel.TransactionHeader,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = FloconTheme.colors
    val spacing = FloconTheme.spacing
    val transaction = item.transaction

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clickable(onClick = onClick)
            .padding(horizontal = spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(spacing.small),
    ) {
        TimeColumn(
            time = item.time,
            delta = null,
            modifier = Modifier.width(FloconTheme.sizes.timeColumn),
        )
        Spacer(Modifier.width(railGutter()))
        Row(
            modifier = Modifier
                .weight(1f)
                .background(colors.bg200.copy(alpha = HeaderAlpha), FloconTheme.shapes.medium)
                .padding(horizontal = spacing.compact, vertical = spacing.tiny),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(spacing.small),
        ) {
            Icon(
                imageVector = if (item.isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
                contentDescription = stringResource(
                    if (item.isExpanded) Res.string.database_transaction_collapse else Res.string.database_transaction_expand,
                ),
                tint = colors.inkMuted,
                modifier = Modifier.size(ChevronSize),
            )
            Text(
                text = stringResource(Res.string.database_query_log_panel_transaction),
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = TimelineSource.Database.categoryColors().first,
                maxLines = 1,
            )
            Text(
                text = "${transaction.queryCount} ${pluralStringResource(Res.plurals.database_transaction_queries, transaction.queryCount)}" +
                    " · ${transaction.durationLabel}",
                style = monoStyle(small = true),
                color = colors.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            item.databaseName?.let { TagPill(text = it) }
        }
    }
}

/**
 * The line along the queries of an expanded transaction: they are indented under their header.
 */
@Composable
internal fun Modifier.transactionIndent(): Modifier {
    val color = FloconTheme.colors.bg300
    val spacing = FloconTheme.spacing
    // under the middle of the chevron of the header
    val lineStart = FloconTheme.sizes.timeColumn + spacing.compact + railGutter() + spacing.small + spacing.compact + ChevronSize / 2
    return this
        .drawBehind {
            val x = lineStart.toPx()
            drawLine(color = color, start = Offset(x, 0f), end = Offset(x, size.height), strokeWidth = IndentLineWidth.toPx())
        }
        .padding(start = spacing.large)
}

private const val HeaderAlpha = 0.5f

@Preview
@Composable
private fun TimelineTransactionRowPreview() {
    FloconTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            TimelineTransactionRow(item = previewTimelineTransactionHeader(isExpanded = false), onClick = {})
        }
    }
}
