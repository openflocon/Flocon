package io.github.openflocon.flocondesktop.core.domain.di

import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.core.domain.device.GetCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.HandleDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveCurrentDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.device.ObserveDevicesUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceAppUseCase
import io.github.openflocon.flocondesktop.core.domain.device.SelectDeviceUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.InitAdbPathUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.StartAdbForwardUseCase
import io.github.openflocon.flocondesktop.core.domain.settings.TestAdbUseCase
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
        factoryOf(::SelectDeviceAppUseCase)
        factoryOf(::ObserveDevicesUseCase)
        factoryOf(::ObserveCurrentDeviceUseCase)
        factoryOf(::ObserveCurrentDeviceIdUseCase)
        factoryOf(::ObserveCurrentDeviceAppUseCase)
        factoryOf(::ObserveCurrentDeviceIdAndPackageNameUseCase)
        factoryOf(::GetCurrentDeviceUseCase)
        factoryOf(::GetCurrentDeviceIdUseCase)
        factoryOf(::GetCurrentDeviceIdAndPackageNameUseCase)
        factoryOf(::GetCurrentDeviceAppUseCase)
    }
