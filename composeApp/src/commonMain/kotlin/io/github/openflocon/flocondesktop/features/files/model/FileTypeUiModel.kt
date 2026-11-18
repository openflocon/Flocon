package io.github.openflocon.flocondesktop.features.files.model

/** Kind of an entry, derived from its extension: drives the colored tile and the badge of the file. */
enum class FileTypeUiModel {
    Folder,
    Json,
    Proto,
    Database,
    Image,
    Video,
    Text,
    Other,
}
