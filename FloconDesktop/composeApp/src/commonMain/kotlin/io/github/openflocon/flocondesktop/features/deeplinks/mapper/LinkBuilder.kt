package io.github.openflocon.flocondesktop.features.deeplinks.mapper

import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkVariableViewState

/** The link to send: the fields are replaced by [values], the variables are already resolved. */
internal fun List<DeeplinkPart>.buildLink(
    values: Map<DeeplinkPart.TextField, String> = emptyMap(),
): String = joinToString(separator = "") {
    when (it) {
        is DeeplinkPart.Text -> it.value
        is DeeplinkPart.TextField -> values[it] ?: ""
        is DeeplinkPart.Variable -> it.value
    }
}

private val FreeformVariableRegex = "\\{([^{}]+)}".toRegex()

/**
 * A free link can use the shared variables, written {name}.
 * Names that are not a declared variable stay in the text as they are.
 */
internal fun parseFreeformLink(
    link: String,
    variables: List<DeeplinkVariableViewState>,
): List<DeeplinkPart> {
    val result = mutableListOf<DeeplinkPart>()
    var lastIndex = 0

    FreeformVariableRegex.findAll(link).forEach { match ->
        val variable = variables.firstOrNull { it.name == match.groupValues[1] } ?: return@forEach
        if (match.range.first > lastIndex) {
            result.add(DeeplinkPart.Text(link.substring(lastIndex, match.range.first)))
        }
        result.add(
            DeeplinkPart.Variable(
                value = variable.value.ifEmpty { "{${variable.name}}" },
                name = variable.name,
                isResolved = variable.value.isNotEmpty(),
            )
        )
        lastIndex = match.range.last + 1
    }
    if (lastIndex < link.length) {
        result.add(DeeplinkPart.Text(link.substring(lastIndex)))
    }
    return result
}

/**
 * Where the declared variables are written in a free link as typed, with whether each one has a value:
 * used to color them in the text field.
 */
internal fun freeformVariableRanges(
    link: String,
    variables: List<DeeplinkVariableViewState>,
): List<Pair<IntRange, Boolean>> = FreeformVariableRegex.findAll(link).mapNotNull { match ->
    val variable = variables.firstOrNull { it.name == match.groupValues[1] } ?: return@mapNotNull null
    match.range to variable.value.isNotEmpty()
}.toList()
