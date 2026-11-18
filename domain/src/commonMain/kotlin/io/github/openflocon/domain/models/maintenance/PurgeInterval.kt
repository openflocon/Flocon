package io.github.openflocon.domain.models.maintenance

enum class PurgeInterval(val days: Int) {
    OneDay(1),
    ThreeDays(3),
    OneWeek(7),
    TwoWeeks(14),
    OneMonth(30),
    Everything(0);

    companion object {
        val DEFAULT = OneWeek
    }
}
