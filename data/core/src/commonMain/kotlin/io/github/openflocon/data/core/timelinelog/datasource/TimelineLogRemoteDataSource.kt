package io.github.openflocon.data.core.timelinelog.datasource

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel

interface TimelineLogRemoteDataSource {
    fun getLog(message: FloconIncomingMessageDomainModel): TimelineLogDomainModel?
}
