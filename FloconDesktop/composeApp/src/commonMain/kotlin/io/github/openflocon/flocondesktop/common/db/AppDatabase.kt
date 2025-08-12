package io.github.openflocon.flocondesktop.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.openflocon.data.local.analytics.dao.FloconAnalyticsDao
import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
import io.github.openflocon.data.local.dashboard.dao.FloconDashboardDao
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.dashboard.models.DashboardSectionEntity
import io.github.openflocon.data.local.database.dao.QueryDao
import io.github.openflocon.data.local.database.models.SuccessQueryEntity
import io.github.openflocon.data.local.deeplink.dao.FloconDeeplinkDao
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.files.dao.FloconFileDao
import io.github.openflocon.data.local.files.models.FileEntity
import io.github.openflocon.data.local.images.dao.FloconImageDao
import io.github.openflocon.data.local.images.models.DeviceImageEntity
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.dao.NetworkFilterDao
import io.github.openflocon.data.local.network.dao.NetworkMocksDao
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.NetworkFilterEntity
import io.github.openflocon.data.local.network.models.mock.MockNetworkResponseEntity
import io.github.openflocon.data.local.table.dao.FloconTableDao
import io.github.openflocon.data.local.table.models.TableEntity
import io.github.openflocon.data.local.table.models.TableItemEntity
import io.github.openflocon.flocondesktop.common.db.converters.ListStringsConverters
import io.github.openflocon.flocondesktop.common.db.converters.MapStringsConverters
import kotlinx.coroutines.Dispatchers

@Database(
    version = 34,
    entities = [
        FloconNetworkCallEntity::class,
        FileEntity::class,
        DashboardEntity::class,
        DashboardSectionEntity::class,
        DashboardElementEntity::class,
        TableEntity::class,
        TableItemEntity::class,
        DeviceImageEntity::class,
        SuccessQueryEntity::class,
        DeeplinkEntity::class,
        AnalyticsItemEntity::class,
        NetworkFilterEntity::class,
        MockNetworkResponseEntity::class,
    ],
)
@TypeConverters(
    MapStringsConverters::class,
    ListStringsConverters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val networkDao: FloconNetworkDao
    abstract val fileDao: FloconFileDao
    abstract val dashboardDao: FloconDashboardDao
    abstract val tableDao: FloconTableDao
    abstract val imageDao: FloconImageDao
    abstract val queryDao: QueryDao
    abstract val deeplinkDao: FloconDeeplinkDao
    abstract val analyticsDao: FloconAnalyticsDao
    abstract val networkFilterDao: NetworkFilterDao
    abstract val networkMocksDao: NetworkMocksDao
}

fun getRoomDatabase(): AppDatabase = getDatabaseBuilder()
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
