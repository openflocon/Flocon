package io.github.openflocon.flocondesktop.common.log

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class LogLevel { DEBUG, ERROR }

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val timestamp: Instant,
)

data class LogEntryUiModel(
    val level: LogLevel,
    val message: String,
    val timestamp: String,
)

fun LogEntry.toUiModel(): LogEntryUiModel {
    val local = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatted = "%02d:%02d:%02d".format(local.hour, local.minute, local.second)
    return LogEntryUiModel(level = level, message = message, timestamp = formatted)
}

class LogManager {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    fun d(tag: String, message: String) {
        append(LogLevel.DEBUG, "[$tag] $message")
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        val suffix = throwable?.message?.let { ": $it" } ?: ""
        append(LogLevel.ERROR, "[$tag] $message$suffix")
    }

    fun clear() {
        _logs.value = emptyList()
    }

    private fun append(level: LogLevel, message: String) {
        _logs.update { (it + LogEntry(level, message, Clock.System.now())).takeLast(MAX_ENTRIES) }
    }

    companion object {
        private const val MAX_ENTRIES = 200
    }
}
