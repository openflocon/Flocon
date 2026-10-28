package io.github.openflocon.domain.adb

import io.github.openflocon.domain.adb.usecase.ExecuteAdbCommandUseCase
import io.github.openflocon.domain.adb.usecase.GetDeviceSerialUseCase
import io.github.openflocon.domain.adb.usecase.SendCommandUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val adbModule = module {
    factoryOf(::ExecuteAdbCommandUseCase)
    factoryOf(::SendCommandUseCase)
    factoryOf(::GetDeviceSerialUseCase)
}
