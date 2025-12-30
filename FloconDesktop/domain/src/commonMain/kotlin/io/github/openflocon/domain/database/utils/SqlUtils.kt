package io.github.openflocon.domain.database.utils

fun injectSqlArgs(sql: String, args: List<String>?): String {
    return if (args.isNullOrEmpty()) {
        sql
    } else {
        var result = sql
        args.forEach { arg ->
            result = result.replaceFirst("?", "'$arg'")
        }
        result
    }
}
