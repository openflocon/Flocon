package io.github.openflocon.domain.dashboard

import io.github.openflocon.domain.dashboard.usecase.DeleteCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.DeleteDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.GetCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveCurrentDeviceDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveDashboardArrangementUseCase
import io.github.openflocon.domain.dashboard.usecase.ObserveDeviceDashboardsUseCase
import io.github.openflocon.domain.dashboard.usecase.SelectCurrentDeviceDashboardUseCase
import io.github.openflocon.domain.dashboard.usecase.SendCheckBoxUpdateDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SendClickEventToDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SelectDashboardArrangementUseCase
import io.github.openflocon.domain.dashboard.usecase.SubmitFormToDeviceDeviceUseCase
import io.github.openflocon.domain.dashboard.usecase.SubmitTextFieldToDeviceDeviceUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val dashboardModule = module {
    factoryOf(::ObserveCurrentDeviceDashboardUseCase)
    factoryOf(::SendClickEventToDeviceDeviceUseCase)
    factoryOf(::SubmitTextFieldToDeviceDeviceUseCase)
    factoryOf(::SubmitFormToDeviceDeviceUseCase)
    factoryOf(::SendCheckBoxUpdateDeviceDeviceUseCase)

    factoryOf(::GetCurrentDeviceSelectedDashboardUseCase)
    factoryOf(::ObserveCurrentDeviceSelectedDashboardUseCase)
    factoryOf(::ObserveDeviceDashboardsUseCase)
    factoryOf(::SelectCurrentDeviceDashboardUseCase)

    factoryOf(::ObserveDashboardArrangementUseCase)
    factoryOf(::SelectDashboardArrangementUseCase)

    factoryOf(::DeleteDashboardUseCase)
    factoryOf(::DeleteCurrentDeviceSelectedDashboardUseCase)
}
