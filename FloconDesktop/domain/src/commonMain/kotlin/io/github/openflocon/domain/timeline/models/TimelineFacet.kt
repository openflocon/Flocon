package io.github.openflocon.domain.timeline.models

/**
 * A way to narrow the events of one [source] down to some values, ex: the GET and POST calls of the network.
 * Nothing selected for a facet means no restriction.
 */
enum class TimelineFacet(val source: TimelineSource) {
    NetworkMethod(TimelineSource.Network),
    NetworkStatus(TimelineSource.Network), // 1xx, 2xx, 4xx, 5xx
    DatabaseName(TimelineSource.Database),
    AnalyticsTable(TimelineSource.Analytics),
    AccessibilityAction(TimelineSource.Accessibility),
}
