package io.github.openflocon.domain.crashreporter.usecase

import io.github.openflocon.domain.crashreporter.repository.CrashReporterRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Whether the selected app reported at least one crash, without loading them.
 */
class ObserveCurrentDeviceHasCrashReportsUseCase(
    private val repository: CrashReporterRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
) {
    operator fun invoke(): Flow<Boolean> = observeCurrentDeviceIdAndPackageNameUseCase()
        .flatMapLatest { current ->
            if (current == null) {
                flowOf(false)
            } else {
                repository.observeHasCrashReports(current)
            }
        }
        .distinctUntilChanged()
}
