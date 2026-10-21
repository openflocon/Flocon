package io.github.openflocon.flocon.myapplication.multi.featureflags

import io.github.openflocon.flocon.plugins.featureflags.floconFeatureFlags
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlagState

fun initializeFeatureFlags() {
    floconFeatureFlags("firebase").register(
        FloconFeatureFlag(name = "new_checkout", state = FloconFeatureFlagState.Custom, variant = "treatment", payload = """{"buttonColor":"#FF5722","steps":3}"""),
        FloconFeatureFlag(name = "dark_mode", state = FloconFeatureFlagState.On, variant = "on"),
        FloconFeatureFlag(name = "onboarding_v2", state = FloconFeatureFlagState.Off, variant = "control"),
    )
    floconFeatureFlags("statsig").register(
        FloconFeatureFlag(name = "search_ranking", state = FloconFeatureFlagState.Custom, variant = "variant_b", payload = """{"algorithm":"bm25"}"""),
    )
}
