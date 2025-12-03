package com.flocon.data.remote.crashreporter.datasource

import com.flocon.data.remote.crashreporter.mapper.toDomain
import com.flocon.data.remote.crashreporter.model.CrashReportDataModel
import io.github.openflocon.data.core.crashreporter.datasources.CrashReportRemoteDataSource
import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.serialization.json.Json

class CrashReportRemoteDataSourceImpl(
    private val json: Json,
) : CrashReportRemoteDataSource {

    override fun getItems(message: FloconIncomingMessageDomainModel): List<CrashReportDomainModel> = decodeAddItems(message).takeIf { it.isNotEmpty() }
        ?.let { list ->
            list.map {
                it.toDomain()
            }
        }
        ?.takeIf { it.isNotEmpty() }
        .orEmpty()

    private fun decodeAddItems(message: FloconIncomingMessageDomainModel): List<CrashReportDataModel> = try {
        json.decodeFromString<List<CrashReportDataModel>>(message.body)
    } catch (t: Throwable) {
        t.printStackTrace()
        emptyList()
    }
}
