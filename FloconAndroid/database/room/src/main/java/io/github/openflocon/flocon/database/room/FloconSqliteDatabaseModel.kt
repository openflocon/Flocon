package io.github.openflocon.flocon.database.room

import androidx.sqlite.db.SupportSQLiteDatabase

internal class FloconSqliteDatabaseModel(
    override val displayName: String,
    override val database: SupportSQLiteDatabase
) : FloconAndroidSqlDatabaseModel
