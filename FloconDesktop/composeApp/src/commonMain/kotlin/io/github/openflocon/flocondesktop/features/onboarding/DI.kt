package io.github.openflocon.flocondesktop.features.onboarding

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val onboardingModule = module {
    viewModelOf(::OnboardingViewModel)
}
