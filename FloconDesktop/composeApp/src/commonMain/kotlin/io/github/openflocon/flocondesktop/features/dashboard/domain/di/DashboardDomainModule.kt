package io.github.openflocon.flocondesktop.features.dashboard.domain.di

import io.github.openflocon.flocondesktop.features.dashboard.domain.GetCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.ObserveCurrentDeviceDashboardUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.ObserveCurrentDeviceSelectedDashboardUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.ObserveDeviceDashboardsUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.SelectCurrentDeviceDashboardUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.SendCheckBoxUpdateDeviceDeviceUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.SendClickEventToDeviceDeviceUseCase
import io.github.openflocon.flocondesktop.features.dashboard.domain.SubmitTextFieldToDeviceDeviceUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dashboardDomainModule =
    module {
        factoryOf(::ObserveCurrentDeviceDashboardUseCase)
        factoryOf(::SendClickEventToDeviceDeviceUseCase)
        factoryOf(::SubmitTextFieldToDeviceDeviceUseCase)
        factoryOf(::SendCheckBoxUpdateDeviceDeviceUseCase)

        factoryOf(::GetCurrentDeviceSelectedDashboardUseCase)
        factoryOf(::ObserveCurrentDeviceSelectedDashboardUseCase)
        factoryOf(::ObserveDeviceDashboardsUseCase)
        factoryOf(::SelectCurrentDeviceDashboardUseCase)
    }
