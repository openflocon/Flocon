package io.github.openflocon.flocon.database.core

import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.openflocon.flocon.database.core.model.FloconAndroidSqlDatabaseModel

internal class FloconSqliteDatabaseModel(
    override val displayName: String,
    val database: SupportSQLiteDatabase
) : FloconAndroidSqlDatabaseModel
