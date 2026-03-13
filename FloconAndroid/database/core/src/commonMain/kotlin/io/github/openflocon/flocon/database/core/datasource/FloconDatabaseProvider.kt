package io.github.openflocon.flocon.database.core.datasource

import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel
import io.github.openflocon.flocon.dsl.FloconMarker

interface FloconDatabaseProvider {

    @FloconMarker
    fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel>

}