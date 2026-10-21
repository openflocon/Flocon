package io.github.openflocon.flocon.myapplication.multi.featureflags

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.featureflags.featureFlagsPlugin
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.featureflags.model.FloconFeatureFlagState

fun initializeFeatureFlags() {
    Flocon.featureFlagsPlugin.register(
        provider = "firebase",
        FloconFeatureFlag(name = "new_checkout", state = FloconFeatureFlagState.Custom, variant = "treatment", payload = """{"buttonColor":"#FF5722","steps":3}"""),
        FloconFeatureFlag(name = "dark_mode", state = FloconFeatureFlagState.On, variant = "on"),
        FloconFeatureFlag(name = "onboarding_v2", state = FloconFeatureFlagState.Off, variant = "control"),
    )
    Flocon.featureFlagsPlugin.register(
        provider = "statsig",
        FloconFeatureFlag(name = "search_ranking", state = FloconFeatureFlagState.Custom, variant = "variant_b", payload = """{"algorithm":"bm25"}"""),
    )
}
