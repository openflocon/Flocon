package io.github.openflocon.flocondesktop.features.graphql.ui.model

import androidx.compose.runtime.Immutable

@Immutable
data class GraphQlItemViewState(
    val uuid: String,
    val dateFormatted: String,
    val route: String,
    val method: GraphQlMethodUi,
    val graphQlStatusUi: GraphQlStatusUi,
    val requestSize: String,
    val responseSize: String,
    val timeFormatted: String,
)

fun previewGraphQlItemViewState(): GraphQlItemViewState = GraphQlItemViewState(
    uuid = "0",
    dateFormatted = "00:00:00.0000",
    route = "www.google.com.test",
    method = GraphQlMethodUi.GET,
    graphQlStatusUi = GraphQlStatusUi(200, true),
    requestSize = "10.kb",
    responseSize = "0.B",
    timeFormatted = "333ms",
)
