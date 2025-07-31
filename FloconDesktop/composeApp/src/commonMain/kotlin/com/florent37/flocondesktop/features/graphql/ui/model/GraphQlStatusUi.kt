package com.florent37.flocondesktop.features.graphql.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GraphQlStatusUi(
    val code: Int,
    val isSuccess: Boolean,
)
