package io.github.openflocon.flocon.plugins.database

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.QueryResultDataModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.listDeviceDataBaseDataModelToJson
import io.github.openflocon.flocon.plugins.database.model.fromdevice.toJson
import io.github.openflocon.flocon.plugins.database.model.todevice.DatabaseQueryMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal interface FloconDatabaseDataSource {
    fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse

    fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel>
}

internal expect fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource

internal class FloconDatabasePluginImpl(
    private var sender: FloconMessageSender,
    private val context: FloconContext,
) : FloconPlugin, FloconDatabasePlugin {

    private val registeredDatabases = MutableStateFlow<List<FloconDatabaseModel>>(emptyList())

    private val dataSource = buildFloconDatabaseDataSource(context)

    override fun onMessageReceived(
        messageFromServer: FloconMessageFromServer,
    ) {
        when (messageFromServer.method) {
            Protocol.ToDevice.Database.Method.GetDatabases -> {
                sendAllDatabases(sender)
            }

            Protocol.ToDevice.Database.Method.Query -> {
                val queryMessage =
                    DatabaseQueryMessage.fromJson(message = messageFromServer.body) ?: return
                val result = dataSource.executeSQL(
                    registeredDatabases = registeredDatabases.value,
                    databaseName = queryMessage.database,
                    query = queryMessage.query,
                )
                try {
                    sender.send(
                        plugin = Protocol.FromDevice.Database.Plugin,
                        method = Protocol.FromDevice.Database.Method.Query,
                        body = QueryResultDataModel(
                            requestId = queryMessage.requestId,
                            result = result.toJson(),
                        ).toJson(),
                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("Database parsing error", t)
                }
            }
        }
    }

    override fun onConnectedToServer() {
        sendAllDatabases(sender)
    }

    private fun sendAllDatabases(sender: FloconMessageSender) {
        val databases = dataSource.getAllDataBases(
            registeredDatabases = registeredDatabases.value,
        )
        try {
            sender.send(
                plugin = Protocol.FromDevice.Database.Plugin,
                method = Protocol.FromDevice.Database.Method.GetDatabases,
                body = listDeviceDataBaseDataModelToJson(databases),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Database parsing error", t)
        }
    }

    override fun register(floconDatabaseModel: FloconDatabaseModel) {
        registeredDatabases.update { it + floconDatabaseModel }
    }
}