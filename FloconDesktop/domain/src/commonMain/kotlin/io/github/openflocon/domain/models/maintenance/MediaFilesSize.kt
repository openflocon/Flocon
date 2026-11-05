package io.github.openflocon.domain.models.maintenance

/** Size on disk, in bytes, of the files saved by the user in the Flocon documents folder. */
data class MediaFilesSize(
    val screenshotsBytes: Long,
    val recordingsBytes: Long,
)
