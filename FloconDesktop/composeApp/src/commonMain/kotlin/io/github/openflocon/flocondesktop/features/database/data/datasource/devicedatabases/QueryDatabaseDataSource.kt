package io.github.openflocon.flocondesktop.features.database.data.datasource.devicedatabases

import io.github.openflocon.flocondesktop.FloconOutgoingMessageDataModel
import io.github.openflocon.flocondesktop.Protocol
import io.github.openflocon.flocondesktop.Server
import io.github.openflocon.flocondesktop.common.Either
import io.github.openflocon.flocondesktop.common.Failure
import io.github.openflocon.flocondesktop.common.Success
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.DatabaseExecuteSqlResponse
import io.github.openflocon.flocondesktop.features.database.data.model.incoming.ResponseAndRequestId
import io.github.openflocon.flocondesktop.features.database.data.model.outgoing.DatabaseOutgoingQueryMessage
import io.github.openflocon.flocondesktop.features.database.domain.model.DatabaseExecuteSqlResponseDomainModel
import io.github.openflocon.flocondesktop.features.database.domain.model.DeviceDataBaseId
import io.github.openflocon.flocondesktop.messages.domain.model.DeviceIdAndPackageName
import io.github.openflocon.flocondesktop.messages.domain.model.toFlocon
import io.github.openflocon.flocondesktop.newRequestId
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
        deviceIdAndPackageName: DeviceIdAndPackageName,
        databaseId: DeviceDataBaseId,
        query: String,
    ): Either<Exception, DatabaseExecuteSqlResponseDomainModel> {
        val requestId = newRequestId()
        server.sendMessageToClient(
            deviceIdAndPackageName = deviceIdAndPackageName.toFlocon(),
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
