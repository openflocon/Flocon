package io.github.openflocon.flocon.database.core.datasource

import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel

interface FloconDatabaseDataSource {

    suspend fun executeQuery(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteResponse?

    fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel>

}