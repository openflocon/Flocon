package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_query_log_panel_arguments
import flocondesktop.composeapp.generated.resources.database_query_log_panel_copy_arguments
import flocondesktop.composeapp.generated.resources.database_query_log_panel_copy_sql
import flocondesktop.composeapp.generated.resources.database_query_log_panel_sql
import flocondesktop.composeapp.generated.resources.database_query_log_panel_transaction
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.database.DatabaseQueryLogPanelViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseQueryLogPanelUiModel
import io.github.openflocon.flocondesktop.features.database.model.previewDatabaseQueryLogPanelUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseQueryLogPanel(
    id: Int
) {
    val viewModel = koinViewModel<DatabaseQueryLogPanelViewModel>(key = id.toString()) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopySql = viewModel::onCopySql,
            onCopyArguments = viewModel::onCopyArguments,
        )
    }
}

@Composable
private fun Content(
    uiState: DatabaseQueryLogPanelUiModel,
    onCopySql: () -> Unit,
    onCopyArguments: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = uiState.dbName,
                title = uiState.rawSql,
                chips = {
                    DetailChip(text = uiState.dateFormatted)
                    if (uiState.isTransaction) {
                        DetailChip(text = stringResource(Res.string.database_query_log_panel_transaction))
                    }
                },
            )
        },
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.database_query_log_panel_sql),
            badge = null,
            copyContentDescription = stringResource(Res.string.database_query_log_panel_copy_sql),
            onCopy = onCopySql,
        ) {
            SelectionContainer {
                Text(
                    text = uiState.sql,
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.ink,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(FloconTheme.spacing.medium),
                )
            }
        }
        if (uiState.arguments.isNotEmpty()) {
            val entries = remember(uiState.arguments) {
                uiState.arguments.mapIndexed { index, value -> "${index + 1}" to value }
            }
            DetailSectionCard(
                title = stringResource(Res.string.database_query_log_panel_arguments),
                badge = entries.size.toString(),
                copyContentDescription = stringResource(Res.string.database_query_log_panel_copy_arguments),
                onCopy = onCopyArguments,
            ) {
                DetailKeyValueList(entries = entries)
            }
        }
    }
}

@Preview
@Composable
private fun DatabaseQueryLogPanelPreview() {
    FloconTheme {
        Content(
            uiState = previewDatabaseQueryLogPanelUiModel(),
            onCopySql = {},
            onCopyArguments = {},
        )
    }
}
