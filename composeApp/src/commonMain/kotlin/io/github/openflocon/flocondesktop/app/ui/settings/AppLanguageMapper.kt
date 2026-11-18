package io.github.openflocon.flocondesktop.app.ui.settings

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import java.util.Locale

// the default strings (values/strings.xml) are in English
private const val DEFAULT_LANGUAGE_TAG = "en"

/** The languages that can be chosen: English plus the ones that have a strings file. */
fun availableLanguages(): ImmutableList<LanguageUiModel> = (listOf(DEFAULT_LANGUAGE_TAG) + AvailableLanguageTags)
    .distinct()
    .map { tag -> LanguageUiModel(tag = tag, label = languageLabel(tag)) }
    .sortedBy { it.label.lowercase() }
    .toImmutableList()

// each language is named in itself, so it can be found whatever the current one is
private fun languageLabel(tag: String): String {
    val locale = Locale.forLanguageTag(tag)
    return locale.getDisplayName(locale).replaceFirstChar { it.titlecase(locale) }
}

fun String.toLocale(): Locale = Locale.forLanguageTag(this)
