package io.github.openflocon.flocon.myapplication.featureflags

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.featureflags.featureFlags
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlagState

fun initializeFeatureFlags() {
    Flocon.featureFlags("firebase").register(
        FloconFeatureFlag(name = "new_checkout", state = FloconFeatureFlagState.Custom, variant = "treatment", payload = """{"buttonColor":"#FF5722","steps":3}"""),
        FloconFeatureFlag(name = "dark_mode", state = FloconFeatureFlagState.On, variant = "on"),
        FloconFeatureFlag(name = "onboarding_v2", state = FloconFeatureFlagState.Off, variant = "control"),
    )
    Flocon.featureFlags("statsig").register(
        FloconFeatureFlag(name = "search_ranking", state = FloconFeatureFlagState.Custom, variant = "variant_b", payload = """{"algorithm":"bm25","boost":{"recent":1.5,"popular":2}}"""),
        FloconFeatureFlag(name = "paywall_copy", state = FloconFeatureFlagState.Custom, variant = "short", payload = "Unlock everything"),
    )
}
