package com.flocon.data.remote.accessibility.model

import kotlinx.serialization.Serializable

@Serializable
data class AccessibilityItemDataModel(
    val id: String,
    val type: String,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String>? = emptyMap(),
    val createdAt: Long,
)

@Serializable
data class AccessibilityStateDataModel(
    val supported: Boolean,
    val enabled: Boolean,
    val touchExploration: Boolean = false,
    val services: List<String>? = emptyList(),
    val canToggleTalkBack: Boolean = false,
)

@Serializable
data class AccessibilityFocusedNodeDataModel(
    val found: Boolean = false,
    val text: String? = null,
    val contentDescription: String? = null,
    val className: String? = null,
    val screen: String? = null,
    val bounds: String? = null,
    val extras: Map<String, String>? = emptyMap(),
    val index: Int? = null,
    val count: Int? = null,
    val hasNext: Boolean = false,
    val hasPrevious: Boolean = false,
)
