package io.github.openflocon.flocon.plugins.database

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.github.openflocon.flocon.plugins.database.model.FloconDatabaseModel

internal class FloconSqliteDatabaseModel(
    override val displayName: String,
    val database: SupportSQLiteDatabase
) : FloconDatabaseModel

fun floconRegisterDatabase(displayName: String, database: SupportSQLiteDatabase) {
    floconRegisterDatabase(
        FloconSqliteDatabaseModel(
            displayName = displayName,
            database = database,
        )
    )
}


fun floconRegisterDatabase(displayName: String, openHelper: SupportSQLiteOpenHelper) {
    floconRegisterDatabase(
        displayName = displayName,
        database = openHelper.writableDatabase,
    )
}