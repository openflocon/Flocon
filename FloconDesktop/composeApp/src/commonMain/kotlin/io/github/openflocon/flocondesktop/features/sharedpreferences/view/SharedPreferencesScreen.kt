package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesViewModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
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
    rows: SharedPreferencesRowsStateUiModel,
    changeValue: (SharedPreferencesRowUiModel, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var sharedPrefRows by remember { mutableStateOf<List<SharedPreferencesRowUiModel>>(emptyList()) }

    FloconFeature(
        modifier = modifier.fillMaxSize()
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            selector = {
                SharedPrefSelectorView(
                    sharedPrefsState = deviceSharedPrefs,
                    onSharedPrefSelected = onSharedPrefSelected,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            filterBar = {
                SharedPreferencesFilterBar(
                    items = rows.rows,
                    onItemsChange = {
                        sharedPrefRows = it
                    },
                )
            }
        )
        SelectionContainer {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.background)
            ) {
                when (rows) {
                    SharedPreferencesRowsStateUiModel.Empty -> {}
                    SharedPreferencesRowsStateUiModel.Loading -> {}
                    is SharedPreferencesRowsStateUiModel.WithContent -> {
                        items(sharedPrefRows) {
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
