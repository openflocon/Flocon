package io.github.openflocon.flocondesktop.features.crashreporter

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.crashes
import flocondesktop.composeapp.generated.resources.crashes_counter
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureEmptyState
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterAction
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterSelectedUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.model.CrashReporterUiModel
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportDetail
import io.github.openflocon.flocondesktop.features.crashreporter.view.CrashReportItem
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.navigation.mascot.mascotPlatform
import org.jetbrains.compose.resources.pluralStringResource
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

    CrashReportScreen(
        modifier = Modifier.fillMaxSize(),
        crashReports = crashReports,
        selected = selected,
        showEmptyState = showEmptyState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CrashReportScreen(
    onAction: (CrashReporterAction) -> Unit,
    crashReports: LazyPagingItems<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
    showEmptyState: Boolean,
    modifier: Modifier = Modifier,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        modifier = modifier
    ) {
        FloconPageTopBar(
            modifier = Modifier
                .fillMaxWidth()
                .mascotPlatform(),
            title = stringResource(Res.string.crashes),
            subtitle = pluralStringResource(Res.plurals.crashes_counter, crashReports.itemCount, crashReports.itemCount),
            headerActions = {
                FeatureDocButton(doc = FeatureDoc.CrashReporter)
                FloconIconButton(
                    imageVector = Icons.Outlined.Delete,
                    onClick = { onAction(CrashReporterAction.Clean) },
                )
            },
        )

        if (showEmptyState) {
            FeatureEmptyState(
                doc = FeatureDoc.CrashReporter,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(FloconTheme.shapes.medium)
                    .background(FloconTheme.colors.bg100)
            )
        } else {
            Row(Modifier.fillMaxSize()) {
                LeftPannel(
                    modifier = Modifier.fillMaxHeight()
                        .width(340.dp),
                    crashReports = crashReports,
                    onAction = onAction,
                    selected = selected,
                )

                Spacer(modifier = Modifier.width(FloconTheme.spacing.medium))

                Surface(
                    color = FloconTheme.colors.bg100,
                    contentColor = FloconTheme.colors.ink,
                    modifier = Modifier.fillMaxSize()
                        .clip(FloconTheme.shapes.medium)
                        .border(
                            width = 1.dp,
                            color = FloconTheme.colors.bg200,
                            shape = FloconTheme.shapes.medium
                        )
                ) {
                    selected?.let {
                        CrashReportDetail(
                            modifier = Modifier
                                .fillMaxSize(),
                            crash = it,
                            onCopy = {
                                onAction(CrashReporterAction.Copy(it))
                            },
                            onDelete = {
                                onAction(CrashReporterAction.Delete(it.id))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeftPannel(
    onAction: (CrashReporterAction) -> Unit,
    crashReports: LazyPagingItems<CrashReporterUiModel>,
    selected: CrashReporterSelectedUiModel?,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = FloconTheme.colors.bg100,
        contentColor = FloconTheme.colors.ink,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = FloconTheme.colors.bg200,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            Modifier.fillMaxSize()
                .padding(all = FloconTheme.spacing.extraSmall),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(FloconTheme.spacing.medium),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
            ) {
                items(count = crashReports.itemCount) { index ->
                    crashReports[index]?.let { crash ->
                        CrashReportItem(
                            crash = crash,
                            isSelected = crash.id == selected?.id,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onAction(CrashReporterAction.Select(it.id))
                            },
                            onDelete = {
                                onAction(CrashReporterAction.Delete(it.id))
                            }
                        )
                    }
                }
            }
        }
    }
}
