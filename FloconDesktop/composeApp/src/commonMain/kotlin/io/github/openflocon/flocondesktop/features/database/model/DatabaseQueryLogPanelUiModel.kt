package io.github.openflocon.flocondesktop.features.database.model

import androidx.compose.runtime.Immutable

@Immutable
data class DatabaseQueryLogPanelUiModel(
    val dbName: String,
    val sql: String,
    val rawSql: String,
    val arguments: List<String>,
    val dateFormatted: String,
    val isTransaction: Boolean,
)

fun previewDatabaseQueryLogPanelUiModel() = DatabaseQueryLogPanelUiModel(
    dbName = "app.db",
    sql = "SELECT * FROM users WHERE id = 42",
    rawSql = "SELECT * FROM users WHERE id = ?",
    arguments = listOf("42"),
    dateFormatted = "2023-01-01 00:00:00",
    isTransaction = false,
)
