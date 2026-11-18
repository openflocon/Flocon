package io.github.openflocon.domain.timeline.models

/** Which filter of the user hides an event of the timeline */
enum class TimelineHiddenReason {
    // the source of the event is unchecked
    Source,

    // the text search does not match the event
    Text,

    // a value of a facet (method, status, database...) is hidden
    Facets,

    // the status, host or duration rules of the network calls
    NetworkRules,

    // the event belongs to an older session
    CurrentSessionOnly,

    // no single filter hides it: it takes several of them
    Combination,
}
