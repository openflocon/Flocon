package io.github.openflocon.domain.device.models

import io.github.openflocon.domain.models.ProcessId

data class RecordingDomainModel(
    val onDeviceFilePath: String,
    val processId: ProcessId,
)
