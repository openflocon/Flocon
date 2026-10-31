package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.menu_item_database
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.database.DatabaseViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.view.databases_tables.DatabaseFilterField
import io.github.openflocon.flocondesktop.features.database.view.databases_tables.DatabasesAndTables
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.navigation.mascot.mascotPlatform
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableSet
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DatabaseScreen(modifier: Modifier = Modifier) {
    val viewModel: DatabaseViewModel = koinViewModel()
    val deviceDataBases by viewModel.deviceDataBases.collectAsStateWithLifecycle()
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    val tabs by viewModel.tabs.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()

    DisposableEffect(viewModel) {
        viewModel.onVisible()
        onDispose {
            viewModel.onNotVisible()
        }
    }

    DatabaseScreen(
        deviceDataBases = deviceDataBases,
        favorites = favorites,
        onAction = viewModel::onAction,
        tabs = tabs,
        selectedTab = selectedTab,
        modifier = modifier,
    )
}

@Composable
fun DatabaseScreen(
    deviceDataBases: DatabasesStateUiModel,
    favorites: ImmutableList<DatabaseFavoriteQueryUiModel>,
    tabs: ImmutableList<DatabaseTabState>,
    selectedTab: DatabaseTabState?,
    onAction: (DatabaseScreenAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier.fillMaxSize()
    ) {
        if (deviceDataBases is DatabasesStateUiModel.Empty) {
            FloconPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .mascotPlatform(),
                trailing = { FeatureDocButton(doc = FeatureDoc.Database) },
            )
            FeatureEmptyState(
                doc = FeatureDoc.Database,
                modifier = Modifier
                    .fillMaxSize()
                    .floconPanel()
            )
        } else {
            var filter by rememberSaveable { mutableStateOf("") }
            val sidebarWidth = 340.dp
            val barHeight = FloconTheme.sizes.controlHeight + FloconTheme.spacing.small * 2
            val shape = remember(sidebarWidth, barHeight) { DatabaseLShape(sidebarWidth, barHeight) }

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
                        DatabaseFilterField(
                            value = filter,
                            onValueChange = { filter = it },
                            modifier = Modifier
                                .width(sidebarWidth)
                                .padding(horizontal = FloconTheme.spacing.small),
                        )
                        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                        DatabaseTabs(
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = FloconTheme.spacing.small),
                            tabs = tabs,
                            selected = selectedTab,
                            onAction = {
                                onAction(DatabaseScreenAction.OnTabAction(it))
                            },
                        )
                        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
                        ) {
                            FeatureDocButton(doc = FeatureDoc.Database)
                        }
                    }
                    Row(Modifier.fillMaxSize()) {
                        DatabasesAndTables(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(sidebarWidth),
                            state = deviceDataBases,
                            filter = filter,
                            favorites = favorites,
                            onAction = onAction
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .padding(
                                    start = FloconTheme.spacing.small,
                                    top = FloconTheme.spacing.small,
                                )
                        ) {
                            selectedTab?.let {
                                DatabaseTab(
                                    tab = it,
                                    favoritesTitles = remember(favorites) {
                                        favorites.map { it.title }.toImmutableSet()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private val DATABASE_L_CORNER_RADIUS = 12.dp

/**
 * Top bar and sidebar in a single block, an inverted L:
 * the bar spans the whole width, the sidebar goes down on the left,
 * with a concave corner where they meet.
 */
private class DatabaseLShape(
    private val sidebarWidth: Dp,
    private val barHeight: Dp,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val r = with(density) { DATABASE_L_CORNER_RADIUS.toPx() }
        val sw = with(density) { sidebarWidth.toPx() }
        val bh = with(density) { barHeight.toPx() }
        val w = size.width
        val h = size.height
        return Outline.Generic(
            Path().apply {
                moveTo(r, 0f)
                lineTo(w - r, 0f)
                quadraticTo(w, 0f, w, r)
                lineTo(w, bh - r)
                quadraticTo(w, bh, w - r, bh)
                lineTo(sw + r, bh)
                quadraticTo(sw, bh, sw, bh + r)
                lineTo(sw, h - r)
                quadraticTo(sw, h, sw - r, h)
                lineTo(r, h)
                quadraticTo(0f, h, 0f, h - r)
                lineTo(0f, r)
                quadraticTo(0f, 0f, r, 0f)
                close()
            }
        )
    }
}
