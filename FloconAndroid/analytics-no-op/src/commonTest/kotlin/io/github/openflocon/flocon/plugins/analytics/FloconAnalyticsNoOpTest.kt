package io.github.openflocon.flocon.plugins.analytics

import io.github.openflocon.flocon.analytics.model.AnalyticsItem
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class FloconAnalyticsNoOpTest {

    private val plugin = FloconAnalyticsNoOpImpl()

    @Test
    fun `registerAnalytics does nothing`() = runTest {
        plugin.registerAnalytics(listOf(
            AnalyticsItem(
                id = "1",
                name = "test",
                properties = mapOf()
            )
        ))
        // No assertions needed as it's a no-op
    }
}
