package io.github.openflocon.flocondesktop.common.log

import androidx.compose.runtime.Immutable
import io.github.openflocon.domain.log.LogDomain
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

enum class LogLevel { DEBUG, INFO, ERROR }

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val timestamp: Instant,
    val domain: LogDomain,
)

@Immutable
data class LogEntryUiModel(
    val level: LogLevel,
    val message: String,
    val timestamp: String,
    val domain: LogDomain,
)

fun LogEntry.toUiModel(): LogEntryUiModel {
    val local = timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
    val formatted = "%02d:%02d:%02d".format(local.hour, local.minute, local.second)
    return LogEntryUiModel(level = level, message = message, timestamp = formatted, domain = domain)
}

class LogManagerImpl : LogManagerEditable {

    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    override val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    override fun d(tag: String, message: String, domain: LogDomain) {
        append(LogLevel.DEBUG, "[$tag] $message", domain)
    }

    override fun i(tag: String, message: String, domain: LogDomain) {
        append(LogLevel.INFO, "[$tag] $message", domain)
    }

    override fun e(tag: String, message: String, throwable: Throwable?, domain: LogDomain) {
        val suffix = throwable?.message?.let { ": $it" } ?: ""
        append(LogLevel.ERROR, "[$tag] $message$suffix", domain)
    }

    override fun clear() {
        _logs.value = emptyList()
    }

    private fun append(level: LogLevel, message: String, domain: LogDomain) {
        _logs.update { (it + LogEntry(level, message, Clock.System.now(), domain)).takeLast(MAX_ENTRIES) }
    }

    companion object {
        private const val MAX_ENTRIES = 200
    }
}
