package io.github.openflocon.data.core.crashreporter.datasources

import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel

interface CrashReportRemoteDataSource {
    fun getItems(message: FloconIncomingMessageDomainModel): List<CrashReportDomainModel>
}
