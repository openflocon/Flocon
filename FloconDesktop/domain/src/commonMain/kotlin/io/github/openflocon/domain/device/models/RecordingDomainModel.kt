package io.github.openflocon.domain.device.models

import io.github.openflocon.domain.common.Either
import kotlinx.coroutines.Deferred

data class RecordingDomainModel(
    val onDeviceFilePath: String,
    val processName: String,
    val completableDeferred: Deferred<Either<Throwable, String>>,
)
