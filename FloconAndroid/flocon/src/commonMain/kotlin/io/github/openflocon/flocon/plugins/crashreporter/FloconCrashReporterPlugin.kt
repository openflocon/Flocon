package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.*
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel
import io.github.openflocon.flocon.plugins.crashreporter.model.crashReportsListToJson
import io.github.openflocon.flocon.pluginsold.crashreporter.FloconCrashReporterConfig
import io.github.openflocon.flocon.pluginsold.crashreporter.FloconCrashReporterPlugin
import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

object FloconCrashReporter :
    FloconPluginFactory<FloconCrashReporterConfig, FloconCrashReporterPlugin> {
    override val name: String = "CrashReporter"
    override val pluginId: String =
        Protocol.ToDevice.Analytics.Plugin // Crash reporter is usually write-only but we can set an ID

    override fun createConfig() = FloconCrashReporterConfig()
    override fun install(
        config: FloconCrashReporterConfig,
        app: FloconApp
    ): FloconCrashReporterPlugin {
        val client = app.client as FloconMessageSender
        return FloconCrashReporterPluginImpl(
            context = TODO(), //FloconContext(appContext = null), // Handled by datasource
            sender = client,
            coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
        )
    }
}

internal class FloconCrashReporterPluginImpl(
    private val context: FloconContext,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope,
) : FloconPlugin, FloconCrashReporterPlugin {
    override val key: String = "CRASH_REPORTER"

    private val dataSource = buildFloconCrashReporterDataSource(context)

    override fun setupCrashHandler() {
        setupUncaughtExceptionHandler(context) { throwable ->
            val crash = createCrashReport(throwable)
            dataSource.saveCrash(crash)
        }
    }

    override fun onConnectedToServer() {
        // Send all pending crashes
        coroutineScope.launch {
            try {
                val pendingCrashes = dataSource.loadPendingCrashes()
                if (pendingCrashes.isNotEmpty()) {
                    sendCrashes(pendingCrashes)
                    // Delete sent crashes
                    pendingCrashes.forEach { dataSource.deleteCrash(it.crashId) }
                }
            } catch (t: Throwable) {
                FloconLogger.logError("Error sending pending crashes", t)
            }
        }
    }

    override fun onMessageReceived(
        method: String,
        body: String,
    ) {
        // No messages from desktop for crashes (write-only plugin)
    }

    private fun sendCrashes(crashes: List<CrashReportDataModel>) {
        try {
            sender.send(
                plugin = Protocol.FromDevice.CrashReporter.Plugin,
                method = Protocol.FromDevice.CrashReporter.Method.ReportCrash,
                body = crashReportsListToJson(crashes),
            )
        } catch (t: Throwable) {
            FloconLogger.logError("Crash report sending error", t)
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private fun createCrashReport(throwable: Throwable): CrashReportDataModel {
        return CrashReportDataModel(
            crashId = Uuid.random().toString(),
            timestamp = currentTimeMillis(),
            exceptionType = throwable::class.simpleName ?: "Unknown",
            exceptionMessage = throwable.message ?: "No message",
            stackTrace = throwable.stackTraceToString(),
        )
    }
}
