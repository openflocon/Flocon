package io.github.openflocon.flocondesktop.common.ui

/**
 * Which database transactions show their queries: all of them or none ([expandedByDefault], a setting), except the ones
 * the user flipped since ([toggled]).
 */
data class TransactionFolding(
    val expandedByDefault: Boolean = true,
    val toggled: Set<String> = emptySet(),
) {
    fun isExpanded(transactionId: String): Boolean = expandedByDefault != (transactionId in toggled)
}
