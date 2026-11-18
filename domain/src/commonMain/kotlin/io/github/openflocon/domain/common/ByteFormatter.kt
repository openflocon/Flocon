package io.github.openflocon.domain.common

import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.round

/**
 * Utility object for formatting byte sizes into human-readable strings (e.g., "1.2 KB", "5.6 MB").
 */
object ByteFormatter {
    private val units = arrayOf("B", "KB", "MB", "GB", "TB")
    private const val KILO: Double = 1024.0

    /**
     * Formats a byte size (Long) into a human-readable string.
     *
     * Examples:
     * 1023L -> "1023 B"
     * 1024L -> "1.00 KB"
     * 1500L -> "1.46 KB"
     * 1048576L -> "1.00 MB"
     *
     * @param bytes The size in bytes (Long).
     * @return A human-readable string representation of the byte size.
     */
    fun formatBytes(bytes: Long): String {
        if (bytes <= 0) return "0 B"

        // Calculate the log base 1024 to find the appropriate unit index
        val digitGroups = (log10(bytes.toDouble()) / log10(KILO)).toInt()

        // Calculate the value in the chosen unit
        val value = bytes / KILO.pow(digitGroups.toDouble())

        // Format the value to two decimal places using a multiplatform approach
        // Multiply by 100, round, then divide by 100.0 to keep decimals.
        val formattedValue = (round(value * 100) / 100.0)

        return "$formattedValue ${units[digitGroups]}"
    }
}
