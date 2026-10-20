package io.github.openflocon.flocon.myapplication.featureflags

import io.github.openflocon.flocon.Flocon
import io.github.openflocon.flocon.plugins.featureflags.featureFlags
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag

fun initializeFeatureFlags() {
    Flocon.featureFlags("firebase").register(
        FloconFeatureFlag(name = "new_checkout", variant = "treatment", payload = """{"buttonColor":"#FF5722","steps":3}"""),
        FloconFeatureFlag(name = "dark_mode", variant = "on"),
        FloconFeatureFlag(name = "onboarding_v2", variant = "control"),
    )
    Flocon.featureFlags("statsig").register(
        FloconFeatureFlag(name = "search_ranking", variant = "variant_b", payload = """{"algorithm":"bm25","boost":{"recent":1.5,"popular":2}}"""),
        FloconFeatureFlag(name = "paywall_copy", variant = "short", payload = "Unlock everything"),
    )
}
