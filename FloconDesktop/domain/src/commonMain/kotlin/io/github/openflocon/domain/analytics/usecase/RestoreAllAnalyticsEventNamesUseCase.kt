package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.repository.AnalyticsPreferencesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class RestoreAllAnalyticsEventNamesUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsPreferencesRepository: AnalyticsPreferencesRepository,
) {
    suspend operator fun invoke() {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            analyticsPreferencesRepository.restoreAllEventNames(packageName = it.packageName)
        }
    }
}
