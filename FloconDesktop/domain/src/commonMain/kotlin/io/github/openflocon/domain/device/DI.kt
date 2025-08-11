package io.github.openflocon.domain.device

import io.github.openflocon.domain.device.usecase.GetCurrentDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceUseCase
import io.github.openflocon.domain.device.usecase.HandleDeviceUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceUseCase
import io.github.openflocon.domain.device.usecase.ObserveDevicesUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceAppUseCase
import io.github.openflocon.domain.device.usecase.SelectDeviceUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val deviceModule = module {
    factoryOf(::GetCurrentDeviceAppUseCase)
    factoryOf(::GetCurrentDeviceIdAndPackageNameUseCase)
    factoryOf(::GetCurrentDeviceIdUseCase)
    factoryOf(::GetCurrentDeviceUseCase)
    factoryOf(::HandleDeviceUseCase)
    factoryOf(::ObserveCurrentDeviceIdUseCase)
    factoryOf(::ObserveCurrentDeviceAppUseCase)
    factoryOf(::ObserveCurrentDeviceIdAndPackageNameUseCase)
    factoryOf(::ObserveCurrentDeviceUseCase)
    factoryOf(::ObserveDevicesUseCase)
    factoryOf(::SelectDeviceAppUseCase)
    factoryOf(::SelectDeviceUseCase)
}
