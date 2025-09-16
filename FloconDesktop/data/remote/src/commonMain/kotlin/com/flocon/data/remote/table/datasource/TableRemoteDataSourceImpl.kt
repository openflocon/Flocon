package com.flocon.data.remote.table.datasource

import com.flocon.data.remote.common.safeDecodeFromString
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.table.mapper.toDomain
import com.flocon.data.remote.table.model.TableItemDataModel
import io.github.openflocon.data.core.table.datasource.TableRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel
import kotlinx.serialization.json.Json

class TableRemoteDataSourceImpl(
    private val json: Json,
) : TableRemoteDataSource {

    override fun getItems(message: FloconIncomingMessageDomainModel): List<TableDomainModel> = json.safeDecodeFromString<List<TableItemDataModel>>(message.body)
        ?.map { toDomain(it) }
        .orEmpty()
}
