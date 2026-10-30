package io.github.openflocon.flocondesktop.features.network.mock.edition.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.mock.MockEditionTarget
import io.github.openflocon.flocondesktop.features.network.mock.NetworkMockEditionViewModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.MockNetworkUiModel
import io.github.openflocon.flocondesktop.features.network.mock.edition.model.SelectedMockUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Creates or edits a mock, shown in the big dialog of the navigation. */
@Composable
fun NetworkMockEditionScreen(
    mockId: String?,
    fromNetworkCallId: String?,
    modifier: Modifier = Modifier,
) {
    val viewModel = koinViewModel<NetworkMockEditionViewModel>(key = mockId ?: fromNetworkCallId ?: "new") {
        parametersOf(MockEditionTarget(mockId = mockId, fromNetworkCallId = fromNetworkCallId))
    }
    val state by viewModel.state.collectAsStateWithLifecycle()

    // the editor starts from the first value: nothing is drawn until the mock is loaded
    state?.let {
        NetworkMockEditionScreen(
            state = it,
            onSave = viewModel::save,
            onCancel = viewModel::cancel,
            modifier = modifier,
        )
    }
}

@Composable
fun NetworkMockEditionScreen(
    state: SelectedMockUiModel,
    onSave: (MockNetworkUiModel) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MockEditorScreen(
        initialMock = state,
        onSave = onSave,
        onCancel = onCancel,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun NetworkMockEditionScreenPreview() {
    FloconTheme {
        NetworkMockEditionScreen(
            state = SelectedMockUiModel.Creation,
            onSave = {},
            onCancel = {},
        )
    }
}
