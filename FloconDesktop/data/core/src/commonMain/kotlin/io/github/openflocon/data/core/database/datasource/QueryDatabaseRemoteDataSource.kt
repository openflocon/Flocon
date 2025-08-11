package io.github.openflocon.data.core.database.datasource

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.ResponseAndRequestIdDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import kotlin.uuid.ExperimentalUuidApi

interface QueryDatabaseRemoteDataSource {

    fun onQueryResultReceived(received: ResponseAndRequestIdDomainModel)

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Exception, DatabaseExecuteSqlResponseDomainModel>

}
