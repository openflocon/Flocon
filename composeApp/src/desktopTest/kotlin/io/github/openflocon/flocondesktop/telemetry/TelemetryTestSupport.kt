package io.github.openflocon.flocondesktop.telemetry

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryStateDomainModel
import io.github.openflocon.domain.telemetry.repository.TelemetryRepository
import io.github.openflocon.domain.telemetry.usecase.ObserveTelemetryStateUseCase
import io.github.openflocon.domain.telemetry.usecase.SetTelemetryLevelUseCase
import io.github.openflocon.domain.telemetry.usecase.TrackTelemetryEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Records what would be sent: like the real repository, nothing is recorded before the user opted in. */
internal class FakeTelemetryRepository(
    level: TelemetryLevelDomainModel? = TelemetryLevelDomainModel.Anonymous,
) : TelemetryRepository {
    val events = mutableListOf<Pair<String, Map<String, Any>>>()

    private val _state = MutableStateFlow(stateOf(level))
    override val state: StateFlow<TelemetryStateDomainModel> = _state.asStateFlow()

    override fun setLevel(level: TelemetryLevelDomainModel) {
        _state.value = stateOf(level)
    }

    override fun track(event: String, properties: Map<String, Any>) {
        if (_state.value.canSend) events += event to properties
    }

    private fun stateOf(level: TelemetryLevelDomainModel?) = TelemetryStateDomainModel(
        isAvailable = true,
        doNotTrack = false,
        hasChosen = level != null,
        level = level ?: TelemetryLevelDomainModel.Disabled,
    )
}

internal fun telemetryManager(repository: TelemetryRepository) = TelemetryManager(
    observeTelemetryStateUseCase = ObserveTelemetryStateUseCase(repository),
    setTelemetryLevelUseCase = SetTelemetryLevelUseCase(repository),
    trackTelemetryEventUseCase = TrackTelemetryEventUseCase(repository),
)
