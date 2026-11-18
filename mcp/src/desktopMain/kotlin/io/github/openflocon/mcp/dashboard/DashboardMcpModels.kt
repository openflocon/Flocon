package io.github.openflocon.mcp.dashboard

import kotlinx.serialization.Serializable

@Serializable
internal data class DashboardsMcpResult(
    val deviceId: String,
    val packageName: String,
    val selectedDashboardId: String?,
    val dashboardIds: List<String>,
)

@Serializable
internal data class DashboardMcpResult(
    val deviceId: String,
    val packageName: String,
    val dashboardId: String,
    val containers: List<DashboardContainerMcpModel>,
)

@Serializable
internal data class DashboardContainerMcpModel(
    val name: String,
    /** "section" or "form" */
    val type: String,
    val formId: String? = null,
    val submitText: String? = null,
    val elements: List<DashboardElementMcpModel>,
)

/**
 * An element of a dashboard. [type] is one of: text, label, plain_text, json, markdown, html, text_field, check_box, button.
 */
@Serializable
internal data class DashboardElementMcpModel(
    val type: String,
    val label: String? = null,
    val value: String? = null,
    val checked: Boolean? = null,
    val placeHolder: String? = null,
    val id: String? = null,
    val color: Int? = null,
)
