package io.github.openflocon.domain.timeline.models

// apps can log millions of queries: only the most recent ones are merged in the timeline
const val TIMELINE_MAX_DATABASE_LOGS = 20_000

// the events of one read of the timeline, newest first: the older ones are not returned
const val TIMELINE_MAX_EVENTS = 50_000
