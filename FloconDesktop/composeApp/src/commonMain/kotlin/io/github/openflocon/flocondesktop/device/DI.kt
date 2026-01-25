package io.github.openflocon.flocondesktop.device

import io.github.openflocon.flocondesktop.device.pages.battery.BatteryViewModel
import io.github.openflocon.flocondesktop.device.pages.cpu.CpuViewModel
import io.github.openflocon.flocondesktop.device.pages.info.InfoViewModel
import io.github.openflocon.flocondesktop.device.pages.memory.MemoryViewModel
import io.github.openflocon.flocondesktop.device.pages.permission.PermissionViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val deviceModule = module {
    viewModelOf(::DeviceViewModel)
    viewModelOf(::PermissionViewModel)
    viewModelOf(::CpuViewModel)
    viewModelOf(::BatteryViewModel)
    viewModelOf(::InfoViewModel)
    viewModelOf(::MemoryViewModel)
}
