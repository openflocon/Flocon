package io.github.openflocon.flocon.database.room

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.database.core.databasePlugin
import io.github.openflocon.flocon.database.core.model.FloconAndroidSqlDatabaseModel

data class FloconRoomDatabaseModel(
    override val displayName: String,
    val database: SupportSQLiteDatabase
) : FloconAndroidSqlDatabaseModel

fun floconRegisterDatabase(displayName: String, database: SupportSQLiteDatabase) {
    Flocon.databasePlugin.register(
        FloconRoomDatabaseModel(
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
