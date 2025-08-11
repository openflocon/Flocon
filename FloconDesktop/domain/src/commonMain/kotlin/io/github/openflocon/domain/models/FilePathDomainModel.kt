package io.github.openflocon.domain.models

sealed interface FilePathDomainModel {
    sealed interface Constants : FilePathDomainModel {
        data object CachesDir : Constants

        data object FilesDir : Constants
    }

    data class Real(
        val absolutePath: String,
    ) : FilePathDomainModel
}
