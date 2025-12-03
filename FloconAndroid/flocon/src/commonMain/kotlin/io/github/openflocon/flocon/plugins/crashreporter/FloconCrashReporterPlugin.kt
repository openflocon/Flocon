package io.github.openflocon.flocon.plugins.crashreporter

import io.github.openflocon.flocon.FloconContext
import io.github.openflocon.flocon.FloconLogger
import io.github.openflocon.flocon.Protocol
import io.github.openflocon.flocon.core.FloconMessageSender
import io.github.openflocon.flocon.core.FloconPlugin
import io.github.openflocon.flocon.model.FloconMessageFromServer
import io.github.openflocon.flocon.plugins.crashreporter.model.CrashReportDataModel
import io.github.openflocon.flocon.plugins.crashreporter.model.crashReportsListToJson
import io.github.openflocon.flocon.utils.currentTimeMillis
import io.github.openflocon.flocondesktop.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

internal class FloconCrashReporterPluginImpl(
    private val context: FloconContext,
    private var sender: FloconMessageSender,
    private val coroutineScope: CoroutineScope,
) : FloconPlugin, FloconCrashReporterPlugin {

    private val dataSource = buildFloconCrashReporterDataSource(context)

    fun setupCrashHandler() {
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

    override fun onMessageReceived(messageFromServer: FloconMessageFromServer) {
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
            appVersion = BuildConfig.APP_VERSION,
        )
    }
}
