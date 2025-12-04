package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.previewCrashReportItem
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportDetailView
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportItemView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrashReporterScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: CrashReporterViewModel = koinViewModel()
    val crashReports by viewModel.crashReports.collectAsStateWithLifecycle()
    val selected by viewModel.selected.collectAsStateWithLifecycle()

    CrashReportScreen(
        modifier = Modifier.fillMaxSize(),
        crashReports = crashReports,
        selected = selected,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CrashReportScreen(
    modifier: Modifier = Modifier,
    onAction: (CrashReporterAction) -> Unit,
    crashReports: List<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
) {
    FloconFeature(
        modifier = modifier
    ) {
        Row(Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxHeight()
                    .weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(crashReports) { crash ->
                    CrashReportItemView(
                        crash = crash,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = {
                            onAction(CrashReporterAction.Select(it.id))
                        }
                    )
                }
            }
            selected?.let {
                CrashReportDetailView(
                    modifier = Modifier.fillMaxHeight()
                        .weight(3f),
                    crash = it,
                )
            }
        }
    }
}