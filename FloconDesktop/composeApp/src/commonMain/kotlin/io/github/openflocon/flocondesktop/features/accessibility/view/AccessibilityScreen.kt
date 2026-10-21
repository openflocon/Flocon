package io.github.openflocon.flocondesktop.features.accessibility.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_clear
import flocondesktop.composeapp.generated.resources.accessibility_filter
import flocondesktop.composeapp.generated.resources.no_accessibility_events
import io.github.openflocon.flocondesktop.features.accessibility.AccessibilityViewModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityAction
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityBannerUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityRowUiModel
import io.github.openflocon.flocondesktop.features.accessibility.model.previewAccessibilityRowUiModel
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun AccessibilityScreen(modifier: Modifier = Modifier) {
    val viewModel: AccessibilityViewModel = koinViewModel()
    val rows by viewModel.rows.collectAsStateWithLifecycle()
    val types by viewModel.types.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
    val banner by viewModel.banner.collectAsStateWithLifecycle()

    AccessibilityScreen(
        banner = banner,
        types = types,
        selectedType = selectedType,
        onTypeSelected = viewModel::onTypeSelected,
        rows = rows,
        filterText = viewModel.filterText,
        onFilterTextChanged = viewModel::onFilterTextChanged,
        onClearClicked = viewModel::onClearClicked,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
fun AccessibilityScreen(
    banner: AccessibilityBannerUiModel?,
    types: ImmutableList<String>,
    selectedType: String?,
    onTypeSelected: (String?) -> Unit,
    rows: ImmutableList<AccessibilityRowUiModel>,
    filterText: State<String>,
    onFilterTextChanged: (String) -> Unit,
    onClearClicked: () -> Unit,
    onAction: (AccessibilityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    FloconFeature(
        modifier = modifier,
    ) {
        // nothing until the device sends its state
        if (banner != null) {
            AccessibilityBannerView(
                banner = banner,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            filterBar = {
                FilterBar(
                    filterText = filterText,
                    placeholderText = stringResource(Res.string.accessibility_filter),
                    onTextChange = onFilterTextChanged,
                    modifier = Modifier.weight(1f),
                )
            },
            actions = {
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    tooltip = stringResource(Res.string.accessibility_clear),
                    onClick = onClearClicked,
                )
            },
        )

        AccessibilityTypeFilters(
            types = types,
            selectedType = selectedType,
            onTypeSelected = onTypeSelected,
            modifier = Modifier.fillMaxWidth(),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
                .border(
                    width = 1.dp,
                    color = FloconTheme.colorPalette.secondary,
                    shape = FloconTheme.shapes.medium
                )
        ) {
            if (rows.isEmpty()) {
                Text(
                    text = stringResource(Res.string.no_accessibility_events),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    itemsIndexed(
                        items = rows,
                        key = { _, item -> item.id },
                    ) { index, item ->
                        AccessibilityRowView(
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
private fun AccessibilityScreenPreview() {
    FloconTheme {
        AccessibilityScreen(
            banner = AccessibilityBannerUiModel.Running(
                services = listOf("TalkBack"),
                touchExploration = true,
            ),
            types = persistentListOf("ANNOUNCEMENT", "VIEW_FOCUSED"),
            selectedType = null,
            onTypeSelected = {},
            rows = persistentListOf(
                previewAccessibilityRowUiModel("1"),
                previewAccessibilityRowUiModel("2"),
            ),
            filterText = mutableStateOf(""),
            onFilterTextChanged = {},
            onClearClicked = {},
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
