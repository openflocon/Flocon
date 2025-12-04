package io.github.openflocon.domain.crashreporter.usecase

import io.github.openflocon.domain.crashreporter.models.CrashReportDomainModel
import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveCrashReportsByIdUseCase(
    private val repository: CrashReporterRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(id: String): Flow<CrashReportDomainModel?> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { current ->
        if (current == null) {
            flowOf(null)
        } else {
            repository.observeCrashReportById(id = id, deviceIdAndPackageName = current)
        }
    }
}
