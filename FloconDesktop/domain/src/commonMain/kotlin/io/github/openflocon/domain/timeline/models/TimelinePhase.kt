package io.github.openflocon.domain.timeline.models

// a network call is two events of the timeline: its request and its response
enum class TimelinePhase {
    Request,
    Response,
}
