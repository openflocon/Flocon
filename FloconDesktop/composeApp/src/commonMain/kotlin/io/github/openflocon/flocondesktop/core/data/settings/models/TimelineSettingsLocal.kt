package io.github.openflocon.flocondesktop.core.data.settings.models

import io.github.openflocon.domain.timeline.models.DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS
import io.github.openflocon.domain.timeline.models.TimelineScreenshotIntervalsSeconds
import io.github.openflocon.domain.timeline.models.TimelineSettingsDomainModel
import io.github.openflocon.domain.timeline.models.TimelineSource
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class TimelineSettingsLocal(

    @SerialName("auto_scroll")
    val autoScroll: Boolean = false,

    @SerialName("invert_list")
    val invertList: Boolean = false,

    @SerialName("pin_panel")
    val pinPanel: Boolean = false,

    @SerialName("graph")
    val graph: Boolean = true,

    @SerialName("merge_plugins")
    val graphSingle: Boolean = false,

    // the sources turned off, so a source added later is on by default
    @SerialName("disabled_sources")
    val disabledSources: Set<String> = emptySet(),

    @SerialName("screenshots")
    val screenshots: Boolean = false,

    @SerialName("screenshot_interval_seconds")
    val screenshotIntervalSeconds: Int = DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS,

)

internal fun TimelineSettingsLocal.toDomain() = TimelineSettingsDomainModel(
    autoScroll = autoScroll,
    invertList = invertList,
    pinPanel = pinPanel,
    graph = graph,
    graphSingle = graphSingle,
    // an unknown name (a source removed since) is ignored
    selectedSources = TimelineSource.entries.filter { it.name !in disabledSources }.toSet(),
    screenshots = screenshots,
    // a value that is not one of the choices (an old or edited file) falls back to the default
    screenshotIntervalSeconds = screenshotIntervalSeconds.takeIf { it in TimelineScreenshotIntervalsSeconds }
        ?: DEFAULT_TIMELINE_SCREENSHOT_INTERVAL_SECONDS,
)

internal fun TimelineSettingsDomainModel.toLocal() = TimelineSettingsLocal(
    autoScroll = autoScroll,
    invertList = invertList,
    pinPanel = pinPanel,
    graph = graph,
    graphSingle = graphSingle,
    disabledSources = TimelineSource.entries.filter { it !in selectedSources }.map { it.name }.toSet(),
    screenshots = screenshots,
    screenshotIntervalSeconds = screenshotIntervalSeconds,
)
