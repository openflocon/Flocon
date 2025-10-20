package io.github.openflocon.flocon

internal expect class FloconFile

internal expect fun FloconFile.exists() : Boolean

internal expect fun deleteFolderContent(folder: FloconFile)