package io.github.openflocon.data.core.telemetry.repository

import io.github.openflocon.data.core.telemetry.datasource.TelemetryLocalDataSource
import io.github.openflocon.data.core.telemetry.datasource.TelemetryRemoteDataSource
import io.github.openflocon.domain.telemetry.models.TelemetryConfigDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel
import io.github.openflocon.domain.telemetry.models.TelemetryStateDomainModel
import io.github.openflocon.domain.telemetry.repository.TelemetryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Decides, on every event, whether it is sent (key, DO_NOT_TRACK, user level), and with which identifier.
 * The remote data source only knows how to send.
 */
class TelemetryRepositoryImpl(
    private val localDataSource: TelemetryLocalDataSource,
    private val remoteDataSource: TelemetryRemoteDataSource,
    private val config: TelemetryConfigDomainModel,
    private val applicationScope: CoroutineScope,
) : TelemetryRepository {

    /** Regenerated on every launch and never written to disk. */
    private val sessionId: String = UUID.randomUUID().toString()

    private val _state = MutableStateFlow(computeState())
    override val state: StateFlow<TelemetryStateDomainModel> = _state.asStateFlow()

    override fun setLevel(level: TelemetryLevelDomainModel) {
        localDataSource.setLevel(level)
        if (level != TelemetryLevelDomainModel.Full) {
            // Consent withdrawn (or never given): the persistent identifier must not survive.
            localDataSource.setInstallationId(null)
        }
        _state.value = computeState()
    }

    override fun track(event: String, properties: Map<String, Any>) {
        val current = _state.value
        // Opt-in: nothing leaves the computer before the user answered the consent prompt.
        if (!current.isAvailable || !current.hasChosen) return

        val distinctId = when (current.level) {
            TelemetryLevelDomainModel.Disabled -> return

            TelemetryLevelDomainModel.Anonymous -> sessionId

            TelemetryLevelDomainModel.Full ->
                localDataSource.getInstallationId()
                    ?: UUID.randomUUID().toString().also { localDataSource.setInstallationId(it) }
        }

        val allProperties = properties + mapOf(
            // No person profiles: cheaper on PostHog and nothing is attached to a "user".
            "\$process_person_profile" to false,
            "\$session_id" to sessionId,
            // Don't resolve the request IP into a location.
            "\$geoip_disable" to true,
            "\$lib" to "flocon-desktop",
            "app_version" to config.appVersion,
            "os_name" to config.osName,
            "os_arch" to config.osArch,
            "telemetry_level" to current.level.name,
        )

        applicationScope.launch {
            remoteDataSource.capture(
                config = config,
                event = event,
                distinctId = distinctId,
                properties = allProperties,
            )
        }
    }

    private fun computeState(): TelemetryStateDomainModel {
        val chosen = localDataSource.getLevel()
        return TelemetryStateDomainModel(
            isAvailable = config.isAvailable,
            doNotTrack = config.doNotTrack,
            hasChosen = chosen != null,
            level = when {
                !config.isAvailable -> TelemetryLevelDomainModel.Disabled
                else -> chosen ?: TelemetryLevelDomainModel.Disabled
            },
        )
    }
}
