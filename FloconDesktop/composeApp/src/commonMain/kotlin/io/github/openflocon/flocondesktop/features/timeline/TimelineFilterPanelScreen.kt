package io.github.openflocon.flocondesktop.features.timeline

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.timeline.view.TimelineFilterPanel
import org.koin.compose.viewmodel.koinViewModel

/**
 * The filters of the timeline, in a panel next to it.
 */
@Composable
internal fun TimelineFilterPanelScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: TimelineFilterPanelViewModel = koinViewModel()
    val panel by viewModel.panel.collectAsStateWithLifecycle()

    TimelineFilterPanel(
        panel = panel,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
