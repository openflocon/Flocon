package io.github.openflocon.flocon.plugins.dashboard

import io.github.openflocon.flocon.plugins.dashboard.model.DashboardScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

fun CoroutineScope.floconDashboard(id: String, block: DashboardScope.() -> Unit): Job {
    // no op
    return Job().apply { complete() }
}
