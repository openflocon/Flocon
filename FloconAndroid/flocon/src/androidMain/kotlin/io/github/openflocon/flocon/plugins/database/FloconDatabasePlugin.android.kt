package io.github.openflocon.flocon.plugins.database

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.plugins.database.model.fromdevice.DeviceDataBaseDataModel
import java.io.File
import java.util.Locale

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceAndroid(context.appContext)
}

internal class FloconDatabaseDataSourceAndroid(private val context: Context) : FloconDatabaseDataSource {

    private val MAX_DEPTH = 7

    override fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
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

    override fun getAllDataBases(
        registeredDatabases: List<FloconDatabaseModel>
    ): List<DeviceDataBaseDataModel> {
        val databasesDir = context.getDatabasePath("dummy_db").parentFile ?: return emptyList()

        val foundDatabases = mutableListOf<DeviceDataBaseDataModel>()
        // Start the recursive search from the base databases directory
        scanDirectoryForDatabases(
            directory = databasesDir,
            depth = 0,
            foundDatabases = foundDatabases
        )

        registeredDatabases.forEach {
            if(File(it.absolutePath).exists()) {
                foundDatabases.add(
                    DeviceDataBaseDataModel(
                        id = it.absolutePath,
                        name = it.displayName,
                    )
                )
            }
        }

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