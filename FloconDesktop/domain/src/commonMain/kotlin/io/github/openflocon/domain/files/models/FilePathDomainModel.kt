package io.github.openflocon.domain.files.models

sealed interface FilePathDomainModel {
    /**
     * A root folder announced by the device, identified by its id
     * (a stable slug for default folders, an absolute path for registered ones).
     */
    data class Root(
        val id: String,
    ) : FilePathDomainModel

    data class Real(
        val absolutePath: String,
    ) : FilePathDomainModel
}
