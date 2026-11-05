package io.github.openflocon.domain.timelinelog.repository

import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import kotlinx.coroutines.flow.Flow

interface TimelineLogRepository {
    fun observeLog(id: Long): Flow<TimelineLogDomainModel?>
}
