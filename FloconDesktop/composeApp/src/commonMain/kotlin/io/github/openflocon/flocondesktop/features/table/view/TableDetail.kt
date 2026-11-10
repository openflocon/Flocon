package io.github.openflocon.flocondesktop.features.table.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.detail_columns
import flocondesktop.composeapp.generated.resources.detail_copy_columns
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.table.TableDetailViewModel
import io.github.openflocon.flocondesktop.features.table.model.TableDetailUiModel
import io.github.openflocon.flocondesktop.features.table.model.previewTableDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun TableDetail(
    id: String
) {
    val viewModel = koinViewModel<TableDetailViewModel>(key = id) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopyColumns = viewModel::onCopyColumns,
        )
    }
}

@Composable
private fun Content(
    uiState: TableDetailUiModel,
    onCopyColumns: () -> Unit,
) {
    val entries = remember(uiState.columns) { uiState.columns.map { it.name to it.value } }
    DetailPanel(
        header = {
            DetailHeader(
                label = uiState.tableName,
                title = uiState.title,
                chips = {
                    DetailChip(text = uiState.dateFormatted)
                },
            )
        },
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.detail_columns),
            badge = entries.size.toString(),
            copyContentDescription = stringResource(Res.string.detail_copy_columns),
            onCopy = onCopyColumns.takeIf { entries.isNotEmpty() },
        ) {
            DetailKeyValueList(entries = entries)
        }
    }
}

@Preview
@Composable
private fun TableDetailPreview() {
    FloconTheme {
        Content(
            uiState = previewTableDetailUiModel(),
            onCopyColumns = {},
        )
    }
}
