package io.github.openflocon.flocondesktop.features.database.view

internal enum class SqlTokenType { Comment, String, Number, Keyword, Text }

internal data class SqlToken(val text: String, val type: SqlTokenType)

private val SqlKeywords = setOf(
    "SELECT", "FROM", "WHERE", "AND", "OR", "INSERT", "INTO", "VALUES",
    "UPDATE", "SET", "DELETE", "CREATE", "TABLE", "DROP", "ALTER",
    "JOIN", "LEFT", "RIGHT", "INNER", "OUTER", "ON", "AS", "DISTINCT",
    "GROUP", "BY", "ORDER", "LIMIT", "HAVING",
)

// block comments, string literals, numbers, words, whitespace, then any other character
private val SqlTokenRegex = Regex("""(/\*[\s\S]*?\*/|'[^']*'|\b\d+\b|\b\w+\b|\s+|.)""", RegexOption.MULTILINE)
private val NumberRegex = Regex("\\d+")

/**
 * Splits a SQL text into tokens, keeping every character: joining the tokens gives back [text].
 */
internal fun sqlTokens(text: String): List<SqlToken> = SqlTokenRegex.findAll(text).map { match ->
    val token = match.value
    val type = when {
        token.startsWith("/*") && token.endsWith("*/") -> SqlTokenType.Comment
        token.startsWith("'") && token.endsWith("'") && token.length > 1 -> SqlTokenType.String
        NumberRegex.matches(token) -> SqlTokenType.Number
        token.uppercase() in SqlKeywords -> SqlTokenType.Keyword
        else -> SqlTokenType.Text
    }
    SqlToken(token, type)
}.toList()
