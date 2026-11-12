package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.common.ui.LShape
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.deeplinks.DeepLinkViewModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenAction
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkScreenState
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkTabUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.koin.compose.viewmodel.koinViewModel

private val SidebarWidth = 340.dp

@Composable
fun DeeplinkScreen(modifier: Modifier = Modifier) {
    val viewModel: DeepLinkViewModel = koinViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()

    DeeplinkScreen(
        state = state,
        freeformLink = viewModel.freeformLink,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

/**
 * Same layout as the database screen: the filter, the tabs and the doc button in the top bar,
 * the deeplinks under the filter, both drawn as one L-shaped block. The selected tab fills the rest.
 */
@Composable
private fun DeeplinkScreen(
    state: DeeplinkScreenState,
    freeformLink: String,
    onAction: (DeeplinkScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        if (state.showEmptyState) {
            FloconPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .mascotPlatform(),
                trailing = { FeatureDocButton(doc = FeatureDoc.Deeplinks) },
            )
            FeatureEmptyState(
                doc = FeatureDoc.Deeplinks,
                modifier = Modifier
                    .fillMaxSize()
                    .floconPanel(),
            )
        } else {
            var filter by rememberSaveable { mutableStateOf("") }
            val barHeight = FloconTheme.sizes.controlHeight + FloconTheme.spacing.small * 2
            val shape = remember(barHeight) { LShape(SidebarWidth, barHeight) }

            Box(Modifier.fillMaxSize()) {
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
                        DeeplinkFilterField(
                            value = filter,
                            onValueChange = { filter = it },
                            modifier = Modifier
                                .width(SidebarWidth)
                                .padding(horizontal = FloconTheme.spacing.small),
                        )
                        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                        DeeplinkTabs(
                            tabs = state.tabs,
                            selected = state.selectedTab,
                            onAction = onAction,
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = FloconTheme.spacing.small),
                        )
                        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
                        ) {
                            FeatureDocButton(doc = FeatureDoc.Deeplinks)
                        }
                    }
                    Row(Modifier.fillMaxSize()) {
                        DeeplinkSidebar(
                            deepLinks = state.deepLinks,
                            variablesCount = state.variables.size,
                            selectedTabId = state.selectedTab.id,
                            filter = filter,
                            onAction = onAction,
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(SidebarWidth),
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    start = FloconTheme.spacing.small,
                                    top = FloconTheme.spacing.small,
                                )
                                .floconPanel()
                        ) {
                            DeeplinkDetail(
                                state = state,
                                freeformLink = freeformLink,
                                onAction = onAction,
                                modifier = Modifier.fillMaxSize(),
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
private fun DeeplinkScreenPreview() {
    FloconTheme {
        DeeplinkScreen(
            state = DeeplinkScreenState(
                deepLinks = listOf(
                    previewDeeplinkViewState(),
                    previewDeeplinkViewState().copy(
                        deeplinkId = 1L,
                        label = "Open a profile",
                        parts = listOf(
                            DeeplinkPart.Text("myapp://profile/"),
                            DeeplinkPart.Variable(value = "{userId}", name = "userId", isResolved = false),
                        ),
                    ),
                    previewDeeplinkViewState().copy(deeplinkId = 2L, label = null, isHistory = true),
                ),
                variables = listOf(
                    DeeplinkVariableViewState(
                        name = "userId",
                        description = null,
                        value = "",
                    ),
                    DeeplinkVariableViewState(
                        name = "env",
                        description = null,
                        value = "staging",
                        mode = DeeplinkVariableViewState.Mode.AutoComplete(listOf("dev", "staging", "prod")),
                    ),
                ),
                tabs = listOf(
                    DeeplinkTabUiModel.Freeform,
                    DeeplinkTabUiModel.Link(deeplinkId = 1L, isHistory = false, title = "Open a profile"),
                ),
                selectedTab = DeeplinkTabUiModel.Freeform,
            ),
            freeformLink = "",
            onAction = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
