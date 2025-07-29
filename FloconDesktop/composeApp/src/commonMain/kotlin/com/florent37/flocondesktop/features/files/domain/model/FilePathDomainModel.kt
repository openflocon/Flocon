package com.florent37.flocondesktop.features.files.domain.model

sealed interface FilePathDomainModel {
    sealed interface Constants : FilePathDomainModel {
        data object CachesDir : FilePathDomainModel.Constants

        data object FilesDir : FilePathDomainModel.Constants
    }

    data class Real(
        val absolutePath: String,
    ) : FilePathDomainModel
}
