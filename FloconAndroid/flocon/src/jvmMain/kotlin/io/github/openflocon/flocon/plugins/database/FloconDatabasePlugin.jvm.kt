package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceJvm()
}

internal class FloconDatabaseDataSourceJvm : FloconDatabaseDataSource {
    override fun executeSQL(
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        return DatabaseExecuteSqlResponse.Error(
            message = "Not implemented",
            originalSql = query,
        )
    }

    override fun getAllDataBases(): List<DeviceDataBaseDataModel> {
       return emptyList()
    }

}