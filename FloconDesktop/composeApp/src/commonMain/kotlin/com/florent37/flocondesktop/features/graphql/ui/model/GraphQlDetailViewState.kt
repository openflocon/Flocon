package com.florent37.flocondesktop.features.graphql.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GraphQlDetailViewState(
    val fullUrl: String,
    val requestTimeFormatted: String,
    val durationFormatted: String,
    val method: GraphQlMethodUi,
    val status: GraphQlStatusUi,
    // request
    val requestBody: String,
    val requestSize: String,
    val requestHeaders: List<GraphQlDetailHeaderUi>,
    // response
    val responseBody: String,
    val responseSize: String,
    val responseHeaders: List<GraphQlDetailHeaderUi>,
)
