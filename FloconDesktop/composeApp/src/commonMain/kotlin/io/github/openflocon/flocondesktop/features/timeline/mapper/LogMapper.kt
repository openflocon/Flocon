package io.github.openflocon.flocondesktop.features.timeline.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogDetailUiModel
import io.github.openflocon.library.designsystem.theme.FloconColors
import kotlinx.collections.immutable.toImmutableList

/**
 * The icon of a log: the types sent by the SDK have their own, any other type is a custom tag of the app.
 */
internal enum class TimelineLogIcon { Navigation, UserAction, Lifecycle, Custom }

internal fun logIconOf(type: String): TimelineLogIcon = when (type) {
    "navigation" -> TimelineLogIcon.Navigation
    "userAction" -> TimelineLogIcon.UserAction
    "lifecycle" -> TimelineLogIcon.Lifecycle
    else -> TimelineLogIcon.Custom
}

/**
 * Debug is quiet, info neutral, warning and error use the colors of the http statuses.
 */
internal fun TimelineLogLevel.color(colors: FloconColors): Color = when (this) {
    TimelineLogLevel.Debug -> colors.inkFaint
    TimelineLogLevel.Info -> colors.inkMuted
    TimelineLogLevel.Warning -> colors.statusWarning
    TimelineLogLevel.Error -> colors.statusError
}

internal fun TimelineLogDomainModel.toDetailUi(): TimelineLogDetailUiModel = TimelineLogDetailUiModel(
    value = value,
    type = type,
    level = level,
    time = formatDate(timestamp),
    params = params.entries.sortedBy { it.key }.map { it.key to it.value }.toImmutableList(),
)
