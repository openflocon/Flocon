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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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
    val (categoryColor, categorySoft) = TimelineSource.Database.categoryColors()

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
        Box(modifier = Modifier.width(FloconTheme.sizes.categoryColumn)) {
            EventBadge(source = TimelineSource.Database)
        }
        Row(
            modifier = Modifier
                .weight(1f)
                // the tint of the database events: the header is read as part of them, not as a grey bar
                .background(categorySoft, FloconTheme.shapes.medium)
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
                color = categoryColor,
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
 * The branch of a query of an expanded transaction: the query is indented, a line along the queries of the transaction
 * curves towards it. The line goes on below the query unless it is the [isLast] one of its transaction.
 */
@Composable
internal fun Modifier.transactionBranch(isLast: Boolean): Modifier {
    val color = FloconTheme.colors.inkFaint
    val spacing = FloconTheme.spacing
    val indent = spacing.large + spacing.small
    val lineX = spacing.compact + spacing.small / 2
    val radius = spacing.small
    val elbowEnd = spacing.compact + indent - BranchGap
    return this
        .drawBehind {
            val x = lineX.toPx()
            val r = radius.toPx()
            val centerY = size.height / 2
            val stroke = Stroke(width = BranchLineWidth.toPx(), cap = StrokeCap.Round)
            // the line comes from the header, and goes on to the next query of the transaction
            drawLine(
                color = color,
                start = Offset(x, 0f),
                end = Offset(x, if (isLast) centerY - r else size.height),
                strokeWidth = stroke.width,
            )
            // the curve towards the query
            drawPath(
                path = Path().apply {
                    moveTo(x, centerY - r)
                    quadraticTo(x, centerY, x + r, centerY)
                    lineTo(elbowEnd.toPx(), centerY)
                },
                color = color,
                style = stroke,
            )
        }
        .padding(start = indent)
}

private val BranchLineWidth = 1.dp
private val BranchGap = 2.dp

@Preview
@Composable
private fun TimelineTransactionRowPreview() {
    FloconTheme {
        Box(modifier = Modifier.fillMaxWidth()) {
            TimelineTransactionRow(item = previewTimelineTransactionHeader(isExpanded = false), onClick = {})
        }
    }
}
