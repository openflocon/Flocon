package io.github.openflocon.flocon.database.core.model

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse
import java.util.Locale

actual fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse {
    var database: SQLiteDatabase? = null
    return try {
        database = SQLiteDatabase.openDatabase(
            path,
            null,
            SQLiteDatabase.OPEN_READWRITE
        )
        executeSQLInternal(database, query)
    } catch (t: Throwable) {
        DatabaseExecuteSqlResponse.Error(
            message = t.message ?: "error on executeSQL",
            originalSql = query,
        )
    } finally {
        database?.close()
    }
}

private fun executeSQLInternal(
    database: SQLiteDatabase,
    query: String
): DatabaseExecuteSqlResponse {
    val firstWord = query.trim().let {
        val idx = it.indexOf(' ')
        if (idx >= 0) it.substring(0, idx) else it
    }.uppercase(Locale.getDefault())

    return when (firstWord) {
        "SELECT", "PRAGMA", "EXPLAIN" -> executeSelect(database, query)
        "INSERT" -> executeInsert(database, query)
        "UPDATE", "DELETE" -> executeUpdateDelete(database, query)
        else -> executeRawQuery(database, query)
    }
}

private fun executeSelect(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val cursor: Cursor = database.rawQuery(query, null)
    return try {
        val columnNames = cursor.columnNames.toList()
        val rows = mutableListOf<List<String?>>()
        while (cursor.moveToNext()) {
            val values = mutableListOf<String?>()
            for (i in 0 until cursor.columnCount) {
                values.add(
                    when (cursor.getType(i)) {
                        Cursor.FIELD_TYPE_NULL -> null
                        Cursor.FIELD_TYPE_INTEGER -> cursor.getLong(i).toString()
                        Cursor.FIELD_TYPE_FLOAT -> cursor.getDouble(i).toString()
                        Cursor.FIELD_TYPE_BLOB -> cursor.getBlob(i).toString()
                        else -> cursor.getString(i)
                    }
                )
            }
            rows.add(values)
        }
        DatabaseExecuteSqlResponse.Select(columns = columnNames, values = rows)
    } finally {
        cursor.close()
    }
}

private fun executeInsert(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val insertedId: Long = statement.executeInsert()
    return DatabaseExecuteSqlResponse.Insert(insertedId)
}

private fun executeUpdateDelete(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    val statement = database.compileStatement(query)
    val count: Int = statement.executeUpdateDelete()
    return DatabaseExecuteSqlResponse.UpdateDelete(count)
}

private fun executeRawQuery(
    database: SQLiteDatabase,
    query: String,
): DatabaseExecuteSqlResponse {
    database.execSQL(query)
    return DatabaseExecuteSqlResponse.RawSuccess
}