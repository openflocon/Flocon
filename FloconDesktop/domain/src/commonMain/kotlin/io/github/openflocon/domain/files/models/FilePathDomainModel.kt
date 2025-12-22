package io.github.openflocon.domain.files.models

sealed interface FilePathDomainModel {
    sealed interface Constants : FilePathDomainModel {
        data object CachesDir : Constants

        data object FilesDir : Constants
    }

    data class Real(
        val absolutePath: String,
    ) : FilePathDomainModel
}

val FilePathDomainModel.path: String
    get() = when (this) {
        is FilePathDomainModel.Constants.CachesDir -> "caches"
        is FilePathDomainModel.Constants.FilesDir -> "files"
        is FilePathDomainModel.Real -> absolutePath
    }
