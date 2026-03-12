package io.github.openflocon.flocon.database.core.model

import androidx.sqlite.db.SupportSQLiteDatabase

interface FloconDatabaseModel {
    val displayName: String
}

interface FloconAndroidSqlDatabaseModel : FloconDatabaseModel {
    val database: SupportSQLiteDatabase
}

data class FloconFileDatabaseModel(
    override val displayName: String,
    val absolutePath: String
) : FloconDatabaseModel
