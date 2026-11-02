package io.github.openflocon.flocondesktop.features.timeline.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAccessibilityFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineAnalyticsFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineCrashFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineDatabaseFilterUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFacetGroupUiModel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineFilterAction
import io.github.openflocon.library.designsystem.FloconTheme

// The filters of the sources other than the network: each source has its own card, like NetworkFilterCard.

@Composable
internal fun DatabaseFilterCard(
    database: TimelineDatabaseFilterUiModel,
    onAction: (TimelineFilterAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    // the database and the kind of statement side by side
    FacetRow(
        groups = listOfNotNull(database.names, database.statements),
        onAction = onAction,
        modifier = modifier,
    )
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
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = FloconTheme.spacing.large, end = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
    ) {
        groups.forEach { group ->
            FacetGroup(group = group, onAction = onAction, modifier = Modifier.weight(1f))
        }
    }
}
