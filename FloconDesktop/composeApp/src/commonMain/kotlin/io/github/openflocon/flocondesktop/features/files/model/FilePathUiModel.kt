package io.github.openflocon.flocondesktop.features.files.model

sealed interface FilePathUiModel {
    sealed interface Constants : FilePathUiModel {
        data object CachesDir : Constants

        data object FilesDir : Constants
    }

    data class Real(
        val absolutePath: String,
    ) : FilePathUiModel
}


val FilePathUiModel.path: String
    get() = when (this) {
        is FilePathUiModel.Constants.CachesDir -> "caches"
        is FilePathUiModel.Constants.FilesDir -> "files"
        is FilePathUiModel.Real -> absolutePath
    }
