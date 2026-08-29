package io.github.openflocon.flocondesktop.features.onboarding

import androidx.navigation3.runtime.EntryProviderScope
import io.github.openflocon.flocondesktop.features.onboarding.view.OnboardingScreen
import io.github.openflocon.navigation.FloconRoute
import kotlinx.serialization.Serializable

sealed interface OnboardingRoutes : FloconRoute {

    @Serializable
    data object Main : OnboardingRoutes
}

fun EntryProviderScope<FloconRoute>.onboardingRoutes() {
    entry<OnboardingRoutes.Main> {
        OnboardingScreen()
    }
}
