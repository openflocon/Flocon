package io.github.openflocon.flocondesktop

import java.nio.file.Paths

fun clearTmpFiles() {
    try {
        val performancesDir = Paths.get(System.getProperty("user.home"), "Desktop", "Flocon", "performances").toFile()
        if (performancesDir.exists()) {
            performancesDir.deleteRecursively()
        }
    } catch (e: Exception) {
        println("Failed to clear performances directory: ${e.message}")
    }
}
