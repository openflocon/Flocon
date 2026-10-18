package io.github.openflocon.flocondesktop.common

import co.touchlab.kermit.Logger
import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import java.io.IOException

// absolute path : a .app launched from the Finder does not inherit the shell PATH
private const val XCRUN_PATH = "/usr/bin/xcrun"

actual fun localOpenUrlInIosSimulator(url: String): Either<Throwable, String> = try {
    // arguments are passed as a list, so the url does not need to be quoted / escaped
    val command = listOf(XCRUN_PATH, "simctl", "openurl", "booted", url)
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
