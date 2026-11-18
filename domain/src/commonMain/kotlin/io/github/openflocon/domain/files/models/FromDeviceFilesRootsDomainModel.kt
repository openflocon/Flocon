package io.github.openflocon.domain.files.models

data class FromDeviceFilesRootsDomainModel(
    val requestId: String,
    val roots: List<FileRootDomainModel>,
)
