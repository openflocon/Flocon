package io.github.openflocon.domain.files.models

data class FromDeviceFilesDomainModel(
    val name: String,
    val isDirectory: Boolean,
    val path: String,
    val size: Long,
    val lastModified: Long,
)
