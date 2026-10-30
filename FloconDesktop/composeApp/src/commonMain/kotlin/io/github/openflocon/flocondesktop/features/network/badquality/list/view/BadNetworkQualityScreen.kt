package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.badquality.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.view.BadQualityEditionWindow
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.NetworkBadQualityLineUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.list.model.previewNetworkBadQualityLineUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.koin.compose.viewmodel.koinViewModel

/** The bad network configs, shown in the big dialog of the navigation. */
@Composable
fun BadNetworkQualityScreen(modifier: Modifier = Modifier) {
    val viewModel: BadQualityNetworkViewModel = koinViewModel()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val selectedConfig by viewModel.selectedItem.collectAsStateWithLifecycle()

    BadNetworkQualityScreen(
        lines = items,
        selected = selectedConfig,
        onItemClicked = viewModel::select,
        onAddItemClicked = viewModel::create,
        onDeleteClicked = viewModel::delete,
        setEnabled = viewModel::setEnabledElement,
        onSave = viewModel::save,
        onCloseEdition = viewModel::closeEdition,
        modifier = modifier,
    )
}

@Composable
fun BadNetworkQualityScreen(
    lines: ImmutableList<NetworkBadQualityLineUiModel>,
    selected: SelectedBadQualityUiModel?,
    onItemClicked: (id: String) -> Unit,
    onAddItemClicked: () -> Unit,
    onDeleteClicked: (id: String) -> Unit,
    setEnabled: (id: String?) -> Unit,
    onSave: (BadQualityConfigUiModel) -> Unit,
    onCloseEdition: () -> Unit,
    modifier: Modifier = Modifier,
) {
    NetworkBadQualityContent(
        lines = lines,
        onItemClicked = onItemClicked,
        onAddItemClicked = onAddItemClicked,
        onDeleteClicked = onDeleteClicked,
        setEnabled = setEnabled,
        modifier = modifier.fillMaxSize(),
    )

    selected?.let {
        BadQualityEditionWindow(
            onCloseRequest = onCloseEdition,
            save = onSave,
            state = it,
        )
    }
}

@Preview
@Composable
private fun BadNetworkQualityScreenPreview() {
    FloconTheme {
        BadNetworkQualityScreen(
            lines = persistentListOf(previewNetworkBadQualityLineUiModel()),
            selected = null,
            onItemClicked = {},
            onAddItemClicked = {},
            onDeleteClicked = {},
            setEnabled = {},
            onSave = {},
            onCloseEdition = {},
        )
    }
}
