package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun SharedPreferencesScreen(modifier: Modifier = Modifier) {
    val viewModel: SharedPreferencesViewModel = koinViewModel()
    val deviceSharedPrefs by viewModel.sharedPrefs.collectAsStateWithLifecycle()
    val elementToEdit by viewModel.elementToEdit.collectAsStateWithLifecycle()
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
        onEditClicked = { row, stringValue ->
            viewModel.onEditClicked(row, stringValue)
        },
    )

    elementToEdit?.let {
        SharedPreferenceEditScreen(
            row = it.row,
            stringValue = it.stringValue,
            cancel = {
                viewModel.cancelEdition()
            },
            save = { row, stringValue ->
                viewModel.onEditDone(row, stringValue)
            }
        )
    }
}

@Composable
fun SharedPrefScreen(
    deviceSharedPrefs: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    rows: SharedPreferencesRowsStateUiModel,
    changeValue: (SharedPreferencesRowUiModel, String) -> Unit,
    onEditClicked: (row: SharedPreferencesRowUiModel, stringValue: SharedPreferencesRowUiModel.Value.StringValue) -> Unit,
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
            val lazyListState = rememberLazyListState()
            val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
            Row(
                modifier = modifier.fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.secondary,
                        shape = FloconTheme.shapes.medium
                    )
            ) {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier
                        .fillMaxWidth()
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
                                    onEditClicked = onEditClicked,
                                )
                            }
                        }
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier.fillMaxHeight(),
                )
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
            onEditClicked = { _, _ -> },
        )
    }
}
