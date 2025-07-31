package io.github.openflocon.flocondesktop.features.graphql.ui.model

import androidx.compose.runtime.Immutable

@Immutable
sealed class GraphQlMethodUi(
    open val text: String,
) {
    @Immutable
    data object GET : GraphQlMethodUi(text = "GET")

    @Immutable
    data object POST : GraphQlMethodUi(text = "POST")

    @Immutable
    data class Other(override val text: String) : GraphQlMethodUi(text = "POST")
}
