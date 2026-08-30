package io.github.openflocon.flocondesktop.features.network.badquality.list.view

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.network.badquality.BadQualityNetworkViewModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.view.BadNetworkQualityEditionContent
import io.github.openflocon.flocondesktop.features.network.badquality.edition.view.BadQualityEditionWindow
import io.github.openflocon.library.designsystem.components.FloconDialog
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun BadNetworkQualityScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: BadQualityNetworkViewModel = koinViewModel()
    val items by viewModel.items.collectAsStateWithLifecycle()
    val selectedConfig by viewModel.selectedItem.collectAsStateWithLifecycle()

    val currentSelected = selectedConfig
    if (currentSelected != null) {
        BadNetworkQualityEditionContent(
            state = currentSelected,
            save = viewModel::save,
            close = viewModel::closeEdition,
            modifier = modifier.fillMaxSize(),
        )
    } else {
        NetworkBadQualityContent(
            lines = items,
            modifier = modifier.fillMaxSize(),
            onItemClicked = viewModel::select,
            onAddItemClicked = viewModel::create,
            onDeleteClicked = viewModel::delete,
            setEnabled = viewModel::setEnabledElement,
        )
    }
}

@Composable
fun BadNetworkQualityWindow(
    onCloseRequest: () -> Unit,
) {
    FloconDialog(
        onDismissRequest = onCloseRequest,
    ) {
        BadNetworkQualityContent(
            onCloseRequest = onCloseRequest,
        )
    }
}

@Composable
private fun BadNetworkQualityContent(
    onCloseRequest: () -> Unit,
) {
    val viewModel: BadQualityNetworkViewModel = koinViewModel()
    val items by viewModel.items.collectAsStateWithLifecycle()

    val viewModelEvent by viewModel.events.collectAsStateWithLifecycle(null)
    LaunchedEffect(viewModelEvent) {
        when (viewModelEvent) {
            BadQualityNetworkViewModel.Event.Close -> onCloseRequest()
            null -> {}
        }
    }

    NetworkBadQualityContent(
        lines = items,
        modifier = Modifier.fillMaxWidth(),
        onItemClicked = viewModel::select,
        onAddItemClicked = viewModel::create,
        onDeleteClicked = viewModel::delete,
        setEnabled = viewModel::setEnabledElement,
    )

    val selectedConfig by viewModel.selectedItem.collectAsStateWithLifecycle()
    selectedConfig?.let {
        BadQualityEditionWindow(
            onCloseRequest = viewModel::closeEdition,
            save = viewModel::save,
            state = it,
        )
    }
}
