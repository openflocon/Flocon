package io.github.openflocon.flocon.database.core.model

import io.github.openflocon.flocon.database.core.model.fromdevice.DatabaseExecuteSqlResponse

actual fun openDbAndExecuteQuery(
    path: String,
    query: String
): DatabaseExecuteSqlResponse {
//    var helper: SupportSQLiteOpenHelper? = null
//    return try {
//        val path = context.getDatabasePath(databaseName)
//        val version = getDatabaseVersion(path = path.absolutePath)
//        helper = FrameworkSQLiteOpenHelperFactory().create(
//            SupportSQLiteOpenHelper.Configuration.builder(context)
//                .name(path.absolutePath)
//                .callback(object : SupportSQLiteOpenHelper.Callback(version) {
//                    override fun onCreate(db: SupportSQLiteDatabase) {
//                        // no op
//                    }
//
//                    override fun onUpgrade(
//                        db: SupportSQLiteDatabase,
//                        oldVersion: Int,
//                        newVersion: Int
//                    ) {
//                        // no op
//                    }
//                })
//                .build()
//        )
//        val database = helper.writableDatabase
//
//        executeSQL(
//            database = database,
//            query = query,
//        )
//    } catch (t: Throwable) {
//        DatabaseExecuteSqlResponse.Error(
//            message = t.message ?: "error on executeSQL",
//            originalSql = query,
//        )
//    } finally {
//        helper?.close()
//    }
    TODO()
}