package io.github.openflocon.domain.database.utils

/**
 * The table read by [query], only when each row of its result is a row of that table:
 * `SELECT * | column, ... FROM table [WHERE ...] [ORDER BY ...] [LIMIT ...]`.
 *
 * Returns `null` for anything else (joins, aliases, expressions, sub queries, compound queries, grouping,
 * schema qualified names...). Being too strict only makes a result read-only, being too lax would
 * update the wrong row.
 */
fun parseSingleTableSelect(query: String): String? {
    val tokens = tokenize(query) ?: return null
    val body = if (tokens.lastOrNull() == SqlToken.Symbol(';')) tokens.dropLast(1) else tokens
    var position = 0

    fun next(): SqlToken? = body.getOrNull(position++)

    if (!next().isKeyword("select")) return null

    if (body.getOrNull(position) == SqlToken.Symbol('*')) {
        position++
        if (!next().isKeyword("from")) return null
    } else {
        while (true) {
            if (next().identifierName() == null) return null
            val separator = next()
            if (separator.isKeyword("from")) break
            if (separator != SqlToken.Symbol(',')) return null
        }
    }

    val table = next().identifierName() ?: return null

    val rest = body.drop(position)
    val first = rest.firstOrNull() ?: return table
    if (CLAUSES_AFTER_TABLE.none { first.isKeyword(it) }) return null
    val isUnsupported = rest.any { token ->
        token == SqlToken.Symbol(';') || UNSUPPORTED_KEYWORDS.any { token.isKeyword(it) }
    }
    return if (isUnsupported) null else table
}

private val CLAUSES_AFTER_TABLE = listOf("where", "order", "limit")
private val UNSUPPORTED_KEYWORDS = listOf("select", "union", "intersect", "except", "group", "having", "join", "window")

private sealed interface SqlToken {
    data class Word(val text: String) : SqlToken
    data class QuotedIdentifier(val name: String) : SqlToken
    data object StringLiteral : SqlToken
    data class Symbol(val char: Char) : SqlToken
}

private fun SqlToken?.isKeyword(keyword: String): Boolean = this is SqlToken.Word && text.equals(keyword, ignoreCase = true)

private fun SqlToken?.identifierName(): String? = when (this) {
    is SqlToken.Word -> text.takeIf { !it.first().isDigit() }
    is SqlToken.QuotedIdentifier -> name
    else -> null
}

/** `null` when a quote or a block comment is not closed. */
private fun tokenize(sql: String): List<SqlToken>? {
    val tokens = mutableListOf<SqlToken>()
    var index = 0
    while (index < sql.length) {
        val char = sql[index]
        when {
            char.isWhitespace() -> index++

            sql.startsWith("--", index) -> {
                val end = sql.indexOf('\n', index)
                index = if (end < 0) sql.length else end + 1
            }

            sql.startsWith("/*", index) -> {
                val end = sql.indexOf("*/", index + 2)
                if (end < 0) return null
                index = end + 2
            }

            char == '\'' || char == '"' || char == '`' || char == '[' -> {
                val closing = if (char == '[') ']' else char
                val content = StringBuilder()
                var cursor = index + 1
                while (true) {
                    if (cursor >= sql.length) return null
                    if (sql[cursor] == closing) {
                        // a doubled quote is an escaped quote, brackets have no escape
                        if (closing != ']' && sql.getOrNull(cursor + 1) == closing) {
                            content.append(closing)
                            cursor += 2
                            continue
                        }
                        break
                    }
                    content.append(sql[cursor])
                    cursor++
                }
                tokens += if (char == '\'') SqlToken.StringLiteral else SqlToken.QuotedIdentifier(content.toString())
                index = cursor + 1
            }

            char.isLetterOrDigit() || char == '_' -> {
                var end = index
                while (end < sql.length && (sql[end].isLetterOrDigit() || sql[end] == '_' || sql[end] == '$')) end++
                tokens += SqlToken.Word(sql.substring(index, end))
                index = end
            }

            else -> {
                tokens += SqlToken.Symbol(char)
                index++
            }
        }
    }
    return tokens
}
