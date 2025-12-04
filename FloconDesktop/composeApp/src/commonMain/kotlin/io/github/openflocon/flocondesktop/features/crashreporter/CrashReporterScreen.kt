package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.composeunstyled.Text
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportDetailView
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportItemView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
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
            Surface(
                color = FloconTheme.colorPalette.primary,
                modifier = Modifier.fillMaxHeight()
                    .width(340.dp)
                    .clip(FloconTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.secondary,
                        shape = FloconTheme.shapes.medium
                    )
            ) {
                Column(
                    Modifier.fillMaxSize()
                        .padding(all = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "Crashes",
                        color = FloconTheme.colorPalette.onSurface,
                        style = FloconTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .padding(horizontal = 12.dp)
                    )
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(12.dp),
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
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                color = FloconTheme.colorPalette.primary,
                modifier = Modifier.fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.secondary,
                        shape = FloconTheme.shapes.medium
                    )
            ) {
                selected?.let {
                    CrashReportDetailView(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        crash = it,
                    )
                }
            }
        }
    }
}