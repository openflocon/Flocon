package io.github.openflocon.flocondesktop.common

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.common.Failure
import io.github.openflocon.domain.common.Success
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.bufferedReader
import kotlin.io.println
import kotlin.io.readText
import kotlin.use

actual fun localFindAdbPath(): String? {
    // 1. Check if 'adb' is already in the system PATH
    try {
        val process =
            ProcessBuilder("adb", "version")
                .redirectErrorStream(true)
                .start()
        val exitCode = process.waitFor()
        if (exitCode == 0) {
            println(" 'adb' found in system PATH.")
            return "adb" // It's in the PATH, so we can just use "adb"
        }
    } catch (e: IOException) {
        println(" 'adb' not found in system PATH directly: ${e.message}")
        // Fall through to search in SDK
    } catch (e: InterruptedException) {
        Thread.currentThread().interrupt()
        println("Process interrupted while checking 'adb' in system PATH.")
    }

    // 2. Search in common Android SDK locations
    println("Searching for 'adb' in common Android SDK locations...")
    val userHome = System.getProperty("user.home")
    val possibleSdkPaths =
        listOf(
            File(userHome, "Library/Android/sdk"), // macOS default
            File(userHome, "AppData/Local/Android/sdk"), // Windows default
            File(userHome, "Android/sdk"), // Linux common
            File("/usr/local/android-sdk"), // Another common custom install location
            // Add more paths if you have other common install locations
        )

    for (sdkPath in possibleSdkPaths) {
        val platformToolsPath = File(sdkPath, "platform-tools")
        val adbExecutable = File(platformToolsPath, "adb")
        if (adbExecutable.exists() && adbExecutable.canExecute()) {
            return adbExecutable.absolutePath
        }
    }

    return null
}

actual fun localExecuteAdbCommand(
    adbPath: String,
    command: String,
    deviceSerial: String?,
): Either<Throwable, String> = try {
    if(deviceSerial != null) {
        singleDeviceExecuteSystemCommand(adbPath = "$adbPath -s $deviceSerial", command = command)
    } else {
        val devices = listConnectedDevices(adbPath)
        if (devices.isEmpty() || devices.size == 1) {
            singleDeviceExecuteSystemCommand(adbPath = adbPath, command = command)
        } else {
            devices.map { serial ->
                singleDeviceExecuteSystemCommand(adbPath = "$adbPath -s $serial", command = command)
            }.let {
                it.forEach {
                    // return a failure if there's on in the list
                    if (it is Failure)
                        return it
                }
                return it.firstOrNull() ?: Success("")
            }
        }
    }
} catch (t: Throwable) {
    Failure(t)
}

actual fun askSerialToAllDevices(adbPath: String, command: String, serialVariableName: String): Either<Throwable, String> = try {
    listConnectedDevices(adbPath).map { serial ->
        singleDeviceExecuteSystemCommand(adbPath = "$adbPath -s $serial", command = command.replace(serialVariableName, serial))
    }.let {
        it.forEach {
            // return a failure if there's on in the list
            if (it is Failure)
                return it
        }
        return it.firstOrNull() ?: Success("")
    }
} catch (t: Throwable) {
    Failure(t)
}

private fun singleDeviceExecuteSystemCommand(adbPath: String, command: String): Either<Throwable, String> = try {
    val process = Runtime.getRuntime().exec("$adbPath $command")
    val output = process.inputStream.bufferedReader().use { it.readText() }
    val error = process.errorStream.bufferedReader().use { it.readText() }
    val exitCode = process.waitFor()

    if (exitCode == 0) {
        // println("Command executed successfully. Output:\n$output")
        Success(output)
    } else {
        val errorMessage =
            "Command failed with exit code $exitCode. Error:\n$error"
        // System.err.println(errorMessage)
        Failure(IOException(errorMessage))
    }
} catch (e: IOException) {
    val errorMessage = "Error executing command '$command': ${e.message}"
    // System.err.println(errorMessage)
    Failure(IOException(errorMessage, e))
} catch (e: InterruptedException) {
    // Thread.currentThread().interrupt()
    val errorMessage = "Command execution interrupted for '$command': ${e.message}"
    System.err.println(errorMessage)
    Failure(IOException(errorMessage, e))
}

private fun listConnectedDevices(adbPath: String): List<String> {
    val devices = mutableListOf<String>()
    try {
        val process = Runtime.getRuntime().exec("$adbPath devices")
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.lines().forEach { line ->
            if (line.endsWith("device") && !line.startsWith("List of devices attached")) {
                devices.add(line.split("\t")[0])
            }
        }
        process.waitFor() // Attendre la fin du processus
    } catch (e: Exception) {
        println("Error executing adb devices: ${e.message}")
    }
    return devices
}

private val processes = ConcurrentHashMap<ProcessId, Process>()

actual fun startProcess(adbPath: String, deviceSerial: String?, command: String): Either<Throwable, ProcessId> = try {
    val processId = UUID.randomUUID().toString()
    val process = Runtime.getRuntime().exec("$adbPath -s $deviceSerial $command")
    processes[processId] = process

    if(command.contains("reverse").not())
        println("Executing command: $adbPath $command")

    Success(processId)
} catch (e: IOException) {
    val errorMessage = "Error executing command '$command': ${e.message}"
    // System.err.println(errorMessage)
    Failure(IOException(errorMessage, e))
} catch (e: InterruptedException) {
    // Thread.currentThread().interrupt()
    val errorMessage = "Command execution interrupted for '$command': ${e.message}"
    System.err.println(errorMessage)
    Failure(IOException(errorMessage, e))
}

actual fun stopProcess(processId: ProcessId) {
    processes[processId]?.let { process ->
        try {
            process.destroyForcibly()

            val exitCode = process.waitFor()

            // Une fois arrêté, on peut pull le fichier
            // pullFile(deviceFilePath, localDesktopPath)
            // runCommand("adb", "shell", "rm", deviceFilePath)

        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}
