package com.flocon.data.remote.mock

import com.flocon.data.remote.mock.scenarios.defaultScenario
import com.flocon.data.remote.mock.scenarios.docAccessibilityScenario
import com.flocon.data.remote.mock.scenarios.docAnalyticsScenario
import com.flocon.data.remote.mock.scenarios.docCrashReporterScenario
import com.flocon.data.remote.mock.scenarios.docDashboardScenario
import com.flocon.data.remote.mock.scenarios.docDatabaseScenario
import com.flocon.data.remote.mock.scenarios.docDeeplinksScenario
import com.flocon.data.remote.mock.scenarios.docFeatureFlagsScenario
import com.flocon.data.remote.mock.scenarios.docFilesScenario
import com.flocon.data.remote.mock.scenarios.docGraphQlScenario
import com.flocon.data.remote.mock.scenarios.docGrpcScenario
import com.flocon.data.remote.mock.scenarios.docImagesScenario
import com.flocon.data.remote.mock.scenarios.docNetworkScenario
import com.flocon.data.remote.mock.scenarios.docSharedPrefsScenario
import com.flocon.data.remote.mock.scenarios.docTablesScenario
import com.flocon.data.remote.mock.scenarios.docTimelineScenario
import com.flocon.data.remote.mock.scenarios.docWebSocketScenario
import com.flocon.data.remote.mock.scenarios.docWebViewScenario
import com.flocon.data.remote.mock.scenarios.emptyScenario
import com.flocon.data.remote.mock.scenarios.multiDeviceScenario
import com.flocon.data.remote.mock.scenarios.stressTimelineScenario
import com.flocon.data.remote.mock.scenarios.weirdScenario

/** Every scenario available with `--mock=<name>`. Add a new scenario here. */
object MockScenarios {
    val all: List<MockScenario> by lazy {
        listOf(
            defaultScenario,
            docNetworkScenario,
            docGraphQlScenario,
            docWebSocketScenario,
            docGrpcScenario,
            docTimelineScenario,
            docCrashReporterScenario,
            docAnalyticsScenario,
            docFeatureFlagsScenario,
            docDeeplinksScenario,
            docDatabaseScenario,
            docFilesScenario,
            docSharedPrefsScenario,
            docImagesScenario,
            docDashboardScenario,
            docTablesScenario,
            docAccessibilityScenario,
            docWebViewScenario,
            emptyScenario,
            multiDeviceScenario,
            weirdScenario,
            stressTimelineScenario,
        )
    }

    fun byName(name: String): MockScenario? = all.firstOrNull { it.name == name }
}
