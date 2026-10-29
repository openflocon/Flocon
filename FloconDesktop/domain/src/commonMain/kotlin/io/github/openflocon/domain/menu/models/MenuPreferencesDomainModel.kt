package io.github.openflocon.domain.menu.models

data class MenuPreferencesDomainModel(
    val pinnedItems: List<String>,
    val collapsedSections: Set<String>,
    val hiddenItems: Set<String> = emptySet(),
)
