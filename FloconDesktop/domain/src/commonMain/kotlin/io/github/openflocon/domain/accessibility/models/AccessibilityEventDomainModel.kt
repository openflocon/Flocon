package io.github.openflocon.domain.accessibility.models

data class AccessibilityEventDomainModel(
    val id: String,
    val type: String, // e.g. VIEW_FOCUSED, ANNOUNCEMENT
    val text: String?,
    val contentDescription: String?,
    val className: String?,
    val screen: String?,
    val bounds: String?,
    val extras: Map<String, String>,
    val createdAt: Long,
)
