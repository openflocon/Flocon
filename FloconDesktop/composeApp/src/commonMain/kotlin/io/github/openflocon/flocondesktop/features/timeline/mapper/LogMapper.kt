package io.github.openflocon.flocondesktop.features.timeline.mapper

import androidx.compose.ui.graphics.Color
import io.github.openflocon.domain.common.time.formatDate
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_LIFECYCLE
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_NAVIGATION
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_USER_ACTION
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import io.github.openflocon.flocondesktop.features.timeline.model.TimelineLogDetailUiModel
import io.github.openflocon.library.designsystem.theme.FloconColors
import kotlinx.collections.immutable.toImmutableList

/**
 * The shape of a log in the list, the graph and the detail. The owner reads shapes before colors: a reserved type
 * (sent by the SDK) has its own shape, any other log is a diamond.
 */
internal enum class LogGlyphShape { Diamond, Arrow, Pointer, Heart }

/**
 * The color token of a log: the level for a diamond, the type for the reserved ones.
 */
internal enum class LogGlyphColor {
    Debug,
    Info,
    Warning,
    Error,
    Navigation,
    UserAction,
    Lifecycle;

    fun color(colors: FloconColors): Color = when (this) {
        Debug -> TimelineLogLevel.Debug.color(colors)
        Info -> TimelineLogLevel.Info.color(colors)
        Warning -> TimelineLogLevel.Warning.color(colors)
        Error -> TimelineLogLevel.Error.color(colors)
        Navigation -> colors.logNavigation
        UserAction -> colors.logUserAction
        Lifecycle -> colors.logLifecycle
    }
}

internal data class LogGlyph(val shape: LogGlyphShape, val color: LogGlyphColor) {
    /** A reserved type (navigation, user action, lifecycle): it carries no level on the wire. */
    val isTyped: Boolean get() = shape != LogGlyphShape.Diamond

    fun color(colors: FloconColors): Color = color.color(colors)
}

/**
 * The single mapping of a log to its glyph: the reserved types (case-insensitive) have a shape and a color of their
 * own, everything else (tags, custom types) is a diamond colored by [level] (a missing one counts as info).
 */
internal fun logGlyphOf(type: String?, level: TimelineLogLevel?): LogGlyph = when {
    type.isLogType(TIMELINE_LOG_TYPE_NAVIGATION) -> LogGlyph(LogGlyphShape.Arrow, LogGlyphColor.Navigation)

    type.isLogType(TIMELINE_LOG_TYPE_USER_ACTION) -> LogGlyph(LogGlyphShape.Pointer, LogGlyphColor.UserAction)

    type.isLogType(TIMELINE_LOG_TYPE_LIFECYCLE) -> LogGlyph(LogGlyphShape.Heart, LogGlyphColor.Lifecycle)

    else -> LogGlyph(
        LogGlyphShape.Diamond,
        when (level ?: TimelineLogLevel.Info) {
            TimelineLogLevel.Debug -> LogGlyphColor.Debug
            TimelineLogLevel.Info -> LogGlyphColor.Info
            TimelineLogLevel.Warning -> LogGlyphColor.Warning
            TimelineLogLevel.Error -> LogGlyphColor.Error
        },
    )
}

/**
 * One color per level, used by the list, the graph, the detail and the filters: debug is a dark blue and info a pale
 * violet (far apart in lightness), warning and error use the colors of the http statuses (orange, red).
 */
internal fun TimelineLogLevel.color(colors: FloconColors): Color = when (this) {
    TimelineLogLevel.Debug -> colors.logDebug
    TimelineLogLevel.Info -> colors.logInfo
    TimelineLogLevel.Warning -> colors.statusWarning
    TimelineLogLevel.Error -> colors.statusError
}

/** The level of a stored facet value ("DEBUG", "INFO", "WARNING", "ERROR"). */
internal fun logLevelOf(value: String): TimelineLogLevel? = TimelineLogLevel.entries.firstOrNull { it.name.equals(value, ignoreCase = true) }

internal fun TimelineLogDomainModel.toDetailUi(): TimelineLogDetailUiModel = TimelineLogDetailUiModel(
    value = value,
    type = type,
    level = level,
    time = formatDate(timestamp),
    params = params.entries.sortedBy { it.key }.map { it.key to it.value }.toImmutableList(),
)

private fun String?.isLogType(reserved: String) = equals(reserved, ignoreCase = true)
