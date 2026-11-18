package io.github.openflocon.domain.files.models

data class FromDeviceFilesResultDomainModel(
    val requestId: String,
    val files: List<FromDeviceFilesDomainModel>,
)
