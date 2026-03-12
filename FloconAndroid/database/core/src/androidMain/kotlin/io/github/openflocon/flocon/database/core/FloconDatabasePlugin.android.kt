package io.github.openflocon.flocon.database.core

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.database.core.model.FloconAndroidSqlDatabaseModel
import io.github.openflocon.flocon.database.core.model.FloconDatabaseModel
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import io.github.openflocon.flocon.database.core.model.fromdevice.DeviceDataBaseDataModel
import java.io.File
import java.util.Locale

internal actual fun buildFloconDatabaseDataSource(context: FloconContext): FloconDatabaseDataSource {
    return FloconDatabaseDataSourceAndroid(context.context)
}

internal class FloconDatabaseDataSourceAndroid(private val context: Context) :
    FloconDatabaseDataSource {

    private val MAX_DEPTH = 7

    override fun executeSQL(
        registeredDatabases: List<FloconDatabaseModel>,
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        val databaseModel = registeredDatabases.find { it.displayName == databaseName }
        return when (databaseModel) {
            is FloconAndroidSqlDatabaseModel -> {
                executeSQL(
                    database = databaseModel.database,
                    query = query,
                )
            }

            else -> openDbAndExecuteQuery(
                databaseName = databaseName,
                query = query,
            )
        }
    }

    private fun openDbAndExecuteQuery(
        databaseName: String,
        query: String
    ): DatabaseExecuteSqlResponse {
        var helper: SupportSQLiteOpenHelper? = null
        return try {
            val path = context.getDatabasePath(databaseName)
            val version = getDatabaseVersion(path = path.absolutePath)
            helper = FrameworkSQLiteOpenHelperFactory().create(
                SupportSQLiteOpenHelper.Configuration.builder(context)
                    .name(path.absolutePath)
                    .callback(object : SupportSQLiteOpenHelper.Callback(version) {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            // no op
                        }

                        override fun onUpgrade(
                            db: SupportSQLiteDatabase,
                            oldVersion: Int,
                            newVersion: Int
                        ) {
                            // no op
                        }
                    })
                    .build()
            )
            val database = helper.writableDatabase

            executeSQL(
                database = database,
                query = query,
            )
        } catch (t: Throwable) {
            DatabaseExecuteSqlResponse.Error(
                message = t.message ?: "error on executeSQL",
                originalSql = query,
            )
        } finally {
            helper?.close()
        }
    }

    private fun executeSQL(
        database: SupportSQLiteDatabase,
        query: String
    ): DatabaseExecuteSqlResponse {
        return try {
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
        }
    }

    override fun getAllDataBases(registeredDatabases: List<FloconDatabaseModel>): List<DeviceDataBaseDataModel> {
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
    database: SupportSQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val cursor: Cursor = database.query(query)
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
    database: SupportSQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val count: Int = statement.executeUpdateDelete()
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private fun executeInsert(
    database: SupportSQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val insertedId: Long = statement.executeInsert()
    return DatabaseExecuteSqlResponse.Insert(insertedId)
}

private fun executeRawQuery(
    database: SupportSQLiteDatabase,
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

private fun getDatabaseVersion(
    path: String,
): Int {
    return SQLiteDatabase.openDatabase(
        path,
        null,
        SQLiteDatabase.OPEN_READONLY
    ).use { db ->
        db.rawQuery("PRAGMA user_version", null).use { cursor ->
            if (cursor.moveToFirst()) cursor.getInt(0) else 0
        }
    }
}
