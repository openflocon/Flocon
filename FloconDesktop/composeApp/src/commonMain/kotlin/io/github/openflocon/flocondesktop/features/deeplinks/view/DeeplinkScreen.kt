package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.deeplinks.DeepLinkViewModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconFeature
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeeplinkScreen(modifier: Modifier = Modifier) {
    val viewModel: DeepLinkViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    DeeplinkScreen(
        state = state,
        submit = viewModel::submit,
        removeFromHistory = viewModel::removeFromHistory,
        setVariable = viewModel::setVariable,
        modifier = modifier,
    )
}

@Composable
private fun DeeplinkScreen(
    state: DeeplinkScreenState,
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    removeFromHistory: (DeeplinkViewState) -> Unit,
    setVariable: (name: String, value: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val variableValues by remember(state.variables) {
        derivedStateOf { state.variables.associate { it.name to it.value } }
    }
    val deepLinks by remember(state.deepLinks) {
        derivedStateOf { state.deepLinks.filter { !it.isHistory } }
    }
    val history by remember(state.deepLinks) { derivedStateOf { state.deepLinks.filter { it.isHistory } } }

    FloconFeature(modifier = modifier) {
        // Top bar: freeform input only
        FloconPageTopBar(
            modifier = Modifier.fillMaxWidth(),
            filterBar = {
                DeeplinkFreeformItemView(
                    submit = submit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        )

        // Main body — two rows of equal weight
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Top half: variables (left) | deeplinks (right)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
            ) {
                // Left: Variables
                DeeplinkPanel(
                    title = "Variables",
                    modifier = Modifier
                        .weight(0.3f)
                        .fillMaxHeight()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary),
                ) {
                    DeeplinkVariablesPanelView(
                        variables = state.variables,
                        onVariableChanged = setVariable,
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                    )
                }

                // Right: Deeplinks (non-history)
                DeeplinkScrollablePanel(
                    title = "Deeplinks",
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .clip(FloconTheme.shapes.medium)
                        .background(FloconTheme.colorPalette.primary),
                ) {
                    items(deepLinks) { item ->
                        DeeplinkItemView(
                            submit = submit,
                            removeFromHistory = removeFromHistory,
                            item = item,
                            variableValues = variableValues,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }

            // Bottom half: History
            DeeplinkScrollablePanel(
                title = "History",
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxWidth()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colorPalette.primary)
            ) {
                items(history) { item ->
                    DeeplinkItemView(
                        submit = submit,
                        removeFromHistory = removeFromHistory,
                        item = item,
                        variableValues = variableValues,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

/** Panel with a header label and freeform content slot. */
@Composable
private fun DeeplinkPanel(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Column(modifier = modifier) {
        DeeplinkPanelHeader(title)
        content()
    }
}

/** Panel with a header label and a scrollable LazyColumn content slot. */
@Composable
private fun DeeplinkScrollablePanel(
    title: String,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit,
) {
    val listState = rememberLazyListState()
    val scrollAdapter = rememberFloconScrollbarAdapter(listState)

    Column(modifier = modifier) {
        DeeplinkPanelHeader(title)
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(all = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                content = content,
            )
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight().align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
private fun DeeplinkPanelHeader(title: String) {
    Text(
        text = title,
        style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
        color = FloconTheme.colorPalette.onPrimary,
        modifier = Modifier
            .fillMaxWidth()
            .background(FloconTheme.colorPalette.primary)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    )
    HorizontalDivider(color = FloconTheme.colorPalette.surface)
}

@Composable
@Preview
private fun DeeplinkScreenPreview() {
    FloconTheme {
        DeeplinkScreen(
            state =
                DeeplinkScreenState(
                    deepLinks = listOf(
                        previewDeeplinkViewState(),
                        previewDeeplinkViewState(),
                        previewDeeplinkViewState().copy(isHistory = true),
                        previewDeeplinkViewState().copy(isHistory = true),
                    ),
                    variables =
                        listOf(
                            DeeplinkVariableViewState(
                                name = "userId",
                                description = null,
                                value = ""
                            ),
                            DeeplinkVariableViewState(
                                name = "env",
                                description = null,
                                value = "staging",
                                mode =
                                    DeeplinkVariableViewState.Mode
                                        .AutoComplete(
                                            listOf(
                                                "dev",
                                                "staging",
                                                "prod"
                                            )
                                        ),
                            ),
                        ),
                ),
            submit = { _, _ -> },
            removeFromHistory = {},
            setVariable = { _, _ -> },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
