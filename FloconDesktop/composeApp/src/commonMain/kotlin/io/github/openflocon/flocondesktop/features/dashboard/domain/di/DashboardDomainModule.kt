package io.github.openflocon.flocondesktop.features.dashboard.domain.di

import com.florent37.flocondesktop.features.dashboard.domain.GetCurrentDeviceSelectedDashboardUseCase
import com.florent37.flocondesktop.features.dashboard.domain.ObserveCurrentDeviceDashboardUseCase
import com.florent37.flocondesktop.features.dashboard.domain.ObserveCurrentDeviceSelectedDashboardUseCase
import com.florent37.flocondesktop.features.dashboard.domain.ObserveDeviceDashboardsUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SelectCurrentDeviceDashboardUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SendCheckBoxUpdateDeviceDeviceUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SendClickEventToDeviceDeviceUseCase
import com.florent37.flocondesktop.features.dashboard.domain.SubmitTextFieldToDeviceDeviceUseCase
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
