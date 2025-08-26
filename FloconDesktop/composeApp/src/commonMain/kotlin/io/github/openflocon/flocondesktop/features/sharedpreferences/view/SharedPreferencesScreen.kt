package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesViewModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedPreferencesScreen(modifier: Modifier = Modifier) {
    val viewModel: SharedPreferencesViewModel = koinViewModel()
    val deviceSharedPrefs by viewModel.sharedPrefs.collectAsStateWithLifecycle()
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }
    SharedPrefScreen(
        deviceSharedPrefs = deviceSharedPrefs,
        onSharedPrefSelected = viewModel::onSharedPrefsSelected,
        modifier = modifier,
        rows = rows,
        changeValue = viewModel::onValueChanged,
    )
}

@Composable
fun SharedPrefScreen(
    deviceSharedPrefs: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
    rows: SharedPreferencesRowsStateUiModel,
    changeValue: (SharedPreferencesRowUiModel, String) -> Unit,
) {
    FloconSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            FloconPageTopBar(
                modifier = Modifier.fillMaxWidth(),
                selector = {
                    SharedPrefSelectorView(
                        sharedPrefsState = deviceSharedPrefs,
                        onSharedPrefSelected = onSharedPrefSelected,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SelectionContainer {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    when (rows) {
                        SharedPreferencesRowsStateUiModel.Empty -> {}
                        SharedPreferencesRowsStateUiModel.Loading -> {}
                        is SharedPreferencesRowsStateUiModel.WithContent -> {
                            items(rows.rows) {
                                SharedPreferenceRowView(
                                    model = it,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChanged = changeValue,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun SharedPrefScreenPreview() {
    FloconTheme {
        SharedPrefScreen(
            deviceSharedPrefs = previewSharedPrefsStateUiModel(),
            onSharedPrefSelected = {},
            rows = previewSharedPreferencesRowsStateUiModel(),
            changeValue = { _, _ -> },
        )
    }
}
