package io.github.openflocon.mcp.database

import io.github.openflocon.mcp.McpToolException

/**
 * The SQL sent by the agents is executed in the mobile app database: only read only statements are accepted.
 *
 * The Flocon clients (Android, iOS, JVM) only return rows for queries starting with SELECT, PRAGMA or EXPLAIN,
 * the others are executed as raw statements.
 *
 * @throws McpToolException explaining why the query is refused
 */
internal fun checkReadOnlySql(sql: String) {
    val statements = sql.splitStatements()
    if (statements.size != 1) {
        throw McpToolException("Only one SQL statement can be executed at a time.")
    }
    val statement = statements.single()

    when (statement.firstWord().uppercase()) {
        "SELECT", "EXPLAIN" -> Unit
        PRAGMA -> checkReadOnlyPragma(statement)
        "WITH" -> throw McpToolException("Common table expressions (WITH) are not supported, use a sub query in a SELECT.")
        else -> throw McpToolException("Only read only queries are allowed: SELECT, PRAGMA (without value) or EXPLAIN.")
    }
}

/**
 * `PRAGMA name = value` and `PRAGMA name(value)` modify the database, except for the pragmas taking a table / index as argument
 */
private fun checkReadOnlyPragma(statement: String) {
    val outsideQuotes = statement.withoutQuotedParts()
    if (outsideQuotes.contains('=')) {
        throw McpToolException("PRAGMA can only be read, not assigned.")
    }
    if (outsideQuotes.contains('(')) {
        val name = outsideQuotes.trim().substring(PRAGMA.length).substringBefore('(').trim().substringAfterLast('.').lowercase()
        if (name !in pragmasWithArgument) {
            throw McpToolException("PRAGMA $name(...) modifies the database, only these pragmas accept an argument: $pragmasWithArgument")
        }
    }
}

private const val PRAGMA = "PRAGMA"

private val pragmasWithArgument = setOf(
    "table_info",
    "table_xinfo",
    "table_list",
    "index_list",
    "index_info",
    "index_xinfo",
    "foreign_key_list",
    "foreign_key_check",
    "integrity_check",
    "quick_check",
)

private fun String.firstWord(): String = trim().takeWhile { !it.isWhitespace() && it != '(' && it != ';' }

/**
 * Splits on the `;` that are not in a string / identifier / comment, ignores the empty statements
 */
private fun String.splitStatements(): List<String> {
    val statements = mutableListOf<String>()
    val current = StringBuilder()
    forEachOutsideQuotes(
        onQuoted = { current.append(it) },
        onCode = { char ->
            if (char == ';') {
                statements += current.toString()
                current.clear()
            } else {
                current.append(char)
            }
        },
    )
    statements += current.toString()
    return statements.filter { it.isNotBlank() }
}

private fun String.withoutQuotedParts(): String = buildString {
    this@withoutQuotedParts.forEachOutsideQuotes(onQuoted = {}, onCode = { append(it) })
}

/**
 * Handles 'strings', "identifiers", `identifiers`, [identifiers], -- comments and /* comments */
 */
private inline fun String.forEachOutsideQuotes(
    onQuoted: (Char) -> Unit,
    onCode: (Char) -> Unit,
) {
    var index = 0
    while (index < length) {
        val char = this[index]
        // opening length to closing delimiter
        val quote: Pair<Int, String>? = when {
            char == '\'' -> 1 to "'"
            char == '"' -> 1 to "\""
            char == '`' -> 1 to "`"
            char == '[' -> 1 to "]"
            startsWith("--", index) -> 2 to "\n"
            startsWith("/*", index) -> 2 to "*/"
            else -> null
        }
        if (quote == null) {
            onCode(char)
            index++
        } else {
            val (openingLength, closing) = quote
            val end = indexOf(closing, startIndex = index + openingLength).let { if (it == -1) length else it + closing.length }
            for (i in index until end) onQuoted(this[i])
            index = end
        }
    }
}
