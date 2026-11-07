package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_query_log_panel_transaction
import flocondesktop.composeapp.generated.resources.database_transaction_collapse
import flocondesktop.composeapp.generated.resources.database_transaction_committed
import flocondesktop.composeapp.generated.resources.database_transaction_expand
import flocondesktop.composeapp.generated.resources.database_transaction_open
import flocondesktop.composeapp.generated.resources.database_transaction_queries
import flocondesktop.composeapp.generated.resources.database_transaction_rolled_back
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel.Transaction.Status
import io.github.openflocon.flocondesktop.features.database.model.previewTransactionEntry
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val TimeWidth = 60.dp
private val ChevronSize = 16.dp

/**
 * The header row of a transaction: time, number of queries, duration and status. Click to show / hide its queries.
 */
@Composable
fun DatabaseTransactionLogItem(
    transaction: DatabaseQueryLogEntryUiModel.Transaction,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = FloconTheme.spacing.small)
            .then(if (transaction.isFromOldSession) Modifier.alpha(0.8f) else Modifier),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier.width(TimeWidth),
            text = transaction.dateFormatted,
            style = FloconTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        )
        Icon(
            modifier = Modifier.size(ChevronSize),
            imageVector = if (transaction.isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
            contentDescription = stringResource(
                if (transaction.isExpanded) Res.string.database_transaction_collapse else Res.string.database_transaction_expand,
            ),
            tint = FloconTheme.colors.ink,
        )
        Text(
            text = stringResource(Res.string.database_query_log_panel_transaction),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = "${transaction.queryCount} ${pluralStringResource(Res.plurals.database_transaction_queries, transaction.queryCount)}",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        )
        Text(
            text = transaction.durationLabel,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        )
        Text(
            text = stringResource(transaction.status.label()),
            style = FloconTheme.typography.bodySmall,
            color = transaction.status.color(),
        )
    }
}

private fun Status.label() = when (this) {
    Status.Committed -> Res.string.database_transaction_committed
    Status.RolledBack -> Res.string.database_transaction_rolled_back
    Status.Open -> Res.string.database_transaction_open
}

@Composable
private fun Status.color(): Color = when (this) {
    Status.Committed -> FloconTheme.colors.statusSuccess
    Status.RolledBack -> FloconTheme.colors.statusError
    Status.Open -> FloconTheme.colors.statusWarning
}

@Preview
@Composable
private fun DatabaseTransactionLogItemPreview() {
    FloconTheme {
        DatabaseTransactionLogItem(
            modifier = Modifier.fillMaxWidth(),
            transaction = previewTransactionEntry(isExpanded = false),
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun DatabaseTransactionLogItemRolledBackPreview() {
    FloconTheme {
        DatabaseTransactionLogItem(
            modifier = Modifier.fillMaxWidth(),
            transaction = previewTransactionEntry(status = Status.RolledBack),
            onClick = {},
        )
    }
}
