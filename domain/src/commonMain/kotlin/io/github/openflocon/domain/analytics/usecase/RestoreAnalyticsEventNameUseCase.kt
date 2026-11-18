package io.github.openflocon.domain.analytics.usecase

import io.github.openflocon.domain.analytics.repository.AnalyticsPreferencesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase

class RestoreAnalyticsEventNameUseCase(
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val analyticsPreferencesRepository: AnalyticsPreferencesRepository,
) {
    suspend operator fun invoke(eventName: String) {
        getCurrentDeviceIdAndPackageNameUseCase()?.let {
            analyticsPreferencesRepository.restoreEventName(packageName = it.packageName, eventName = eventName)
        }
    }
}
