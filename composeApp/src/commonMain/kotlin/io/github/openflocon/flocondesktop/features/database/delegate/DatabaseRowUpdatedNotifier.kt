package io.github.openflocon.flocondesktop.features.database.delegate

import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** Tells the query tabs that a row was updated from the detail panel, so that they read it again. */
class DatabaseRowUpdatedNotifier {
    private val _updates = MutableSharedFlow<DatabaseRowEditTargetUiModel>(extraBufferCapacity = 16)
    val updates: SharedFlow<DatabaseRowEditTargetUiModel> = _updates.asSharedFlow()

    fun notifyUpdated(target: DatabaseRowEditTargetUiModel) {
        _updates.tryEmit(target)
    }
}
