package io.github.openflocon.flocondesktop.features.timeline

import io.github.openflocon.domain.timeline.usecase.ObserveTimelineSettingsUseCase
import io.github.openflocon.domain.timeline.usecase.SetTimelinePinPanelUseCase
import io.github.openflocon.navigation.PanelPinGroups
import io.github.openflocon.navigation.scene.OnPin
import io.github.openflocon.navigation.scene.PanelPinRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * The pin state of the panels opened from the timeline, which is a setting of the timeline.
 */
internal class TimelinePanelPin(
    observeTimelineSettingsUseCase: ObserveTimelineSettingsUseCase,
    private val setTimelinePinPanelUseCase: SetTimelinePinPanelUseCase,
) : OnPin,
    PanelPinRegistry {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override val isPinned: StateFlow<Boolean> = observeTimelineSettingsUseCase()
        .map { it.pinPanel }
        .stateIn(scope, SharingStarted.Eagerly, false)

    override fun onPin(pinned: Boolean) {
        scope.launch { setTimelinePinPanelUseCase(pinned) }
    }

    override fun onPinFor(group: String): OnPin? = this.takeIf { group == PanelPinGroups.Timeline }
}
