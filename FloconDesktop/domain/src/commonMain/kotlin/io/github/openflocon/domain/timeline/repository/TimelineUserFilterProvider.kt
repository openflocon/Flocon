package io.github.openflocon.domain.timeline.repository

import io.github.openflocon.domain.timeline.models.TimelineFilterDomainModel

/**
 * The filter the user is looking at in the timeline, as it is now: the screen can be closed, it never was displayed or
 * another screen is open. Reading it never changes it.
 */
interface TimelineUserFilterProvider {
    suspend fun current(): TimelineFilterDomainModel
}
