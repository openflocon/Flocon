package com.flocon.data.remote.timelinelog.datasource

import com.flocon.data.remote.timelinelog.model.TimelineLogDataModel
import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_LIFECYCLE
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_NAVIGATION
import io.github.openflocon.domain.timelinelog.models.TIMELINE_LOG_TYPE_USER_ACTION
import io.github.openflocon.domain.timelinelog.models.TimelineLogDomainModel
import io.github.openflocon.domain.timelinelog.models.TimelineLogLevel
import kotlinx.serialization.json.Json

class TimelineLogRemoteDataSourceImpl(
    private val json: Json,
) : TimelineLogRemoteDataSource {

    override fun getLog(message: FloconIncomingMessageDomainModel): TimelineLogDomainModel? = try {
        json.decodeFromString<TimelineLogDataModel>(message.body).toDomain()
    } catch (t: Throwable) {
        t.printStackTrace()
        null
    }
}

private val reservedTypes = listOf(TIMELINE_LOG_TYPE_NAVIGATION, TIMELINE_LOG_TYPE_USER_ACTION, TIMELINE_LOG_TYPE_LIFECYCLE)

private fun TimelineLogDataModel.toDomain() = TimelineLogDomainModel(
    id = 0,
    timestamp = timestamp,
    type = reservedTypes.firstOrNull { it.equals(type, ignoreCase = true) } ?: type,
    level = levelToDomain(level),
    value = value,
    params = params,
)

private fun levelToDomain(level: String?) = TimelineLogLevel.entries
    .firstOrNull { it.name.equals(level, ignoreCase = true) }
    ?: TimelineLogLevel.Info
