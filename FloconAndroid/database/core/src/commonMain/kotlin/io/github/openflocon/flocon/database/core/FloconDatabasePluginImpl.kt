package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconEncoder
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.database.core.datasource.FloconDatabaseProvider
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DatabaseQueryLogModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.DeviceDataBaseDataModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.QueryResultDataModel
import io.github.openflocon.flocon.database.core.model.fromdevice.sql.listDeviceDataBaseDataModelToJson
import io.github.openflocon.flocon.database.core.model.todevice.DatabaseQueryMessage
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString

internal class FloconDatabasePluginImpl(
    private var sender: FloconMessageSender,
    private val scope: CoroutineScope,
    @FloconMarker
    override val providers: List<FloconDatabaseProvider>
) : FloconPlugin, FloconDatabasePlugin {

    override val key: String = Protocol.FromDevice.Database.Plugin

    private val registeredDatabases = MutableStateFlow<List<FloconDatabaseModel>>(emptyList())

    override suspend fun onMessageReceived(
        method: String,
        body: String
    ) {
        println("Database: $method & $body")
        when (method) {
            Protocol.ToDevice.Database.Method.GetDatabases -> sendAllDatabases(sender)

            Protocol.ToDevice.Database.Method.Query -> {
                val queryMessage = DatabaseQueryMessage.fromJson(message = body) ?: return
                val databaseModel = registeredDatabases.value
                    .find { it.id == queryMessage.database }

                val result = databaseModel?.executeQuery(query = queryMessage.query)
                    ?: DatabaseExecuteSqlResponse.Error(
                        message = "Database not found",
                        originalSql = queryMessage.query,
                    )

                try {
                    sender.send(
                        plugin = Protocol.FromDevice.Database.Plugin,
                        method = Protocol.FromDevice.Database.Method.Query,
                        body = QueryResultDataModel(
                            requestId = queryMessage.requestId,
                            result = FloconEncoder.json.encodeToString(result)
                        )
                            .toJson()
                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("Database parsing error", t)
                }
            }
        }
    }

    override suspend fun onConnectedToServer() {
        sendAllDatabases(sender)
    }

    @OptIn(FloconMarker::class)
    private suspend fun sendAllDatabases(sender: FloconMessageSender) {
        val databases = providers.flatMap { it.getAllDataBases(emptyList()) }
        val all = registeredDatabases.updateAndGet { it + databases }
            .map { DeviceDataBaseDataModel(id = it.id, name = it.displayName) }

        try {
            sender.send(
                plugin = Protocol.FromDevice.Database.Plugin,
                method = Protocol.FromDevice.Database.Method.GetDatabases,
                body = listDeviceDataBaseDataModelToJson(all),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Database parsing error", t)
        }
    }

    override fun register(floconDatabaseModel: FloconDatabaseModel) {
        registeredDatabases.update { it + floconDatabaseModel }
    }

    override fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>) {
        scope.launch {
            try {
                sender.send(
                    plugin = Protocol.FromDevice.Database.Plugin,
                    method = Protocol.FromDevice.Database.Method.LogQuery,
                    body = DatabaseQueryLogModel(
                        dbName = dbName,
                        sqlQuery = sqlQuery,
                        bindArgs = bindArgs.map { it.toString() },
                        timestamp = currentTimeMillis()
                    )
                        .toJson()
                )
            } catch (t: Throwable) {
                FloconLogger.logError("Database logging error", t)
            }
        }
    }

    companion object {
        var plugin: FloconDatabasePlugin? = null
    }
}