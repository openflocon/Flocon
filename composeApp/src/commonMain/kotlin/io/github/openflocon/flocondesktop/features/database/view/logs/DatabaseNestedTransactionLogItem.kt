package io.github.openflocon.flocondesktop.features.database.view.logs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_transaction_collapse
import flocondesktop.composeapp.generated.resources.database_transaction_committed
import flocondesktop.composeapp.generated.resources.database_transaction_expand
import flocondesktop.composeapp.generated.resources.database_transaction_nested
import flocondesktop.composeapp.generated.resources.database_transaction_open
import flocondesktop.composeapp.generated.resources.database_transaction_queries
import flocondesktop.composeapp.generated.resources.database_transaction_rolled_back
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel.Transaction.Item
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogEntryUiModel.Transaction.Status
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource

private val ChevronSize = 16.dp

/**
 * The line of a transaction nested in another one (a savepoint): number of queries, duration and status.
 * Click to show / hide its queries.
 */
@Composable
fun DatabaseNestedTransactionLogItem(
    nested: Item.Nested,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier.size(ChevronSize),
            imageVector = if (nested.isExpanded) Icons.Default.ExpandMore else Icons.Default.ChevronRight,
            contentDescription = stringResource(
                if (nested.isExpanded) Res.string.database_transaction_collapse else Res.string.database_transaction_expand,
            ),
            tint = FloconTheme.colors.ink,
        )
        Text(
            text = stringResource(Res.string.database_transaction_nested),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
        )
        Text(
            text = "${nested.queryCount} ${pluralStringResource(Res.plurals.database_transaction_queries, nested.queryCount)}",
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        )
        Text(
            text = nested.durationLabel,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink.copy(alpha = 0.6f),
        )
        Text(
            text = stringResource(nested.status.label()),
            style = FloconTheme.typography.bodySmall,
            color = nested.status.color(),
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
private fun DatabaseNestedTransactionLogItemPreview() {
    FloconTheme {
        DatabaseNestedTransactionLogItem(
            nested = Item.Nested(
                id = "nested-1",
                durationLabel = "3 ms",
                queryCount = 2,
                status = Status.Committed,
                isExpanded = true,
                depth = 0,
            ),
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
