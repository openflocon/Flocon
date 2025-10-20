package io.github.openflocon.flocon

import android.content.Context
import io.github.openflocon.flocon.client.FloconClientImpl
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.analytics.FloconAnalyticsPlugin
import io.github.openflocon.flocon.plugins.dashboard.FloconDashboardPlugin
import io.github.openflocon.flocon.plugins.deeplinks.FloconDeeplinksPlugin
import io.github.openflocon.flocon.plugins.tables.FloconTablePlugin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Flocon : FloconCore() {
    fun initialize(context: Context) {
        super.initialize(
            FloconContext(appContext = context)
        )
    }
}