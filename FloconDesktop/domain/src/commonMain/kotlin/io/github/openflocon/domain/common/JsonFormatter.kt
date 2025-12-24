package io.github.openflocon.domain.common

interface JsonFormatter {
    fun toPrettyJson(text: String) : String
}