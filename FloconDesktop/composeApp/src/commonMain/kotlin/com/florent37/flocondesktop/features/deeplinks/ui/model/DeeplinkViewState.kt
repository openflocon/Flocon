package com.florent37.flocondesktop.features.deeplinks.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class DeeplinkViewState(
    val label: String?,
    val parts: List<DeeplinkPart>,
    val description: String?,
)

fun previewDeeplinkViewState() = DeeplinkViewState(
    label = "label",
    parts = listOf(
        DeeplinkPart.Text("flocon://myPath/"),
        DeeplinkPart.TextField("subRoute"),
        DeeplinkPart.Text("?param1="),
        DeeplinkPart.TextField("param1"),
    ),
    description = "Vivamus risus justo, placerat ut ultricies sit amet, vulputate ac odio. Proin vehicula interdum leo lacinia posuere. Vivamus fringilla sapien et dui blandit convallis. Fusce in egestas elit, a gravida lectus. Sed rutrum posuere laoreet. Maecenas cursus lorem sed eros vestibulum facilisis. Phasellus ornare viverra ligula ut vehicula. Ut egestas eu lacus eu cursus. Sed vulputate viverra dolor, vitae facilisis dui ultrices ac.",
)
