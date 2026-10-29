package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.preferences
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.sharedpreferences.SharedPreferencesViewModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPreferencesRowsStateUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.previewSharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedPreferencesScreen(modifier: Modifier = Modifier) {
    val viewModel: SharedPreferencesViewModel = koinViewModel()
    val deviceSharedPrefs by viewModel.sharedPrefs.collectAsStateWithLifecycle()
    val elementToEdit by viewModel.elementToEdit.collectAsStateWithLifecycle()
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val autoUpdateState by viewModel.autoUpdateState.collectAsStateWithLifecycle()

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
        autoUpdateState = autoUpdateState,
        onAutoUpdateChange = viewModel::onAutoUpdateChange,
        onAutoUpdateDelayChanged = viewModel::onAutoUpdateDelayChanged,
        refreshClicked = viewModel::refreshClicked,
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
    autoUpdateState: PreferenceAutoUpdate,
    onAutoUpdateChange: (Boolean) -> Unit,
    onAutoUpdateDelayChanged: (delay: Int) -> Unit,
    refreshClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filterText = remember { mutableStateOf("") }
    val sharedPrefRows: List<SharedPreferencesRowUiModel> = remember(rows, filterText.value) {
        if (filterText.value.isBlank()) {
            rows.rows
        } else {
            rows.rows.filter { it.contains(filterText.value) }
        }
    }
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier.fillMaxSize()
    ) {
        FloconPageTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
            title = stringResource(Res.string.preferences),
            headerActions = { FeatureDocButton(doc = FeatureDoc.SharedPreferences) },
            selector = if (deviceSharedPrefs is SharedPrefsStateUiModel.Empty) {
                null
            } else {
                {
                    SharedPreferenceSelector(
                        state = deviceSharedPrefs,
                        onSharedPrefSelected = onSharedPrefSelected,
                    )
                }
            },
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = stringResource(Res.string.filter),
                    onTextChange = { filterText.value = it },
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                PreferenceAutoUpdate(
                    state = autoUpdateState,
                    onChange = onAutoUpdateChange,
                    onAutoUpdateDelayChanged = onAutoUpdateDelayChanged,
                    refreshClicked = refreshClicked,
                )
            },
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colors.bg100)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colors.bg200,
                    shape = FloconTheme.shapes.medium
                )
        ) {
            SelectionContainer {
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (rows) {
                        SharedPreferencesRowsStateUiModel.Empty -> {}

                        SharedPreferencesRowsStateUiModel.Loading -> {}

                        is SharedPreferencesRowsStateUiModel.WithContent -> {
                            items(sharedPrefRows) {
                                SharedPreferenceRow(
                                    model = it,
                                    modifier = Modifier.fillMaxWidth(),
                                    onValueChanged = changeValue,
                                    onEditClicked = onEditClicked,
                                )
                            }
                        }
                    }
                }
            }
            if (deviceSharedPrefs is SharedPrefsStateUiModel.Empty) {
                FeatureEmptyState(
                    doc = FeatureDoc.SharedPreferences,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight()
                    .align(Alignment.TopEnd),
            )
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
            autoUpdateState = PreferenceAutoUpdate.Disabled,
            onAutoUpdateChange = {},
            onAutoUpdateDelayChanged = {},
            refreshClicked = {},
        )
    }
}
