package io.github.openflocon.domain.timeline.models

/**
 * An include / exclude rule typed by the user: a word for a host, a code or a range of codes ("404", "500-503") for a status.
 */
data class TimelineRuleDomainModel(
    val value: String,
    val exclude: Boolean,
)

/**
 * A range of milliseconds, without upper bound when [maxMs] is null.
 */
data class TimelineDurationRangeDomainModel(
    val minMs: Long,
    val maxMs: Long?,
)

/**
 * The status codes matched by a rule: one code, or an inclusive range.
 */
data class TimelineStatusMatcher(
    val from: Int,
    val to: Int,
) {
    // "404" or "500-503", what the user typed and what the rule stores
    fun label(): String = if (from == to) "$from" else "$from-$to"

    companion object {
        /**
         * "404" or "500-503" (spaces allowed). Null for anything else: unknown text, reversed range.
         */
        fun parse(text: String): TimelineStatusMatcher? {
            val parts = text.trim().split("-").map { it.trim() }
            val numbers = parts.map { it.toIntOrNull() ?: return null }
            return when (numbers.size) {
                1 -> TimelineStatusMatcher(numbers[0], numbers[0])
                2 -> numbers.takeIf { it[0] <= it[1] }?.let { TimelineStatusMatcher(it[0], it[1]) }
                else -> null
            }
        }
    }
}

private val DASH_WITH_SPACES = Regex("\\s*-\\s*")

/**
 * "404, 500-503" into matchers, comma or space separated. Null when a part is not a status.
 */
fun parseTimelineStatusRules(text: String): List<TimelineStatusMatcher>? {
    // "500 - 503" is one range, not three parts
    val parts = text.replace(DASH_WITH_SPACES, "-").split(",", " ").map { it.trim() }.filter { it.isNotEmpty() }
    if (parts.isEmpty()) return null
    return parts.map { TimelineStatusMatcher.parse(it) ?: return null }
}
