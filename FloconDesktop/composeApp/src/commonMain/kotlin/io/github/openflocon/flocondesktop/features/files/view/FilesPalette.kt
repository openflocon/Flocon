package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.library.designsystem.FloconTheme

/** Tile background and ink of a kind of file. */
@Immutable
internal data class FileTypeColors(
    val tile: Color,
    val ink: Color,
)

/**
 * The colors specific to the Files screen (one per kind of file, the folder and page drawings of the grid,
 * the size bars and the selection), gathered here instead of being scattered in the composables.
 * Everything else comes from [FloconTheme.colors].
 */
@Immutable
internal data class FilesPalette(
    val folder: FileTypeColors,
    val json: FileTypeColors,
    val proto: FileTypeColors,
    val database: FileTypeColors,
    val image: FileTypeColors,
    val text: FileTypeColors,
    // text of the extension badge, drawn on the ink of the type
    val onBadge: Color,
    val sizeBar: Color,
    val selection: Color,
    val onSelection: Color,
    val switchTrackOn: Color,
    // grid drawings
    val folderBack: Color,
    val folderFront: Color,
    val folderHighlight: Color,
    val page: Color,
    val pageFold: Color,
    val pageLines: Color,
) {
    fun of(type: FileTypeUiModel): FileTypeColors = when (type) {
        FileTypeUiModel.Folder -> folder

        FileTypeUiModel.Json -> json

        FileTypeUiModel.Proto -> proto

        FileTypeUiModel.Database -> database

        FileTypeUiModel.Image,
        FileTypeUiModel.Video,
        -> image

        FileTypeUiModel.Text,
        FileTypeUiModel.Other,
        -> text
    }
}

private val FilesDarkPalette = FilesPalette(
    folder = FileTypeColors(tile = Color(0xFF3B2F5C), ink = Color(0xFFC9B8FF)),
    json = FileTypeColors(tile = Color(0xFF173A3A), ink = Color(0xFF6FD6C6)),
    proto = FileTypeColors(tile = Color(0xFF3D2A1C), ink = Color(0xFFF2A86B)),
    database = FileTypeColors(tile = Color(0xFF1C2C45), ink = Color(0xFF7FB2FF)),
    image = FileTypeColors(tile = Color(0xFF3D2035), ink = Color(0xFFF08FC0)),
    text = FileTypeColors(tile = Color(0xFF2C2A33), ink = Color(0xFFC8C3D6)),
    onBadge = Color(0xFF16131F),
    sizeBar = Color(0xFF9B87F5),
    selection = Color(0xFF5B4AB0),
    onSelection = Color(0xFFECE9F5),
    switchTrackOn = Color(0xFF9B87F5),
    folderBack = Color(0xFF7C66D9),
    folderFront = Color(0xFFA893F5),
    folderHighlight = Color(0xFFC2B3FA),
    page = Color(0xFFF1EEF8),
    pageFold = Color(0xFFCFC8E0),
    pageLines = Color(0xFFC9C2DA),
)

// same hues on light surfaces: soft tiles and darker inks
private val FilesLightPalette = FilesDarkPalette.copy(
    folder = FileTypeColors(tile = Color(0xFFECE6FF), ink = Color(0xFF5B45C8)),
    json = FileTypeColors(tile = Color(0xFFDDF5F1), ink = Color(0xFF0B7D6A)),
    proto = FileTypeColors(tile = Color(0xFFFCEBDD), ink = Color(0xFFB4501B)),
    database = FileTypeColors(tile = Color(0xFFE3EEFC), ink = Color(0xFF1D6BBF)),
    image = FileTypeColors(tile = Color(0xFFFBE6F1), ink = Color(0xFFB3317A)),
    text = FileTypeColors(tile = Color(0xFFEEEDF2), ink = Color(0xFF585C64)),
    onBadge = Color(0xFFFFFFFF),
    sizeBar = Color(0xFF7C66D9),
    switchTrackOn = Color(0xFF7C66D9),
    pageFold = Color(0xFFDCD6EA),
    pageLines = Color(0xFFD5CFE3),
)

internal val filesPalette: FilesPalette
    @Composable
    @ReadOnlyComposable
    get() = if (FloconTheme.isDarkTheme) FilesDarkPalette else FilesLightPalette
