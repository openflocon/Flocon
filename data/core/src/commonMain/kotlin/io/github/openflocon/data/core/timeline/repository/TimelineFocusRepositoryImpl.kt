package io.github.openflocon.data.core.timeline.repository

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.timeline.models.TimelineEventRef
import io.github.openflocon.domain.timeline.models.TimelineFocusRequestDomainModel
import io.github.openflocon.domain.timeline.repository.TimelineFocusRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull

// a request not applied after that long (the window was closed, for example) is dropped, not applied hours later
internal const val TIMELINE_FOCUS_TTL_MS = 600_000L

internal class TimelineFocusRepositoryImpl(
    private val now: () -> Long = { System.currentTimeMillis() },
) : TimelineFocusRepository {

    private val state = MutableStateFlow<TimelineFocusRequestDomainModel?>(null)

    override fun requestFocus(
        target: DeviceIdAndPackageNameDomainModel,
        sessionStart: Long?,
        event: TimelineEventRef?,
        fromMs: Long,
        toMs: Long,
    ) {
        state.value = TimelineFocusRequestDomainModel(
            target = target,
            sessionStart = sessionStart,
            event = event,
            fromMs = fromMs,
            toMs = toMs,
            requestedAtMs = now(),
        )
    }

    override fun observeFocus(): Flow<TimelineFocusRequestDomainModel> = state
        .filterNotNull()
        .filter { !it.isExpired() }

    override fun consumeFocus(request: TimelineFocusRequestDomainModel) {
        state.compareAndSet(request, null)
    }

    private fun TimelineFocusRequestDomainModel.isExpired() = now() - requestedAtMs > TIMELINE_FOCUS_TTL_MS
}
