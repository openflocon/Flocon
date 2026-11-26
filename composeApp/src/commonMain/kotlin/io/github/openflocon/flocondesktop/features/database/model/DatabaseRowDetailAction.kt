package io.github.openflocon.flocondesktop.features.database.model

sealed interface DatabaseRowDetailAction {
    data class ValueChanged(val index: Int, val value: String) : DatabaseRowDetailAction

    /** Switches a value between `NULL` and an empty text. */
    data class NullChanged(val index: Int, val isNull: Boolean) : DatabaseRowDetailAction

    data class CopyValue(val value: String) : DatabaseRowDetailAction

    data object CopyAll : DatabaseRowDetailAction

    data object Update : DatabaseRowDetailAction
}
