package io.github.openflocon.domain.device

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.HandleDeviceAndAppUseCase
import io.github.openflocon.domain.device.usecase.HandleNewAppUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceAppsUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveDevicesUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import io.github.openflocon.domain.device.usecase.HandleNewDeviceUseCase
import io.github.openflocon.domain.device.usecase.ObserveActiveDevicesUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val deviceModule = module {
    factoryOf(::GetCurrentDeviceIdAndPackageNameUseCase)
    factoryOf(::GetCurrentDeviceIdUseCase)
    factoryOf(::HandleDeviceAndAppUseCase)
    factoryOf(::HandleNewDeviceUseCase)
    factoryOf(::ObserveCurrentDeviceIdUseCase)
    factoryOf(::ObserveActiveDevicesUseCase)
    factoryOf(::ObserveCurrentDeviceAppsUseCase)
    factoryOf(::ObserveCurrentDeviceIdAndPackageNameUseCase)
    factoryOf(::ObserveDevicesUseCase)
    factoryOf(::SelectDeviceAppUseCase)
    factoryOf(::SelectDeviceUseCase)
    factoryOf(::HandleNewAppUseCase)
}
