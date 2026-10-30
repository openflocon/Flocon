package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.menu_item_feature_flags
import flocondesktop.composeapp.generated.resources.no_feature_flags
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagsViewModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagProvidersUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagsAction
import io.github.openflocon.flocondesktop.features.featureflags.model.previewFeatureFlagProvidersUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.previewFeatureFlagRowUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FeatureFlagsScreen(modifier: Modifier = Modifier) {
    val viewModel: FeatureFlagsViewModel = koinViewModel()
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val providersState by viewModel.providersState.collectAsStateWithLifecycle()
    val selectedStates by viewModel.selectedStates.collectAsStateWithLifecycle()

    FeatureFlagsScreen(
        selectedStates = selectedStates,
        onStateToggled = viewModel::onStateToggled,
        providersState = providersState,
        onProviderSelected = viewModel::onProviderSelected,
        rows = rows,
        filterText = viewModel.filterText,
        onFilterTextChanged = viewModel::onFilterTextChanged,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun FeatureFlagsScreen(
    providersState: FeatureFlagProvidersUiModel,
    onProviderSelected: (String) -> Unit,
    rows: ImmutableList<FeatureFlagRowUiModel>,
    selectedStates: Set<FeatureFlagStateUiModel>,
    onStateToggled: (FeatureFlagStateUiModel) -> Unit,
    filterText: State<String>,
    onFilterTextChanged: (String) -> Unit,
    onAction: (FeatureFlagsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier,
    ) {
        FloconPageTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
            trailing = { FeatureDocButton(doc = FeatureDoc.FeatureFlags) },
            selector = if (providersState is FeatureFlagProvidersUiModel.Empty) {
                null
            } else {
                {
                    FeatureFlagProviderSelector(
                        providersState = providersState,
                        onProviderSelected = onProviderSelected,
                    )
                }
            },
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = stringResource(Res.string.filter),
                    onTextChange = onFilterTextChanged,
                    modifier = Modifier.weight(1f),
                )
            },
            chips = {
                FeatureFlagStateFilters(
                    selectedStates = selectedStates,
                    onStateToggled = onStateToggled,
                )
            },
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .floconPanel()
        ) {
            if (providersState is FeatureFlagProvidersUiModel.Empty) {
                FeatureEmptyState(
                    doc = FeatureDoc.FeatureFlags,
                    modifier = Modifier.fillMaxSize(),
                )
            } else if (rows.isEmpty()) {
                Text(
                    text = stringResource(Res.string.no_feature_flags),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.ink.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = FloconTheme.spacing.small),
                ) {
                    itemsIndexed(
                        items = rows,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        FeatureFlagRow(
                            model = item,
                            onAction = onAction,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        if (index != rows.lastIndex) {
                            HorizontalDivider(modifier = Modifier.fillMaxWidth())
                        }
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier.fillMaxHeight()
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

@Composable
@Preview
private fun FeatureFlagsScreenPreview() {
    FloconTheme {
        FeatureFlagsScreen(
            providersState = previewFeatureFlagProvidersUiModel(),
            onProviderSelected = {},
            rows = persistentListOf(
                previewFeatureFlagRowUiModel("new_checkout"),
                previewFeatureFlagRowUiModel("dark_mode"),
            ),
            selectedStates = FeatureFlagStateUiModel.entries.toSet(),
            onStateToggled = {},
            filterText = remember { mutableStateOf("") },
            onFilterTextChanged = {},
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
