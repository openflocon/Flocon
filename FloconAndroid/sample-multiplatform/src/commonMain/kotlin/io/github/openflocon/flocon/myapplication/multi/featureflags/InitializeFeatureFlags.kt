package io.github.openflocon.flocon.myapplication.multi.featureflags

import io.github.openflocon.flocon.plugins.featureflags.floconFeatureFlags
import io.github.openflocon.flocon.plugins.featureflags.model.FloconFeatureFlag

fun initializeFeatureFlags() {
    floconFeatureFlags("firebase").register(
        FloconFeatureFlag(name = "new_checkout", variant = "treatment", payload = """{"buttonColor":"#FF5722","steps":3}"""),
        FloconFeatureFlag(name = "dark_mode", variant = "on"),
    )
    floconFeatureFlags("statsig").register(
        FloconFeatureFlag(name = "search_ranking", variant = "variant_b", payload = """{"algorithm":"bm25"}"""),
    )
}
