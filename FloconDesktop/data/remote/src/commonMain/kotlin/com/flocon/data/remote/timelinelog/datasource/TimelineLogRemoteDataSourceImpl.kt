package com.flocon.data.remote.timelinelog.datasource

import com.flocon.data.remote.timelinelog.model.TimelineLogDataModel
import io.github.openflocon.data.core.timelinelog.datasource.TimelineLogRemoteDataSource
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
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

private val reservedTypes = listOf("navigation", "userAction", "lifecycle")

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
