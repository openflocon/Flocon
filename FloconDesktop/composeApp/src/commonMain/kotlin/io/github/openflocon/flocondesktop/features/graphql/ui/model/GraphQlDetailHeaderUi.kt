package io.github.openflocon.flocondesktop.features.graphql.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GraphQlDetailHeaderUi(
    val name: String,
    val value: String,
)

fun previewGraphQlDetailHeaderUi() = GraphQlDetailHeaderUi(
    name = "name",
    value = "value",
)
