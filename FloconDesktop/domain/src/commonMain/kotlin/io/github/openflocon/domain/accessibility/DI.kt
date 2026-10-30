package io.github.openflocon.domain.accessibility

import io.github.openflocon.domain.accessibility.usecase.FocusNextAccessibilityNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.FocusPreviousAccessibilityNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityEventUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityEventsUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityFocusedNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityStateUseCase
import io.github.openflocon.domain.accessibility.usecase.ObserveCurrentDeviceAccessibilityTypesUseCase
import io.github.openflocon.domain.accessibility.usecase.RefreshAccessibilityFocusedNodeUseCase
import io.github.openflocon.domain.accessibility.usecase.ResetCurrentDeviceAccessibilityEventsUseCase
import io.github.openflocon.domain.accessibility.usecase.SetTalkBackEnabledUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal val accessibilityModule = module {
    factoryOf(::ObserveCurrentDeviceAccessibilityEventsUseCase)
    factoryOf(::ObserveCurrentDeviceAccessibilityEventUseCase)
    factoryOf(::ObserveCurrentDeviceAccessibilityTypesUseCase)
    factoryOf(::ObserveCurrentDeviceAccessibilityStateUseCase)
    factoryOf(::ResetCurrentDeviceAccessibilityEventsUseCase)
    factoryOf(::ObserveCurrentDeviceAccessibilityFocusedNodeUseCase)
    factoryOf(::FocusNextAccessibilityNodeUseCase)
    factoryOf(::FocusPreviousAccessibilityNodeUseCase)
    factoryOf(::RefreshAccessibilityFocusedNodeUseCase)
    factoryOf(::SetTalkBackEnabledUseCase)
}
