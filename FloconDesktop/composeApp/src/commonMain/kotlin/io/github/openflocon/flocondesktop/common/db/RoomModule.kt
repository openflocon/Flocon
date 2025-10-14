package io.github.openflocon.flocondesktop.common.db

import org.koin.dsl.module

val roomModule =
    module {
        single<AppDatabase> {
            getRoomDatabase()
        }
        single {
            get<AppDatabase>().networkDao
        }
        single {
            get<AppDatabase>().networkSettingsDao
        }
        single {
            get<AppDatabase>().fileDao
        }
        single {
            get<AppDatabase>().dashboardDao
        }
        single {
            get<AppDatabase>().tableDao
        }
        single {
            get<AppDatabase>().imageDao
        }
        single {
            get<AppDatabase>().queryDao
        }
        single {
            get<AppDatabase>().deeplinkDao
        }
        single {
            get<AppDatabase>().analyticsDao
        }
        single {
            get<AppDatabase>().networkFilterDao
        }
        single {
            get<AppDatabase>().networkMocksDao
        }
        single {
            get<AppDatabase>().adbDevicesDao
        }
        single {
            get<AppDatabase>().networkBadQualityConfigDao
        }
        single {
            get<AppDatabase>().devicesDao
        }
        single {
            get<AppDatabase>().tablesDao
        }
    }
