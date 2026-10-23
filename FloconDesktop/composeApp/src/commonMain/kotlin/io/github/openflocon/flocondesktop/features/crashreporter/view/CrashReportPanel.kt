package io.github.openflocon.flocondesktop.features.crashreporter.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.crashreporter.CrashReporterPanelViewModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun CrashReportPanel(
    crashId: String,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<CrashReporterPanelViewModel> {
        parametersOf(crashId)
    }
    val crash by viewModel.crash.collectAsStateWithLifecycle()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(FloconTheme.colors.bg100)
    ) {
        crash?.let {
            CrashReportDetail(
                modifier = Modifier.fillMaxSize(),
                crash = it,
                onCopy = viewModel::copy,
                onDelete = { viewModel.delete() },
            )
        }
    }
}
