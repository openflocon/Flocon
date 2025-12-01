package io.github.openflocon.domain.dashboard.usecase

import io.github.openflocon.domain.dashboard.models.DashboardArrangementDomainModel
import io.github.openflocon.domain.dashboard.repository.DashboardRepository
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ObserveDashboardArrangementUseCase(
    private val dashboardRepository: DashboardRepository,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase
) {
    operator fun invoke(): Flow<DashboardArrangementDomainModel> = observeCurrentDeviceIdAndPackageNameUseCase().flatMapLatest { model ->
        if (model == null) {
            flowOf(DashboardArrangementDomainModel.Adaptive)
        } else {
            dashboardRepository.observeDashboardArrangement(
                deviceIdAndPackageName = model,
            )
        }
    }
}
