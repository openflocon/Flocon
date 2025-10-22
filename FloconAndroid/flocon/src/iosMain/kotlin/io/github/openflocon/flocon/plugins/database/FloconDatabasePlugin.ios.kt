package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceIOs()
}

internal class FloconDatabaseDataSourceIOs : FloconDatabaseDataSource {
    override fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        return DatabaseExecuteSqlResponse.Error(message = "not implemented", query)
    }

    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<DeviceDataBaseDataModel> {
        return emptyList()
    }

}