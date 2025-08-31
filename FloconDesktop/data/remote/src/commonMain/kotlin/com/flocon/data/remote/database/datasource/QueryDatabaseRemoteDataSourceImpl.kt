package com.flocon.data.remote.database.datasource

import co.touchlab.kermit.Logger
import com.flocon.data.remote.database.mapper.decodeDeviceDatabases
import com.flocon.data.remote.database.mapper.decodeReceivedQuery
import com.flocon.data.remote.database.models.DatabaseExecuteSqlResponseDataModel
import com.flocon.data.remote.database.models.DatabaseOutgoingQueryMessage
import com.flocon.data.remote.database.models.ResponseAndRequestIdDataModel
import com.flocon.data.remote.database.models.toDeviceDatabasesDomain
import com.flocon.data.remote.models.FloconOutgoingMessageDataModel
import com.flocon.data.remote.models.toRemote
import com.flocon.data.remote.server.Server
import com.flocon.data.remote.server.newRequestId
import io.github.openflocon.data.core.database.datasource.QueryDatabaseRemoteDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import io.github.openflocon.domain.database.models.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseDomainModel
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.models.ResponseAndRequestIdDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class QueryDatabaseRemoteDataSourceImpl(
    private val server: Server,
    private val json: Json,
) : QueryDatabaseRemoteDataSource {
    private val queryResultReceived = MutableStateFlow<Set<ResponseAndRequestIdDomainModel>>(emptySet())

    override fun onQueryResultReceived(received: ResponseAndRequestIdDomainModel) {
        queryResultReceived.update { it + received }
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun executeQuery(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Exception, DatabaseExecuteSqlResponseDomainModel> {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toRemote(),
            message = FloconOutgoingMessageDataModel(
                plugin = Protocol.ToDevice.Database.Plugin,
                method = Protocol.ToDevice.Database.Method.Query,
                body =
                Json.encodeToString<DatabaseOutgoingQueryMessage>(
                    DatabaseOutgoingQueryMessage(
                        requestId = requestId,
                        query = query,
                        database = databaseId,
                    ),
                ),
            ),
        )
        // wait for result
        try {
            val result =
                withTimeout(3_000) {
                    queryResultReceived
                        .mapNotNull { it.firstOrNull { it.requestId == requestId } }
                        .first()
                }
            return Success(result.response)
        } catch (e: TimeoutCancellationException) {
            Logger.e(e) { "Timeout: No response for the DB request $requestId" }
            return Failure(e)
        } catch (e: Exception) {
            Logger.e(e) { "Unknown exception : ${e.message}" }
            return Failure(e)
        }
    }

    override fun getDeviceDatabases(message: FloconIncomingMessageDomainModel): List<DeviceDataBaseDomainModel> = toDeviceDatabasesDomain(json.decodeDeviceDatabases(message.body).orEmpty())

    override fun getReceiveQuery(message: FloconIncomingMessageDomainModel): ResponseAndRequestIdDomainModel? = json.decodeReceivedQuery(message.body)?.toDomain()
}

// TODO internal
fun ResponseAndRequestIdDataModel.toDomain() = ResponseAndRequestIdDomainModel(
    requestId = requestId,
    response = response.toDomain(),
)

fun DatabaseExecuteSqlResponseDataModel.toDomain(): DatabaseExecuteSqlResponseDomainModel = when (this) {
    is DatabaseExecuteSqlResponseDataModel.Error ->
        DatabaseExecuteSqlResponseDomainModel.Error(
            message = message,
            originalSql = originalSql,
        )

    is DatabaseExecuteSqlResponseDataModel.Insert ->
        DatabaseExecuteSqlResponseDomainModel.Insert(
            insertedId = insertedId,
        )

    DatabaseExecuteSqlResponseDataModel.RawSuccess -> DatabaseExecuteSqlResponseDomainModel.RawSuccess
    is DatabaseExecuteSqlResponseDataModel.Select ->
        DatabaseExecuteSqlResponseDomainModel.Select(
            columns = columns,
            values = values,
        )

    is DatabaseExecuteSqlResponseDataModel.UpdateDelete ->
        DatabaseExecuteSqlResponseDomainModel.UpdateDelete(
            affectedCount = affectedCount,
        )
}
