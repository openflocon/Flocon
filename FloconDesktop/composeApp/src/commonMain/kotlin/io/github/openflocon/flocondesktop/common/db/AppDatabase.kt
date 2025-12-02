package io.github.openflocon.flocondesktop.common.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.openflocon.data.local.adb.dao.AdbDevicesDao
import io.github.openflocon.data.local.adb.model.DeviceWithSerialEntity
import io.github.openflocon.data.local.analytics.dao.FloconAnalyticsDao
import io.github.openflocon.data.local.analytics.models.AnalyticsItemEntity
import io.github.openflocon.data.local.dashboard.dao.FloconDashboardDao
import io.github.openflocon.data.local.dashboard.models.DashboardContainerEntity
import io.github.openflocon.data.local.dashboard.models.DashboardElementEntity
import io.github.openflocon.data.local.dashboard.models.DashboardEntity
import io.github.openflocon.data.local.database.dao.QueryDao
import io.github.openflocon.data.local.database.dao.TablesDao
import io.github.openflocon.data.local.database.models.DatabaseTableEntity
import io.github.openflocon.data.local.database.models.FavoriteQueryEntity
import io.github.openflocon.data.local.database.models.SuccessQueryEntity
import io.github.openflocon.data.local.deeplink.dao.FloconDeeplinkDao
import io.github.openflocon.data.local.deeplink.models.DeeplinkEntity
import io.github.openflocon.data.local.device.datasource.dao.DevicesDao
import io.github.openflocon.data.local.device.datasource.model.DeviceAppEntity
import io.github.openflocon.data.local.device.datasource.model.DeviceEntity
import io.github.openflocon.data.local.files.dao.FloconFileDao
import io.github.openflocon.data.local.files.models.FileEntity
import io.github.openflocon.data.local.files.models.FileOptionsEntity
import io.github.openflocon.data.local.images.dao.FloconImageDao
import io.github.openflocon.data.local.images.models.DeviceImageEntity
import io.github.openflocon.data.local.network.dao.FloconNetworkDao
import io.github.openflocon.data.local.network.dao.NetworkBadQualityConfigDao
import io.github.openflocon.data.local.network.dao.NetworkFilterDao
import io.github.openflocon.data.local.network.dao.NetworkMocksDao
import io.github.openflocon.data.local.network.dao.NetworkSettingsDao
import io.github.openflocon.data.local.network.models.FloconNetworkCallEntity
import io.github.openflocon.data.local.network.models.NetworkFilterEntity
import io.github.openflocon.data.local.network.models.NetworkSettingsEntity
import io.github.openflocon.data.local.network.models.badquality.BadQualityConfigEntity
import io.github.openflocon.data.local.network.models.mock.MockNetworkEntity
import io.github.openflocon.data.local.table.dao.FloconTableDao
import io.github.openflocon.data.local.table.models.TableEntity
import io.github.openflocon.data.local.table.models.TableItemEntity
import io.github.openflocon.flocondesktop.common.db.converters.DashboardConverters
import io.github.openflocon.flocondesktop.common.db.converters.ListStringsConverters
import io.github.openflocon.flocondesktop.common.db.converters.MapStringsConverters
import kotlinx.coroutines.Dispatchers

@Database(
    version = 73,
    entities = [
        FloconNetworkCallEntity::class,
        FileEntity::class,
        FileOptionsEntity::class,
        DashboardEntity::class,
        DashboardContainerEntity::class,
        DashboardElementEntity::class,
        TableEntity::class,
        TableItemEntity::class,
        DeviceImageEntity::class,
        SuccessQueryEntity::class,
        FavoriteQueryEntity::class,
        DeeplinkEntity::class,
        AnalyticsItemEntity::class,
        NetworkFilterEntity::class,
        NetworkSettingsEntity::class,
        MockNetworkEntity::class,
        DeviceWithSerialEntity::class,
        BadQualityConfigEntity::class,
        DeviceEntity::class,
        DeviceAppEntity::class,
        DatabaseTableEntity::class,
    ],
)
@TypeConverters(
    DashboardConverters::class,
    MapStringsConverters::class,
    ListStringsConverters::class,
)
abstract class AppDatabase : RoomDatabase() {
    abstract val networkDao: FloconNetworkDao
    abstract val networkSettingsDao: NetworkSettingsDao
    abstract val fileDao: FloconFileDao
    abstract val dashboardDao: FloconDashboardDao
    abstract val tableDao: FloconTableDao
    abstract val imageDao: FloconImageDao
    abstract val queryDao: QueryDao
    abstract val deeplinkDao: FloconDeeplinkDao
    abstract val analyticsDao: FloconAnalyticsDao
    abstract val networkFilterDao: NetworkFilterDao
    abstract val networkMocksDao: NetworkMocksDao
    abstract val adbDevicesDao: AdbDevicesDao
    abstract val networkBadQualityConfigDao: NetworkBadQualityConfigDao
    abstract val devicesDao: DevicesDao
    abstract val tablesDao: TablesDao
}

fun getRoomDatabase(): AppDatabase = getDatabaseBuilder()
    .fallbackToDestructiveMigration(dropAllTables = true)
    .setDriver(BundledSQLiteDriver())
    .setQueryCoroutineContext(Dispatchers.IO)
    .build()

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
