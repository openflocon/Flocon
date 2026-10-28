package io.github.openflocon.flocondesktop.core.data.settings.models

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

    // the sources turned off, so a source added later is on by default
    @SerialName("disabled_sources")
    val disabledSources: Set<String> = emptySet(),

)

internal fun TimelineSettingsLocal.toDomain() = TimelineSettingsDomainModel(
    autoScroll = autoScroll,
    invertList = invertList,
    pinPanel = pinPanel,
    // an unknown name (a source removed since) is ignored
    selectedSources = TimelineSource.entries.filter { it.name !in disabledSources }.toSet(),
)

internal fun TimelineSettingsDomainModel.toLocal() = TimelineSettingsLocal(
    autoScroll = autoScroll,
    invertList = invertList,
    pinPanel = pinPanel,
    disabledSources = TimelineSource.entries.filter { it !in selectedSources }.map { it.name }.toSet(),
)
