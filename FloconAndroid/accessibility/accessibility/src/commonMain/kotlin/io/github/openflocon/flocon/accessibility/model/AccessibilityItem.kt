package io.github.openflocon.flocon.accessibility.model

import io.github.openflocon.flocon.utils.currentTimeMillis
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
internal data class AccessibilityItem(
    val id: String,
    val type: String,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String> = emptyMap(),
    val createdAt: Long,
)

@OptIn(ExperimentalUuidApi::class)
internal fun AccessibilityEvent.toItem() = AccessibilityItem(
    id = Uuid.random().toString(),
    type = type,
    text = text,
    contentDescription = contentDescription,
    className = className,
    screen = screen,
    bounds = bounds,
    extras = extras,
    createdAt = currentTimeMillis(),
)
