package io.github.openflocon.flocondesktop.common.db

import io.github.openflocon.domain.device.usecase.GetFloconDocumentPathUseCase
import io.github.openflocon.domain.maintenance.repository.DatabaseMaintenanceRepository
import io.github.openflocon.domain.maintenance.repository.MediaFilesRepository
import org.koin.core.module.dsl.singleOf
import org.koin.core.scope.Scope
import org.koin.dsl.bind
import org.koin.dsl.module

val roomModule =
    module {
        // overridden in mock mode (MockModule), to use a separate file
        single { DatabaseFile(getDatabaseFile()) }
        single<AppDatabase> {
            getRoomDatabase(get<DatabaseFile>().file)
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
            get<AppDatabase>().deeplinkVariableDao
        }
        single {
            get<AppDatabase>().analyticsDao
        }
        single {
            get<AppDatabase>().featureFlagDao
        }
        single {
            get<AppDatabase>().accessibilityEventDao
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
        single {
            get<AppDatabase>().crashReportDao
        }
        single {
            get<AppDatabase>().databaseQueryLogDao
        }
        single {
            get<AppDatabase>().adbCommanderDao
        }
        single {
            get<AppDatabase>().timelineDao
        }
        single {
            get<AppDatabase>().webViewEventDao
        }
        single {
            get<AppDatabase>().timelineLogDao
        }
        single {
            get<AppDatabase>().maintenanceDao
        }
        singleOf(::DatabaseMaintenanceRepositoryImpl) bind DatabaseMaintenanceRepository::class
        single<MediaFilesRepository> { createMediaFilesRepository() }
    }

/** The screenshots and recordings saved in the Flocon documents folder (also wrapped by the mock mode). */
internal fun Scope.createMediaFilesRepository(): MediaFilesRepository {
    val getDocumentsFolder = get<GetFloconDocumentPathUseCase>()
    return MediaFilesRepositoryImpl(
        mediaFiles = MediaFiles { getDocumentsFolder() },
        dispatcherProvider = get(),
        settingsDataSource = get(),
    )
}
