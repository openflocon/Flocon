package io.github.openflocon.domain.database.utils

/** [name] as an SQLite identifier. */
fun quoteSqlIdentifier(name: String): String = "\"" + name.replace("\"", "\"\"") + "\""

/** [value] as an SQLite literal. A `null` value is the `NULL` keyword, any other value is a string. */
fun toSqlLiteral(value: String?): String = if (value == null) "NULL" else "'" + value.replace("'", "''") + "'"
