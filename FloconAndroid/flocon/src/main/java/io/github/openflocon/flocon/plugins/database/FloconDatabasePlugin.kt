package io.github.openflocon.flocon.plugins.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.QueryResultDataModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.listDeviceDataBaseDataModelToJson
import io.github.openflocon.flocon.plugins.database.model.fromdevice.toJson
import io.github.openflocon.flocon.plugins.database.model.todevice.DatabaseQueryMessage
import java.io.File
import java.util.Locale

internal class FloconDatabasePluginImpl(
    private var sender: FloconMessageSender,
    private val context: Context,
) : FloconPlugin, FloconDatabasePlugin {

    private val MAX_DEPTH = 7

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
                val result = executeSQL(
                    databaseName = queryMessage.database,
                    query = queryMessage.query
                )
                try {
                    sender.send(
                        plugin = Protocol.FromDevice.Database.Plugin,
                        method = Protocol.FromDevice.Database.Method.Query,
                        body = QueryResultDataModel(
                            requestId = queryMessage.requestId,
                            result = result.toJson().toString(),
                        ).toJson().toString(),
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
        val databases = getAllDataBases()
        try {
            sender.send(
                plugin = Protocol.FromDevice.Database.Plugin,
                method = Protocol.FromDevice.Database.Method.GetDatabases,
                body = listDeviceDataBaseDataModelToJson(databases).toString(),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Database parsing error", t)
        }
    }

    private fun getAllDataBases(): List<DeviceDataBaseDataModel> {
        val databasesDir = context.getDatabasePath("dummy_db").parentFile ?: return emptyList()

        val foundDatabases = mutableListOf<DeviceDataBaseDataModel>()
        // Start the recursive search from the base databases directory
        scanDirectoryForDatabases(
            directory = databasesDir,
            depth = 0,
            foundDatabases = foundDatabases
        )

        return foundDatabases
    }

    /**
     * Recursively scans a directory for SQLite database files.
     *
     * @param directory The current directory to scan.
     * @param foundDatabases The mutable list to add found databases to.
     */
    private fun scanDirectoryForDatabases(
        directory: File,
        depth: Int,
        foundDatabases: MutableList<DeviceDataBaseDataModel>
    ) {
        if (depth >= MAX_DEPTH) {
            return;
        }
        directory.listFiles()?.forEach { file ->
            if (file.isDirectory) {
                // If it's a directory, recursively call this function
                scanDirectoryForDatabases(
                    directory = file,
                    depth = depth + 1,
                    foundDatabases = foundDatabases,
                )
            } else {
                // If it's a file, check if it's a database file
                if (file.isFile &&
                    !file.name.endsWith("-wal") && // Write-Ahead Log
                    !file.name.endsWith("-shm") && // Shared-Memory
                    !file.name.endsWith("-journal") // Older journaling mode
                ) {
                    foundDatabases.add(
                        DeviceDataBaseDataModel(
                            id = file.absolutePath, // Use absolute path for unique ID
                            name = file.name,
                        )
                    )
                }
            }
        }
    }

    private fun executeSQL(
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        var database: SQLiteDatabase? = null
        return try {
            val path = context.getDatabasePath(databaseName)
            database =
                SQLiteDatabase.openDatabase(path.absolutePath, null, SQLiteDatabase.OPEN_READWRITE)
            val firstWordUpperCase = getFirstWord(query).uppercase(Locale.getDefault())
            when (firstWordUpperCase) {
                "UPDATE", "DELETE" -> executeUpdateDelete(database, query)
                "INSERT" -> executeInsert(database, query)
                "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(database, query)
                else -> executeRawQuery(database, query)
            }
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "error on executeSQL",
                originalSql = query,
            )
        } finally {
            database?.close()
        }
    }
}

private fun executeSelect(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val cursor: Cursor = database.rawQuery(query, null)
    try {
        val columnNames = cursor.columnNames.toList()
        val rows = cursorToList(cursor)
        return DatabaseExecuteSqlResponse.Select(
            columns = columnNames,
            values = rows,
        )
    } finally {
        cursor.close()
    }
}

private fun executeUpdateDelete(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val count: Int = statement.executeUpdateDelete()
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private fun executeInsert(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val insertedId: Long = statement.executeInsert()
    return DatabaseExecuteSqlResponse.Insert(insertedId)
}

private fun executeRawQuery(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    database.execSQL(query)
    return DatabaseExecuteSqlResponse.RawSuccess
}

private fun getFirstWord(s: String): String {
    var s = s
    s = s.trim { it <= ' ' }
    val firstSpace = s.indexOf(' ')
    return if (firstSpace >= 0) s.substring(0, firstSpace) else s
}

private fun cursorToList(cursor: Cursor): List<List<String?>> {
    val rows = mutableListOf<List<String?>>()
    val numColumns = cursor.columnCount
    while (cursor.moveToNext()) {
        val values = mutableListOf<String?>()
        for (column in 0..<numColumns) {
            values.add(getObjectFromColumnIndex(cursor, column))
        }
        rows.add(values)
    }
    return rows
}

private fun getObjectFromColumnIndex(cursor: Cursor, column: Int): String? {
    return when (cursor.getType(column)) {
        Cursor.FIELD_TYPE_NULL -> null
        Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(column).toString()
        Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(column).toString()
        Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(column).toString()
        Cursor.FIELD_TYPE_STRING -> cursor.getString(column).toString()
        else -> cursor.getString(column)
    }
}