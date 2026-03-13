package io.github.openflocon.flocon.database.room

import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper

//data class FloconRoomDatabaseModel(
//    override val displayName: String,
//    override val database: SQLiteDatabase
//) : FloconAndroidSqlDatabaseModel

fun floconRegisterDatabase(displayName: String, database: SQLiteDatabase) {
//    Flocon.databasePlugin.register(
//        FloconRoomDatabaseModel(
//            displayName = displayName,
//            database = database
//        )
//    )
}

fun floconRegisterDatabase(displayName: String, openHelper: SupportSQLiteOpenHelper) {
//    floconRegisterDatabase(
//        displayName = displayName,
//        database = openHelper.writableDatabase.,
//    )
}
