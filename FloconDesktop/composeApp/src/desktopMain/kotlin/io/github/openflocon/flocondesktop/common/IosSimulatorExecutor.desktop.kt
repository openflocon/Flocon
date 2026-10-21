package io.github.openflocon.flocondesktop.common

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import java.io.File
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

// absolute path : a .app launched from the Finder does not inherit the shell PATH
private const val XCRUN_PATH = "/usr/bin/xcrun"

// `simctl recordVideo` fails within this delay when no simulator is booted
private const val RECORDING_START_CHECK_SECONDS = 1L

// `simctl recordVideo` exits once the in-flight frames are processed and the file is finalized
private const val RECORDING_STOP_TIMEOUT_SECONDS = 15L

private class IosSimulatorRecording(
    val process: Process,
    val output: StringBuffer,
)

// running recordings, by output file path
private val recordings = ConcurrentHashMap<String, IosSimulatorRecording>()

// a recording left running would keep recording after Flocon is closed
private val stopRecordingsOnExit by lazy {
    Runtime.getRuntime().addShutdownHook(
        thread(start = false) {
            recordings.values.forEach { it.process.destroy() }
        }
    )
}

actual fun localOpenUrlInIosSimulator(url: String): Either<Throwable, String> =
    // arguments are passed as a list, so the url does not need to be quoted / escaped
    runSimctl("openurl", "booted", url)

actual fun localTakeIosSimulatorScreenshot(filePath: String): Either<Throwable, String> =
    runSimctl("io", "booted", "screenshot", "--type=png", filePath)
        .mapSuccess { filePath }

actual fun localStartIosSimulatorRecording(filePath: String): Either<Throwable, Unit> = try {
    stopRecordingsOnExit

    // h264 instead of the default hevc, so the file can be read everywhere
    val command = listOf(XCRUN_PATH, "simctl", "io", "booted", "recordVideo", "--codec=h264", "--force", filePath)
    Logger.d("Executing command: ${command.joinToString(" ")}")

    val process = ProcessBuilder(command)
        .redirectErrorStream(true)
        .start()

    // the output must be drained, otherwise the process blocks once its buffer is full
    val output = StringBuffer()
    thread(isDaemon = true, name = "simctl-recordVideo") {
        process.inputStream.bufferedReader().forEachLine { output.appendLine(it) }
    }

    if (process.waitFor(RECORDING_START_CHECK_SECONDS, TimeUnit.SECONDS)) {
        val errorMessage = "Recording failed with exit code ${process.exitValue()}. Error:\n$output"
        Logger.e(errorMessage)
        Failure(IOException(errorMessage))
    } else {
        recordings[filePath] = IosSimulatorRecording(process = process, output = output)
        Success(Unit)
    }
} catch (t: Throwable) {
    Failure(t)
}

actual fun localStopIosSimulatorRecording(filePath: String): Either<Throwable, String> = try {
    val recording = recordings.remove(filePath)
        ?: throw IllegalStateException("No iOS Simulator recording for $filePath")

    // SIGINT finalizes the video, Process.destroy() would send SIGTERM
    ProcessBuilder("kill", "-INT", recording.process.pid().toString())
        .start()
        .waitFor()

    if (!recording.process.waitFor(RECORDING_STOP_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
        recording.process.destroyForcibly()
        throw IOException("The recording did not stop in time. Output:\n${recording.output}")
    }

    val file = File(filePath)
    if (file.exists() && file.length() > 0) {
        Success(filePath)
    } else {
        val errorMessage = "The recording was not saved. Output:\n${recording.output}"
        Logger.e(errorMessage)
        Failure(IOException(errorMessage))
    }
} catch (t: Throwable) {
    Failure(t)
}

private fun runSimctl(vararg arguments: String): Either<Throwable, String> = try {
    val command = listOf(XCRUN_PATH, "simctl") + arguments
    Logger.d("Executing command: ${command.joinToString(" ")}")

    val process = ProcessBuilder(command)
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().use { it.readText() }
    val exitCode = process.waitFor()

    if (exitCode == 0) {
        Success(output)
    } else {
        val errorMessage = "Command failed with exit code $exitCode. Error:\n$output"
        Logger.e(errorMessage)
        Failure(IOException(errorMessage))
    }
} catch (t: Throwable) {
    Failure(t)
}
