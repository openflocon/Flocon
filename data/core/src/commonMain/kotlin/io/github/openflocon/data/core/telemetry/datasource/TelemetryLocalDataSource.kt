package io.github.openflocon.data.core.telemetry.datasource

import io.github.openflocon.domain.telemetry.models.TelemetryLevelDomainModel

/** Persists the user's telemetry choice and, only in [TelemetryLevelDomainModel.Full], the installation id. */
interface TelemetryLocalDataSource {
    /** `null` until the user answered the consent prompt. */
    fun getLevel(): TelemetryLevelDomainModel?

    fun setLevel(level: TelemetryLevelDomainModel)

    fun getInstallationId(): String?

    fun setInstallationId(id: String?)
}
