package io.github.openflocon.domain.device.models

import io.github.openflocon.domain.common.Either
import kotlinx.coroutines.Deferred

sealed interface RecordingDomainModel {

    data class Android(
        val deviceId: String,
        val packageName: String,
        val onDeviceFilePath: String,
        val processName: String,
        val completableDeferred: Deferred<Either<Throwable, String>>,
    ) : RecordingDomainModel

    /**
     * Recorded by `simctl` directly into [filePath], on this computer.
     */
    data class IosSimulator(
        val filePath: String,
    ) : RecordingDomainModel
}
