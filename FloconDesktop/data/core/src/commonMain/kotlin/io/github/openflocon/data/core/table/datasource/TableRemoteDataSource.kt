package io.github.openflocon.data.core.table.datasource

import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.table.models.TableDomainModel

interface TableRemoteDataSource {
    fun getItems(message: FloconIncomingMessageDomainModel): List<TableDomainModel>

}
