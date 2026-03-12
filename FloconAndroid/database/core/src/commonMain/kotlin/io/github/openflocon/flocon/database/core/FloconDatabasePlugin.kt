package io.github.openflocon.flocon.database.core

import io.github.openflocon.flocon.FloconConfig
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.FloconPlugin
import io.github.openflocon.flocon.FloconPluginConfig
import io.github.openflocon.flocon.FloconPluginFactory
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DeviceDataBaseDataModel
import io.github.openflocon.flocon.database.core.model.todevice.DatabaseQueryMessage
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.dsl.FloconMarker
import io.github.openflocon.flocon.error.pluginNotInitialized
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FloconDatabaseConfig : FloconPluginConfig

interface FloconDatabasePlugin : FloconPlugin {
    fun register(floconDatabaseModel: FloconDatabaseModel)
    fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>)
}

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

object FloconDatabase : FloconPluginFactory<FloconDatabaseConfig, FloconDatabasePlugin> {
    override val name: String = "Database"
    override val pluginId: String = Protocol.ToDevice.Database.Plugin
    override fun createConfig() = FloconDatabaseConfig()
    override fun install(
        pluginConfig: FloconDatabaseConfig,
        floconConfig: FloconConfig
    ): FloconDatabasePlugin {
        return FloconDatabasePluginImpl(
            sender = floconConfig.client as FloconMessageSender,
            context = floconConfig.context
        ).also { FloconDatabasePluginImpl.plugin = it }
    }
}

internal class FloconDatabasePluginImpl(
    private var sender: FloconMessageSender,
    private val context: FloconContext,
) : FloconPlugin, FloconDatabasePlugin {
    override val key: String = "DATABASE"

    companion object {
        var plugin: FloconDatabasePlugin? = null
    }

    private val registeredDatabases = MutableStateFlow<List<FloconDatabaseModel>>(emptyList())

    private val dataSource = buildFloconDatabaseDataSource(context)

    override suspend fun onMessageReceived(
        method: String,
        body: String,
    ) {
        when (method) {
            Protocol.ToDevice.Database.Method.GetDatabases -> {
                sendAllDatabases(sender)
            }

            Protocol.ToDevice.Database.Method.Query -> {
                val queryMessage =
                    DatabaseQueryMessage.fromJson(message = body) ?: return
                val result = dataSource.executeSQL(
                    registeredDatabases = registeredDatabases.value,
                    databaseName = queryMessage.database,
                    query = queryMessage.query,
                )
                try {
//                    sender.send(
//                        plugin = Protocol.FromDevice.Database.Plugin,
//                        method = Protocol.FromDevice.Database.Method.Query,
//                        body = QueryResultDataModel(
//                            requestId = queryMessage.requestId,
//                            result = result.toJson(),
//                        ).toJson(),
//                    )
                } catch (t: Throwable) {
                    FloconLogger.logError("Database parsing error", t)
                }
            }
        }
    }

    override suspend fun onConnectedToServer() {
        sendAllDatabases(sender)
    }

    private fun sendAllDatabases(sender: FloconMessageSender) {
        val databases = dataSource.getAllDataBases(
            registeredDatabases = registeredDatabases.value,
        )
        try {
//            sender.send(
//                plugin = Protocol.FromDevice.Database.Plugin,
//                method = Protocol.FromDevice.Database.Method.GetDatabases,
//                body = listDeviceDataBaseDataModelToJson(databases),
//            )
        } catch (t: Throwable) {
            FloconLogger.logError("Database parsing error", t)
        }
    }

    override fun register(floconDatabaseModel: FloconDatabaseModel) {
        registeredDatabases.update { it + floconDatabaseModel }
    }

    override fun logQuery(dbName: String, sqlQuery: String, bindArgs: List<Any?>) {
        try {
//            sender.send(
//                plugin = Protocol.FromDevice.Database.Plugin,
//                method = Protocol.FromDevice.Database.Method.LogQuery,
//                body = DatabaseQueryLogModel(
//                    dbName = dbName,
//                    sqlQuery = sqlQuery,
//                    bindArgs = bindArgs.map { it.toString() },
//                    timestamp = currentTimeMillis(),
//                ).toJson(),
//            )
        } catch (t: Throwable) {
            FloconLogger.logError("Database logging error", t)
        }
    }
}

@OptIn(FloconMarker::class)
val Flocon.Companion.databasePlugin: FloconDatabasePlugin
    get() = FloconDatabasePluginImpl.plugin ?: pluginNotInitialized("Database")
