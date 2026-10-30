package io.github.openflocon.flocondesktop.features.files.view

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DataObject
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.InsertDriveFile
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.openflocon.flocondesktop.features.files.model.FileTypeUiModel
import io.github.openflocon.library.designsystem.FloconTheme

/**
 * Colors of a kind of file: [ink] tints its icon (and, at low alpha, its tile), [deep] is the darker shade
 * used on the light page of the grid.
 */
@Immutable
internal data class FileTypeColors(
    val ink: Color,
    val deep: Color,
) {
    /** Background of the tile of the list: the ink, softened. */
    val tile: Color get() = ink.copy(alpha = TileAlpha)

    private companion object {
        const val TileAlpha = 0.18f
    }
}

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
    val video: FileTypeColors,
    val text: FileTypeColors,
    val other: FileTypeColors,
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
) {
    fun of(type: FileTypeUiModel): FileTypeColors = when (type) {
        FileTypeUiModel.Folder -> folder
        FileTypeUiModel.Json -> json
        FileTypeUiModel.Proto -> proto
        FileTypeUiModel.Database -> database
        FileTypeUiModel.Image -> image
        FileTypeUiModel.Video -> video
        FileTypeUiModel.Text -> text
        FileTypeUiModel.Other -> other
    }
}

private val FilesDarkPalette = FilesPalette(
    folder = FileTypeColors(ink = Color(0xFFC9B8FF), deep = Color(0xFF7C66D9)),
    json = FileTypeColors(ink = Color(0xFF6FD6C6), deep = Color(0xFF1E8A7A)),
    proto = FileTypeColors(ink = Color(0xFFF2A86B), deep = Color(0xFFA85A1E)),
    database = FileTypeColors(ink = Color(0xFF86B8FF), deep = Color(0xFF2F64B0)),
    image = FileTypeColors(ink = Color(0xFFF59AC8), deep = Color(0xFFB4387A)),
    video = FileTypeColors(ink = Color(0xFFF7D774), deep = Color(0xFF9A7A12)),
    text = FileTypeColors(ink = Color(0xFFA9CBEF), deep = Color(0xFF3E6C9C)),
    other = FileTypeColors(ink = Color(0xFFE4E0EE), deep = Color(0xFF6E6886)),
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
)

// same hues on light surfaces: darker inks (the tile is the ink at low alpha)
private val FilesLightPalette = FilesDarkPalette.copy(
    folder = FileTypeColors(ink = Color(0xFF5B45C8), deep = Color(0xFF7C66D9)),
    json = FileTypeColors(ink = Color(0xFF0B7D6A), deep = Color(0xFF1E8A7A)),
    proto = FileTypeColors(ink = Color(0xFFB4501B), deep = Color(0xFFA85A1E)),
    database = FileTypeColors(ink = Color(0xFF1D6BBF), deep = Color(0xFF2F64B0)),
    image = FileTypeColors(ink = Color(0xFFB3317A), deep = Color(0xFFB4387A)),
    video = FileTypeColors(ink = Color(0xFF8A6A00), deep = Color(0xFF9A7A12)),
    text = FileTypeColors(ink = Color(0xFF2F6AA8), deep = Color(0xFF3E6C9C)),
    other = FileTypeColors(ink = Color(0xFF5E5878), deep = Color(0xFF6E6886)),
    onBadge = Color(0xFFFFFFFF),
    sizeBar = Color(0xFF7C66D9),
    switchTrackOn = Color(0xFF7C66D9),
    pageFold = Color(0xFFDCD6EA),
)

/** The single place mapping a kind of file to its icon. */
internal fun FileTypeUiModel.icon(): ImageVector = when (this) {
    FileTypeUiModel.Folder -> Icons.Outlined.Folder
    FileTypeUiModel.Image -> Icons.Outlined.Image
    FileTypeUiModel.Video -> Icons.Outlined.Videocam
    FileTypeUiModel.Text -> Icons.Outlined.Description
    FileTypeUiModel.Json -> Icons.Outlined.DataObject
    FileTypeUiModel.Proto -> Icons.Outlined.Tune
    FileTypeUiModel.Database -> Icons.Outlined.Storage
    FileTypeUiModel.Other -> Icons.Outlined.InsertDriveFile
}

internal val filesPalette: FilesPalette
    @Composable
    @ReadOnlyComposable
    get() = if (FloconTheme.isDarkTheme) FilesDarkPalette else FilesLightPalette
