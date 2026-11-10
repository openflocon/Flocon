package com.flocon.data.remote.mock

import com.flocon.data.remote.mock.scenarios.defaultScenario
import com.flocon.data.remote.mock.scenarios.docAnalyticsScenario
import com.flocon.data.remote.mock.scenarios.docCrashReporterScenario
import com.flocon.data.remote.mock.scenarios.docFeatureFlagsScenario
import com.flocon.data.remote.mock.scenarios.docNetworkScenario
import com.flocon.data.remote.mock.scenarios.docTimelineScenario
import com.flocon.data.remote.mock.scenarios.emptyScenario
import com.flocon.data.remote.mock.scenarios.multiDeviceScenario
import com.flocon.data.remote.mock.scenarios.weirdScenario

/** Every scenario available with `--mock=<name>`. Add a new scenario here. */
object MockScenarios {
    val all: List<MockScenario> by lazy {
        listOf(
            defaultScenario,
            docNetworkScenario,
            docTimelineScenario,
            docCrashReporterScenario,
            docAnalyticsScenario,
            docFeatureFlagsScenario,
            emptyScenario,
            multiDeviceScenario,
            weirdScenario,
        )
    }

    fun byName(name: String): MockScenario? = all.firstOrNull { it.name == name }
}
