package com.florent37.flocondesktop.features.database.data.datasource.devicedatabases

import com.florent37.flocondesktop.DeviceId
import com.florent37.flocondesktop.FloconOutgoingMessageDataModel
import com.florent37.flocondesktop.Protocol
import com.florent37.flocondesktop.Server
import com.florent37.flocondesktop.common.Either
import com.florent37.flocondesktop.common.Failure
import com.florent37.flocondesktop.common.Success
import com.florent37.flocondesktop.features.database.data.model.incoming.DatabaseExecuteSqlResponse
import com.florent37.flocondesktop.features.database.data.model.incoming.ResponseAndRequestId
import com.florent37.flocondesktop.features.database.data.model.outgoing.DatabaseOutgoingQueryMessage
import com.florent37.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import com.florent37.flocondesktop.features.database.domain.model.DeviceDataBaseId
import com.florent37.flocondesktop.newRequestId
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withTimeout
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi

class QueryDatabaseDataSource(
    private val server: Server,
) {
    private val queryResultReceived = MutableStateFlow<Set<ResponseAndRequestId>>(emptySet())

    fun onQueryResultReceived(received: ResponseAndRequestId) {
        queryResultReceived.update { it + received }
    }

    @OptIn(ExperimentalUuidApi::class)
    suspend fun executeQuery(
        deviceId: DeviceId,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Exception, DatabaseExecuteSqlResponseDomainModel> {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceId = deviceId,
            message =
            FloconOutgoingMessageDataModel(
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
            return Success(result.response.toDomain())
        } catch (e: TimeoutCancellationException) {
            // Ce bloc est exécuté si le timeout se produit
            println("Timeout: Aucune réponse reçue pour la requête $requestId dans le délai imparti.")
            // Tu peux ajouter ici d'autres logiques de gestion du timeout,
            // comme renvoyer une erreur à l'appelant, journaliser l'événement, etc.
            return Failure(e)
        } catch (e: Exception) {
            // Gère d'autres exceptions qui pourraient survenir
            println("Une erreur inattendue est survenue : ${e.message}")
            return Failure(e)
        }
    }
}

private fun DatabaseExecuteSqlResponse.toDomain(): DatabaseExecuteSqlResponseDomainModel = when (this) {
    is DatabaseExecuteSqlResponse.Error ->
        DatabaseExecuteSqlResponseDomainModel.Error(
            message = message,
            originalSql = originalSql,
        )

    is DatabaseExecuteSqlResponse.Insert ->
        DatabaseExecuteSqlResponseDomainModel.Insert(
            insertedId = insertedId,
        )

    DatabaseExecuteSqlResponse.RawSuccess -> DatabaseExecuteSqlResponseDomainModel.RawSuccess
    is DatabaseExecuteSqlResponse.Select ->
        DatabaseExecuteSqlResponseDomainModel.Select(
            columns = columns,
            values = values,
        )

    is DatabaseExecuteSqlResponse.UpdateDelete ->
        DatabaseExecuteSqlResponseDomainModel.UpdateDelete(
            affectedCount = affectedCount,
        )
}
