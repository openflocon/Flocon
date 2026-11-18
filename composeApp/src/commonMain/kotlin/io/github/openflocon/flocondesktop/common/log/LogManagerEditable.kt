package io.github.openflocon.flocondesktop.common.log

import io.github.openflocon.domain.log.LogManager
import kotlinx.coroutines.flow.StateFlow

/**
 * A [LogManager] whose logs can be read and cleared, used by the logs screen
 */
interface LogManagerEditable : LogManager {
    val logs: StateFlow<List<LogEntry>>

    fun clear()
}
