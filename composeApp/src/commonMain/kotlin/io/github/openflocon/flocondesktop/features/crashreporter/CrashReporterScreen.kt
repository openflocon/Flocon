package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.crash_clear_all_tooltip
import flocondesktop.composeapp.generated.resources.crash_copy_trace
import flocondesktop.composeapp.generated.resources.crash_copy_trace_tooltip
import flocondesktop.composeapp.generated.resources.crash_delete_selected_tooltip
import flocondesktop.composeapp.generated.resources.crash_no_result
import io.github.openflocon.flocondesktop.common.ui.LShape
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashFilterField
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportBody
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportItem
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconVerticalDivider
import io.github.openflocon.library.designsystem.components.WithTooltip
import io.github.openflocon.library.designsystem.components.floconPanel
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CrashReporterScreen(
    modifier: Modifier = Modifier,
) {
    val viewModel: CrashReporterViewModel = koinViewModel()
    val crashReports = viewModel.crashReports.collectAsLazyPagingItems()
    val selected by viewModel.selected.collectAsStateWithLifecycle()
    val showEmptyState by viewModel.showEmptyState.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()

    CrashReportScreen(
        modifier = Modifier.fillMaxSize(),
        crashReports = crashReports,
        selected = selected,
        filter = filter,
        showEmptyState = showEmptyState,
        onAction = viewModel::onAction,
    )
}

private val SidebarWidth = 320.dp

/**
 * Same layout as the database screen: the search field, the crash header and its actions in the top bar,
 * the list of crashes under the search field, both drawn as one L-shaped block.
 */
@Composable
private fun CrashReportScreen(
    onAction: (CrashReporterAction) -> Unit,
    crashReports: LazyPagingItems<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
    filter: String,
    showEmptyState: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
    ) {
        if (showEmptyState) {
            FloconPageTopBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .mascotPlatform(),
                trailing = { FeatureDocButton(doc = FeatureDoc.CrashReporter) },
            )
            FeatureEmptyState(
                doc = FeatureDoc.CrashReporter,
                modifier = Modifier
                    .fillMaxSize()
                    .floconPanel()
            )
        } else {
            // nothing selected yet: the most recent crash
            val firstId = if (crashReports.itemCount > 0) crashReports.peek(0)?.id else null
            LaunchedEffect(selected == null, firstId) {
                if (selected == null && firstId != null) {
                    onAction(CrashReporterAction.Select(firstId))
                }
            }

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
                    TopBar(
                        barHeight = barHeight,
                        filter = filter,
                        selected = selected,
                        onAction = onAction,
                    )
                    Row(Modifier.fillMaxSize()) {
                        CrashList(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(SidebarWidth),
                            crashReports = crashReports,
                            filter = filter,
                            selected = selected,
                            onAction = onAction,
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
                            selected?.let {
                                CrashReportBody(
                                    stackTrace = it.stackTrace,
                                    modifier = Modifier.fillMaxSize(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TopBar(
    barHeight: Dp,
    filter: String,
    selected: CrashReporterSelectedUiModel?,
    onAction: (CrashReporterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .mascotPlatform(),
    ) {
        CrashFilterField(
            value = filter,
            onValueChange = { onAction(CrashReporterAction.Filter(it)) },
            modifier = Modifier
                .width(SidebarWidth)
                .padding(horizontal = FloconTheme.spacing.small),
        )
        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = FloconTheme.spacing.medium),
        ) {
            if (selected != null) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = selected.exceptionType,
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colors.statusError,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = "${selected.exceptionMessage} · ${selected.dateFormatted}",
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.inkMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                WithTooltip(stringResource(Res.string.crash_copy_trace_tooltip)) {
                    FloconOutlinedButton(onClick = { onAction(CrashReporterAction.Copy(selected)) }) {
                        Icon(
                            Icons.Filled.CopyAll,
                            contentDescription = null,
                            modifier = Modifier.padding(end = FloconTheme.spacing.extraSmall).height(16.dp),
                        )
                        Text(stringResource(Res.string.crash_copy_trace))
                    }
                }
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    tooltip = stringResource(Res.string.crash_delete_selected_tooltip),
                    onClick = { onAction(CrashReporterAction.Delete(selected.id)) },
                )
            } else {
                Box(Modifier.weight(1f))
            }
        }
        FloconVerticalDivider(modifier = Modifier.height(FloconTheme.sizes.controlHeight))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.small),
        ) {
            FloconIconButton(
                imageVector = Icons.Outlined.DeleteSweep,
                tooltip = stringResource(Res.string.crash_clear_all_tooltip),
                onClick = { onAction(CrashReporterAction.Clean) },
            )
            FeatureDocButton(doc = FeatureDoc.CrashReporter)
        }
    }
}

@Composable
private fun CrashList(
    onAction: (CrashReporterAction) -> Unit,
    crashReports: LazyPagingItems<CrashReporterUiModel>,
    filter: String,
    selected: CrashReporterSelectedUiModel?,
    modifier: Modifier = Modifier,
) {
    if (crashReports.itemCount == 0 && filter.isNotBlank()) {
        Box(modifier.padding(FloconTheme.spacing.medium)) {
            Text(
                text = stringResource(Res.string.crash_no_result),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colors.inkFaint,
            )
        }
        return
    }
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
    ) {
        items(
            count = crashReports.itemCount,
            key = crashReports.itemKey { it.id },
        ) { index ->
            crashReports[index]?.let { crash ->
                // the list is sorted by date: a new day starts a new section
                val startsDay = index == 0 || crashReports.peek(index - 1)?.dayFormatted != crash.dayFormatted
                if (startsDay) {
                    DayHeader(crash.dayFormatted)
                }
                CrashReportItem(
                    crash = crash,
                    isSelected = crash.id == selected?.id,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onAction(CrashReporterAction.Select(it.id)) },
                    onDelete = { onAction(CrashReporterAction.Delete(it.id)) }
                )
            }
        }
    }
}

@Composable
private fun DayHeader(
    day: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = day,
        style = FloconTheme.typography.labelSmall,
        fontWeight = FontWeight.SemiBold,
        color = FloconTheme.colors.inkFaint,
        modifier = modifier.padding(
            start = FloconTheme.spacing.small,
            top = FloconTheme.spacing.small,
            bottom = FloconTheme.spacing.extraSmall,
        ),
    )
}
