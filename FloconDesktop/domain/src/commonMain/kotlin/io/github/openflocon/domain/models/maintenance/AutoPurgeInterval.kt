package io.github.openflocon.domain.models.maintenance

/** At each launch of the app, the data older than this is deleted. */
enum class AutoPurgeInterval(val days: Int) {
    Never(0),
    OneDay(1),
    ThreeDays(3),
    OneWeek(7),
    TwoWeeks(14),
    OneMonth(30);

    companion object {
        // the user can switch to Never in the settings
        val DEFAULT = TwoWeeks

        fun fromDays(days: Int?, default: AutoPurgeInterval = DEFAULT): AutoPurgeInterval = entries.firstOrNull { it.days == days } ?: default
    }
}
