package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.models.maintenance.AutoPurgeInterval
import io.github.openflocon.domain.models.maintenance.PurgeInterval

@Immutable
data class DataUiState(
    // null while it is being computed
    val databaseSizeBytes: Long? = null,
    // deletes the old data at each launch of the app
    val autoPurgeInterval: AutoPurgeInterval = AutoPurgeInterval.DEFAULT,
    // for the manual purge
    val purgeInterval: PurgeInterval = PurgeInterval.DEFAULT,
    val isPurging: Boolean = false,
    // the screenshots and the video recordings saved in the documents folder, null while computed
    val screenshotsSizeBytes: Long? = null,
    val recordingsSizeBytes: Long? = null,
    // never by default: these are files the user saved on purpose
    val autoMediaCleanupInterval: AutoPurgeInterval = AutoPurgeInterval.Never,
    val mediaCleanupInterval: PurgeInterval = PurgeInterval.DEFAULT,
    val isCleaningMedia: Boolean = false,
)

fun formatDatabaseSize(bytes: Long): String {
    val kb = 1024.0
    val mb = kb * 1024
    val gb = mb * 1024
    return when {
        bytes >= gb -> "${(bytes / gb).format1()} GB"
        bytes >= mb -> "${(bytes / mb).format1()} MB"
        bytes >= kb -> "${(bytes / kb).format1()} KB"
        else -> "$bytes B"
    }
}

private fun Double.format1(): String {
    val tenths = (this * 10).toLong()
    return "${tenths / 10}.${tenths % 10}"
}
