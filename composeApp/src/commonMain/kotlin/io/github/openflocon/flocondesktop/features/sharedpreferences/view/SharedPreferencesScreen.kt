package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.clip
import flocondesktop.composeapp.generated.resources.preferences_sidebar_filter_placeholder
import io.github.openflocon.flocondesktop.common.ui.LShape
import io.github.openflocon.flocondesktop.features.database.view.databases_tables.DatabaseFilterField
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Inbox
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.preferences_clear_filter
import flocondesktop.composeapp.generated.resources.preferences_file_empty_subtitle
import flocondesktop.composeapp.generated.resources.preferences_file_empty_title
import flocondesktop.composeapp.generated.resources.preferences_no_match_title
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
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconEmptyState
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedPreferencesScreen(modifier: Modifier = Modifier) {
    val viewModel: SharedPreferencesViewModel = koinViewModel()
    val deviceSharedPrefs by viewModel.sharedPrefs.collectAsStateWithLifecycle()
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
        onEditClicked = viewModel::onEditClicked,
        onCopyClicked = viewModel::onCopyClicked,
        onDeleteClicked = viewModel::onDeleteClicked,
        autoUpdateState = autoUpdateState,
        onAutoUpdateChange = viewModel::onAutoUpdateChange,
        onAutoUpdateDelayChanged = viewModel::onAutoUpdateDelayChanged,
        refreshClicked = viewModel::refreshClicked,
    )
}

@Composable
fun SharedPrefScreen(
    deviceSharedPrefs: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    rows: SharedPreferencesRowsStateUiModel,
    changeValue: (SharedPreferencesRowUiModel, String) -> Unit,
    onEditClicked: (row: SharedPreferencesRowUiModel) -> Unit,
    onCopyClicked: (row: SharedPreferencesRowUiModel) -> Unit,
    onDeleteClicked: (row: SharedPreferencesRowUiModel) -> Unit,
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
    // UI only: the collapsible rows (json, long text) currently expanded, by key
    var expandedKeys by remember(deviceSharedPrefs) { mutableStateOf(emptySet<String>()) }
    // UI only: the row waiting for the delete confirmation
    var rowToDelete by remember { mutableStateOf<SharedPreferencesRowUiModel?>(null) }
    val lazyListState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)

    if (deviceSharedPrefs is SharedPrefsStateUiModel.Empty) {
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = modifier.fillMaxSize()
        ) {
            FloconPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .mascotPlatform(),
                trailing = { FeatureDocButton(doc = FeatureDoc.SharedPreferences) },
            )
            FeatureEmptyState(
                doc = FeatureDoc.SharedPreferences,
                modifier = Modifier
                    .fillMaxSize()
                    .floconPanel(),
            )
        }
    } else {
        var sidebarFilter by rememberSaveable { mutableStateOf("") }
        val sidebarWidth = 340.dp
        val barHeight = FloconTheme.sizes.controlHeight + FloconTheme.spacing.small * 2
        val shape = remember(sidebarWidth, barHeight) { LShape(sidebarWidth, barHeight) }

        Box(modifier.fillMaxSize()) {
            // the L is only the background: the content on the right is outside of it
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape)
                    .background(FloconTheme.colors.islandSidebar)
                    .border(1.dp, FloconTheme.colors.line, shape)
            )
            Column(Modifier.fillMaxSize()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .mascotPlatform(),
                ) {
                    DatabaseFilterField(
                        value = sidebarFilter,
                        onValueChange = { sidebarFilter = it },
                        placeholder = stringResource(Res.string.preferences_sidebar_filter_placeholder),
                        modifier = Modifier
                            .width(sidebarWidth)
                            .padding(horizontal = FloconTheme.spacing.small),
                    )
                    FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                    FilterBar(
                        filterText = filterText,
                        placeholderText = stringResource(Res.string.filter),
                        onTextChange = { filterText.value = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = FloconTheme.spacing.small),
                    )
                    FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
                        modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
                    ) {
                        PreferenceAutoUpdate(
                            state = autoUpdateState,
                            onChange = onAutoUpdateChange,
                            onAutoUpdateDelayChanged = onAutoUpdateDelayChanged,
                            refreshClicked = refreshClicked,
                        )
                        FeatureDocButton(doc = FeatureDoc.SharedPreferences)
                    }
                }
                Row(Modifier.fillMaxSize()) {
                    SharedPreferencesSidebar(
                        state = deviceSharedPrefs,
                        filter = sidebarFilter,
                        onSharedPrefSelected = onSharedPrefSelected,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(sidebarWidth),
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(
                                start = FloconTheme.spacing.small,
                                top = FloconTheme.spacing.small,
                            )
                            .floconPanel()
                    ) {
                        SharedPreferencesHeader()
                        Box(Modifier.weight(1f).fillMaxWidth()) {
                            LazyColumn(
                                state = lazyListState,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(sharedPrefRows, key = { it.key }) {
                                    SharedPreferenceRow(
                                        model = it,
                                        isExpanded = it.key in expandedKeys,
                                        onToggleExpanded = { key ->
                                            expandedKeys = if (key in expandedKeys) expandedKeys - key else expandedKeys + key
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        onValueChanged = changeValue,
                                        onEditClicked = onEditClicked,
                                        onCopyClicked = onCopyClicked,
                                        onDeleteClicked = { row -> rowToDelete = row },
                                    )
                                }
                            }
                            if (rows !is SharedPreferencesRowsStateUiModel.Loading && rows.rows.isEmpty() && deviceSharedPrefs is SharedPrefsStateUiModel.WithContent) {
                                SharedPreferencesEmptyState(
                                    title = stringResource(Res.string.preferences_file_empty_title, deviceSharedPrefs.selected.name),
                                    subtitle = stringResource(Res.string.preferences_file_empty_subtitle),
                                    icon = Icons.Outlined.Inbox,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            } else if (filterText.value.isNotBlank() && rows.rows.isNotEmpty() && sharedPrefRows.isEmpty()) {
                                SharedPreferencesEmptyState(
                                    title = stringResource(Res.string.preferences_no_match_title, filterText.value),
                                    subtitle = null,
                                    icon = Icons.Outlined.Search,
                                    modifier = Modifier.fillMaxSize(),
                                    onClearFilter = { filterText.value = "" },
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
            }
        }
    }

    rowToDelete?.let { row ->
        SharedPreferenceDeleteDialog(
            key = row.key,
            onConfirm = {
                onDeleteClicked(row)
                rowToDelete = null
            },
            onDismiss = { rowToDelete = null },
        )
    }
}

@Composable
private fun SharedPreferencesEmptyState(
    title: String,
    subtitle: String?,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClearFilter: (() -> Unit)? = null,
) {
    FloconEmptyState(
        title = title,
        description = subtitle.orEmpty(),
        modifier = modifier,
        illustration = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .background(FloconTheme.colors.bg300, RoundedCornerShape(12.dp))
                    .border(1.dp, FloconTheme.colors.lineStrong, RoundedCornerShape(12.dp)),
            ) {
                FloconIcon(icon, tint = FloconTheme.colors.inkMuted, modifier = Modifier.size(24.dp))
            }
        },
        action = onClearFilter?.let {
            {
                FloconButton(onClick = it) {
                    Text(stringResource(Res.string.preferences_clear_filter))
                }
            }
        },
    )
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
            onEditClicked = {},
            onCopyClicked = {},
            onDeleteClicked = {},
            autoUpdateState = PreferenceAutoUpdate.Disabled,
            onAutoUpdateChange = {},
            onAutoUpdateDelayChanged = {},
            refreshClicked = {},
        )
    }
}
