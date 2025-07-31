package io.github.openflocon.flocondesktop.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.florent37.flocondesktop.common.db.converters.ListStringsConverters
import com.florent37.flocondesktop.common.db.converters.MapStringsConverters
import com.florent37.flocondesktop.features.analytics.data.datasource.local.FloconAnalyticsDao
import com.florent37.flocondesktop.features.analytics.data.datasource.local.model.AnalyticsItemEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.FloconDashboardDao
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardElementEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardEntity
import com.florent37.flocondesktop.features.dashboard.data.datasources.room.model.DashboardSectionEntity
import com.florent37.flocondesktop.features.database.data.datasource.local.QueryDao
import com.florent37.flocondesktop.features.database.data.datasource.local.SuccessQueryEntity
import com.florent37.flocondesktop.features.deeplinks.data.datasource.room.FloconDeeplinkDao
import com.florent37.flocondesktop.features.deeplinks.data.datasource.room.model.DeeplinkEntity
import com.florent37.flocondesktop.features.files.data.datasources.FloconFileDao
import com.florent37.flocondesktop.features.files.data.datasources.model.FileEntity
import com.florent37.flocondesktop.features.graphql.data.datasource.room.GraphQlDao
import com.florent37.flocondesktop.features.graphql.data.datasource.room.model.GraphQlRequestEntity
import com.florent37.flocondesktop.features.grpc.data.datasource.room.GrpcDao
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcCallEntity
import com.florent37.flocondesktop.features.grpc.data.datasource.room.model.GrpcResponseEntity
import com.florent37.flocondesktop.features.images.data.datasources.local.FloconImageDao
import com.florent37.flocondesktop.features.images.data.datasources.local.model.DeviceImageEntity
import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestDao
import com.florent37.flocondesktop.features.network.data.datasource.local.FloconHttpRequestEntity
import com.florent37.flocondesktop.features.table.data.datasource.local.FloconTableDao
import com.florent37.flocondesktop.features.table.data.datasource.local.model.TableEntity
import com.florent37.flocondesktop.features.table.data.datasource.local.model.TableItemEntity
import kotlinx.coroutines.Dispatchers

@Database(
    version = 18,
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
        GraphQlRequestEntity::class,
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
    abstract val graphQlDao: GraphQlDao
}

fun getRoomDatabase(): AppDatabase = getDatabaseBuilder()
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
