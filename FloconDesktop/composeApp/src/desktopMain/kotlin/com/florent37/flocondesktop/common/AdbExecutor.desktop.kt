package com.florent37.flocondesktop.common

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import kotlin.io.bufferedReader
import kotlin.io.println
import kotlin.io.readText
import kotlin.use

actual fun findAdbPath(): String? {
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

actual fun executeSystemCommand(command: String): Either<Throwable, String> = try {
    listConnectedDevices().forEach {
        executeAdbCommand(serial = it, command = command)
    }
    Success("")
} catch (t: Throwable) {
    Failure(t)
}

private fun legacyExecuteSystemCommand(command: String): Either<Throwable, String> = try {
    val process = Runtime.getRuntime().exec(command)
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


private fun listConnectedDevices(): List<String> {
    val devices = mutableListOf<String>()
    try {
        val process = Runtime.getRuntime().exec("adb devices")
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

private fun executeAdbCommand(serial: String, command: String): String {
    val result = StringBuilder()
    try {
        val fullCommand = "adb -s $serial $command"
        val process = Runtime.getRuntime().exec(fullCommand)
        val reader = BufferedReader(InputStreamReader(process.inputStream))
        reader.lines().forEach { line ->
            result.append(line).append("\n")
        }
        val errorReader = BufferedReader(InputStreamReader(process.errorStream))
        errorReader.lines().forEach { line ->
            result.append("ERROR: ").append(line).append("\n")
        }
        process.waitFor()
    } catch (e: Exception) {
        result.append("Error executing command: ${e.message}")
    }
    return result.toString()
}
