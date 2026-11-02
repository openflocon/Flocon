package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter_exclude
import flocondesktop.composeapp.generated.resources.filter_include
import flocondesktop.composeapp.generated.resources.remove
import flocondesktop.composeapp.generated.resources.timeline_filters_database_empty
import flocondesktop.composeapp.generated.resources.timeline_filters_hide_transactions
import flocondesktop.composeapp.generated.resources.timeline_filters_sql_placeholder
import flocondesktop.composeapp.generated.resources.timeline_filters_table_empty
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAccessibilityFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAnalyticsFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineCrashFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineDatabaseFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetGroupUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineRuleUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineTableFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineWebViewFilterUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIncludeExcludeInput
import io.github.openflocon.library.designsystem.components.FloconRuleChip
import io.github.openflocon.library.designsystem.components.FloconSwitch
import org.jetbrains.compose.resources.stringResource

// The filters of the sources other than the network: each source has its own card, like NetworkFilterCard.

@Composable
internal fun DatabaseFilterCard(
    database: TimelineDatabaseFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        // which database, and which kind of statement
        FacetRow(
            groups = listOfNotNull(database.names, database.statements),
            onAction = onAction,
            emptyText = stringResource(Res.string.timeline_filters_database_empty),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconIncludeExcludeInput(
                includeLabel = stringResource(Res.string.filter_include),
                excludeLabel = stringResource(Res.string.filter_exclude),
                placeholder = stringResource(Res.string.timeline_filters_sql_placeholder),
                onSubmit = { text, exclude -> onAction(TimelineFilterAction.AddDatabaseRule(text, exclude)) },
            )
            RuleChips(
                rules = database.rules,
                onRemove = { onAction(TimelineFilterAction.RemoveDatabaseRule(it)) },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(Res.string.timeline_filters_hide_transactions),
                    style = FloconTheme.typography.bodyMedium,
                    color = FloconTheme.colors.ink,
                    modifier = Modifier.weight(1f),
                )
                FloconSwitch(
                    checked = database.hideTransactions,
                    onCheckedChange = { onAction(TimelineFilterAction.SetHideDatabaseTransactions(it)) },
                )
            }
        }
    }
}

@Composable
internal fun TableFilterCard(
    table: TimelineTableFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FacetRow(
        groups = listOf(table.tables),
        onAction = onAction,
        emptyText = stringResource(Res.string.timeline_filters_table_empty),
        modifier = modifier,
    )
}

@Composable
internal fun WebViewFilterCard(
    webView: TimelineWebViewFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FacetRow(groups = listOfNotNull(webView.kinds), onAction = onAction, modifier = modifier)
}

@Composable
internal fun AnalyticsFilterCard(
    analytics: TimelineAnalyticsFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FacetRow(groups = listOfNotNull(analytics.tables), onAction = onAction, modifier = modifier)
}

@Composable
internal fun CrashFilterCard(
    crash: TimelineCrashFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FacetRow(groups = listOfNotNull(crash.exceptions), onAction = onAction, modifier = modifier)
}

@Composable
internal fun AccessibilityFilterCard(
    accessibility: TimelineAccessibilityFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    FacetRow(groups = listOfNotNull(accessibility.actions), onAction = onAction, modifier = modifier)
}

// the selectors of a source share the width, a lonely one keeps the width of two
@Composable
private fun FacetRow(
    groups: List<TimelineFacetGroupUiModel>,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
    emptyText: String? = null, // told by a selector without value yet
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        groups.forEach { group ->
            FacetGroup(group = group, onAction = onAction, modifier = Modifier.weight(1f), emptyText = emptyText)
        }
    }
}

@Composable
private fun RuleChips(
    rules: List<TimelineRuleUiModel>,
    onRemove: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (rules.isEmpty()) return
    val removeDescription = stringResource(Res.string.remove)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        rules.forEach { rule ->
            FloconRuleChip(
                text = rule.value,
                exclude = rule.exclude,
                onRemove = { onRemove(rule.value) },
                removeContentDescription = removeDescription,
            )
        }
    }
}
