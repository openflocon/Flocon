package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.deeplinks.DeepLinkViewModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeeplinkScreen(modifier: Modifier = Modifier) {
    val viewModel: DeepLinkViewModel = koinViewModel()
    val deepLinks by viewModel.deepLinks.collectAsStateWithLifecycle()

    DeeplinkScreen(
        deepLinks = deepLinks,
        submit = viewModel::submit,
        removeFromHistory = viewModel::removeFromHistory,
        modifier = modifier,
    )
}

@Composable
private fun DeeplinkScreen(
    deepLinks: List<DeeplinkViewState>,
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    removeFromHistory: (DeeplinkViewState) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    FloconFeature(
        modifier = modifier
    ) {
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            filterBar = {
                DeeplinkFreeformItemView(
                    submit = submit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(FloconTheme.shapes.medium)
                .background(FloconTheme.colorPalette.primary)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                itemsIndexed(deepLinks) { index, item ->
                    DeeplinkItemView(
                        submit = submit,
                        removeFromHistory = removeFromHistory,
                        item = item,
                        modifier = Modifier.fillMaxWidth(),
                    )
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

@Composable
@Preview
private fun DeeplinkScreenPreview() {
    FloconTheme {
        DeeplinkScreen(
            deepLinks = listOf(
                previewDeeplinkViewState(),
                previewDeeplinkViewState(),
                previewDeeplinkViewState(),
            ),
            submit = { _, _ -> },
            modifier = Modifier.fillMaxSize(),
            removeFromHistory = {},
        )
    }
}
