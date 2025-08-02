package io.github.openflocon.flocondesktop.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.openflocon.flocondesktop.common.db.converters.ListStringsConverters
import io.github.openflocon.flocondesktop.common.db.converters.MapStringsConverters
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.FloconAnalyticsDao
import io.github.openflocon.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.FloconDashboardDao
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import io.github.openflocon.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.QueryDao
import io.github.openflocon.flocondesktop.features.database.data.datasource.local.SuccessQueryEntity
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.FloconDeeplinkDao
import io.github.openflocon.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity
import io.github.openflocon.flocondesktop.features.files.data.datasources.FloconFileDao
import io.github.openflocon.flocondesktop.features.files.data.datasources.model.FileEntity
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.GrpcDao
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallEntity
import io.github.openflocon.flocondesktop.features.grpc.data.datasource.room.model.GrpcResponseEntity
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.FloconImageDao
import io.github.openflocon.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.FloconHttpRequestDao
import io.github.openflocon.flocondesktop.features.network.data.datasource.local.model.FloconHttpRequestEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.FloconTableDao
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableEntity
import io.github.openflocon.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import kotlinx.coroutines.Dispatchers

@Database(
    version = 23,
    entities = [
        FloconHttpRequestEntity::class,
        FileEntity::class,
        DashboardEntity::class,
        DashboardSectionEntity::class,
        DashboardElementEntity::class,
        TableEntity::class,
        TableItemEntity::class,
        DeviceImageEntity::class,
        SuccessQueryEntity::class,
        DeeplinkEntity::class,
        GrpcCallEntity::class,
        GrpcResponseEntity::class,
        AnalyticsItemEntity::class,
    ],
)
@TypeConverters(
    MapStringsConverters::class,
    ListStringsConverters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val httpRequestDao: FloconHttpRequestDao
    abstract val fileDao: FloconFileDao
    abstract val dashboardDao: FloconDashboardDao
    abstract val tableDao: FloconTableDao
    abstract val imageDao: FloconImageDao
    abstract val queryDao: QueryDao
    abstract val deeplinkDao: FloconDeeplinkDao
    abstract val grpcDao: GrpcDao
    abstract val analyticsDao: FloconAnalyticsDao
}

fun getRoomDatabase(): AppDatabase = getDatabaseBuilder()
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
