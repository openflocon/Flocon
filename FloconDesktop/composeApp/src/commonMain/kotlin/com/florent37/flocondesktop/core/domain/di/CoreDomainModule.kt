package com.florent37.flocondesktop.core.domain.di

import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import com.florent37.flocondesktop.core.domain.device.GetCurrentDeviceUseCase
import com.florent37.flocondesktop.core.domain.device.HandleDeviceUseCase
import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import com.florent37.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import com.florent37.flocondesktop.core.domain.device.ObserveDevicesUseCase
import com.florent37.flocondesktop.core.domain.device.SelectDeviceUseCase
import com.florent37.flocondesktop.core.domain.settings.InitAdbPathUseCase
import com.florent37.flocondesktop.core.domain.settings.StartAdbForwardUseCase
import com.florent37.flocondesktop.core.domain.settings.TestAdbUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val coreDomainModule =
    module {
        factoryOf(::InitAdbPathUseCase)
        factoryOf(::StartAdbForwardUseCase)
        factoryOf(::TestAdbUseCase)
        // devices
        factoryOf(::HandleDeviceUseCase)
        factoryOf(::SelectDeviceUseCase)
        factoryOf(::ObserveDevicesUseCase)
        factoryOf(::ObserveCurrentDeviceUseCase)
        factoryOf(::ObserveCurrentDeviceIdUseCase)
        factoryOf(::GetCurrentDeviceUseCase)
        factoryOf(::GetCurrentDeviceIdUseCase)
    }
