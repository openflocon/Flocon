package io.github.openflocon.flocondesktop.features.files.model

sealed interface FilePathUiModel {
    data class Root(
        val id: String,
    ) : FilePathUiModel

    data class Real(
        val absolutePath: String,
    ) : FilePathUiModel
}
