package io.github.openflocon.flocon.database.room

import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

internal class FloconSqliteDatabaseModel(
    override val displayName: String,
    override val database: SQLiteDatabase//SupportSQLiteDatabase
) : FloconAndroidSqlDatabaseModel
