package com.florent37.flocondesktop.common.db

import org.koin.dsl.module

val roomModule =
    module {
        single<AppDatabase> {
            getRoomDatabase()
        }
        single {
            get<AppDatabase>().httpRequestDao
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
            get<AppDatabase>().grpcDao
        }
        single {
            get<AppDatabase>().analyticsDao
        }
    }
