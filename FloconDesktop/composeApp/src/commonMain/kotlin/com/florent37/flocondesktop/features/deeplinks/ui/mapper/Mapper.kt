package com.florent37.flocondesktop.features.deeplinks.ui.mapper

import com.florent37.flocondesktop.features.deeplinks.domain.model.DeeplinkDomainModel
import com.florent37.flocondesktop.features.deeplinks.ui.model.DeeplinkPart
import com.florent37.flocondesktop.features.deeplinks.ui.model.DeeplinkViewState

internal fun mapToUi(deepLinks: List<DeeplinkDomainModel>): List<DeeplinkViewState> = buildList {
    addAll(
        deepLinks.map {
            mapToUi(it)
        },
    )
}

internal fun mapToUi(deepLink: DeeplinkDomainModel): DeeplinkViewState = DeeplinkViewState(
    label = deepLink.label,
    description = deepLink.description,
    parts = parseDeeplinkString(deepLink.link),
)

internal fun parseDeeplinkString(input: String): List<DeeplinkPart> {
    val regex = "\\[([^\\[\\]]*)\\]".toRegex() // Regex pour trouver [quelquechose]
    val result = mutableListOf<DeeplinkPart>()
    var lastIndex = 0

    regex.findAll(input).forEach { matchResult ->
        val range = matchResult.range
        val value = matchResult.groupValues[1] // Le contenu entre les crochets

        // 1. Ajouter la partie "Text" avant le [value]
        if (range.first > lastIndex) {
            val textContent = input.substring(lastIndex, range.first)
            if (textContent.isNotEmpty()) {
                result.add(DeeplinkPart.Text(textContent))
            }
        }

        // 2. Ajouter la partie "TextField"
        result.add(DeeplinkPart.TextField(value))

        lastIndex = range.last + 1
    }

    // 3. Ajouter la dernière partie "Text" après le dernier [value] (s'il y en a une)
    if (lastIndex < input.length) {
        val remainingText = input.substring(lastIndex)
        if (remainingText.isNotEmpty()) {
            result.add(DeeplinkPart.Text(remainingText))
        }
    }

    return result
}
