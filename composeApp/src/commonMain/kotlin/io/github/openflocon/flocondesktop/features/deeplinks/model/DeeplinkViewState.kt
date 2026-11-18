package io.github.openflocon.flocondesktop.features.deeplinks.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkViewState(
    val label: String?,
    val deeplinkId: Long,
    val parts: List<DeeplinkPart>,
    val description: String?,
    val isHistory: Boolean,
) {
    /** The label, or the link itself with its fields as [name]. */
    val displayName: String
        get() = label ?: parts.joinToString(separator = "") {
            when (it) {
                is DeeplinkPart.Text -> it.value
                is DeeplinkPart.TextField -> "[${it.label}]"
                is DeeplinkPart.Variable -> it.value
            }
        }

    val variableNames: List<String>
        get() = parts.filterIsInstance<DeeplinkPart.Variable>().map { it.name }

    /** Variables of the link that have no value yet: the link can't be sent. */
    val missingVariablesCount: Int
        get() = parts.count { it is DeeplinkPart.Variable && !it.isResolved }

    /** Fields of the link, filled by hand before each send. */
    val fieldsCount: Int
        get() = parts.count { it is DeeplinkPart.TextField }
}

fun previewDeeplinkViewState() = DeeplinkViewState(
    label = "label",
    parts = listOf(
        DeeplinkPart.Text("flocon://myPath/"),
        DeeplinkPart.TextField("subRoute", autoComplete = null),
        DeeplinkPart.Text("?param1="),
        DeeplinkPart.TextField("param1", autoComplete = null),
    ),
    deeplinkId = 0L,
    description = "Vivamus risus justo, placerat ut ultricies sit amet, vulputate ac odio. Proin vehicula interdum leo lacinia posuere. Vivamus fringilla sapien et dui blandit convallis. Fusce in egestas elit, a gravida lectus. Sed rutrum posuere laoreet. Maecenas cursus lorem sed eros vestibulum facilisis. Phasellus ornare viverra ligula ut vehicula. Ut egestas eu lacus eu cursus. Sed vulputate viverra dolor, vitae facilisis dui ultrices ac.",
    isHistory = false,
)
