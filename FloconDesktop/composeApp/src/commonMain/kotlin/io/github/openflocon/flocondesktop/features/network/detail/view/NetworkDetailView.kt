package io.github.openflocon.flocondesktop.features.network.detail.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.view.components.DetailHeadersView
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkAction
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.escape.EscapeHandler
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkDetailView(
    state: NetworkDetailViewState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp

//    EscapeHandler {
//        onAction(NetworkAction.ClosePanel)
//        true // consumed
//    }

    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.primary)
            .verticalScroll(scrollState)
            .padding(vertical = 8.dp, horizontal = 4.dp),
    ) {
        Request(
            modifier = Modifier
                .fillMaxWidth(),
            state = state,
            onAction = onAction,
            linesLabelWidth = linesLabelWidth,
            headersLabelWidth = headersLabelWidth,
        )
        FloconHorizontalDivider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
                .padding(vertical = 8.dp),
        )
        Response(
            modifier = Modifier
                .fillMaxWidth(),
            state = state,
            onAction = onAction,
            headersLabelWidth = headersLabelWidth,
        )
    }
}

@Composable
private fun Request(
    state: NetworkDetailViewState,
    onAction: (NetworkAction) -> Unit,
    linesLabelWidth: Dp,
    headersLabelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    FloconSection(
        title = "Request",
        initialValue = true,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(
                        color = FloconTheme.colorPalette.secondary,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Full url",
                    value = state.fullUrl,
                    contentColor = FloconTheme.colorPalette.onPrimary,
                    labelWidth = linesLabelWidth,
                )
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Method",
                    contentColor = FloconTheme.colorPalette.onPrimary,
                    labelWidth = linesLabelWidth,
                ) {
                    when (val m = state.method) {
                        is NetworkDetailViewState.Method.Http -> MethodView(method = m.method)
                        is NetworkDetailViewState.Method.MethodName -> {
                            Text(
                                text = m.name,
                                style = FloconTheme.typography.bodySmall,
                                color = FloconTheme.colorPalette.onSecondary,
                                modifier = Modifier.weight(2f)
                                    .background(
                                        color = FloconTheme.colorPalette.secondary.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Status",
                    contentColor = FloconTheme.colorPalette.onPrimary,
                    labelWidth = linesLabelWidth,
                ) {
                    StatusView(
                        status = state.status,
                    )
                }
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Request Time",
                    value = state.requestTimeFormatted,
                    labelWidth = linesLabelWidth,
                    contentColor = FloconTheme.colorPalette.onPrimary
                )
                state.durationFormatted?.let {
                    FloconLineDescription(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Time",
                        value = it,
                        labelWidth = linesLabelWidth,
                        contentColor = FloconTheme.colorPalette.onPrimary
                    )
                }
            }

            state.graphQlSection?.let {
                Spacer(modifier = Modifier.height(12.dp))
                FloconSection(
                    title = "GraphQl",
                    initialValue = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .background(
                                color = FloconTheme.colorPalette.secondary,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(12.dp)
                    ) {
                        FloconLineDescription(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Query name",
                            value = it.queryName,
                            contentColor = FloconTheme.colorPalette.onSecondary,
                            labelWidth = linesLabelWidth,
                        )
                        FloconLineDescription(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Type",
                            labelWidth = linesLabelWidth,
                            contentColor = FloconTheme.colorPalette.onSecondary
                        ) {
                            MethodView(method = it.method)
                        }
                    }
                }
            }

            FloconSection(
                title = "Request - Headers",
                initialValue = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailHeadersView(
                    headers = state.requestHeaders,
                    labelWidth = headersLabelWidth,
                    onAuthorizationClicked = { token -> onAction(NetworkAction.DisplayBearerJwt(token)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
            FloconSection(
                title = "Request - Body",
                initialValue = true,
                actions = {
                    FloconIconButton(
                        imageVector = Icons.Outlined.OpenInFull,
                        onClick = {
                            onAction(
                                NetworkAction.JsonDetail(
                                    state.callId + "request",
                                    state.requestBody,
                                ),
                            )
                        }
                    )
                    FloconIconButton(
                        imageVector = Icons.Outlined.CopyAll,
                        onClick = { onAction(NetworkAction.CopyText(state.requestBody)) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                FloconCodeBlock(
                    code = state.requestBody,
                    containerColor = FloconTheme.colorPalette.secondary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )
            }
        }
    }
}

@Composable
private fun Response(
    state: NetworkDetailViewState,
    onAction: (NetworkAction) -> Unit,
    headersLabelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    val response = state.response ?: return

    FloconSection(
        title = "Response",
        initialValue = true,
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            when (response) {
                is NetworkDetailViewState.Response.Error -> {
                    FloconSection(
                        title = "Response - Body",
                        initialValue = true
                    ) {
                        FloconCodeBlock(
                            code = response.issue,
                            containerColor = FloconTheme.colorPalette.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }

                is NetworkDetailViewState.Response.Success -> {
                    FloconSection(
                        title = "Response - Headers",
                        initialValue = true,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DetailHeadersView(
                            headers = response.headers,
                            labelWidth = headersLabelWidth,
                            onAuthorizationClicked = { token -> onAction(NetworkAction.DisplayBearerJwt(token)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                    FloconSection(
                        title = "Response - Body",
                        initialValue = true,
                        actions = {
                            FloconIconButton(
                                imageVector = Icons.Outlined.OpenInFull,
                                onClick = {
                                    onAction(
                                        NetworkAction.JsonDetail(
                                            state.callId + "response",
                                            response.body,
                                        ),
                                    )
                                }
                            )
                            FloconIconButton(
                                imageVector = Icons.Outlined.CopyAll,
                                onClick = { onAction(NetworkAction.CopyText(response.body)) }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FloconCodeBlock(
                            code = response.body,
                            containerColor = FloconTheme.colorPalette.secondary,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun NetworkDetailViewPreview() {
    FloconTheme {
        NetworkDetailView(
            state = NetworkDetailViewState(
                callId = "",
                fullUrl = "http://www.google.com",
                method = NetworkDetailViewState.Method.Http(NetworkMethodUi.Http.GET),
                status =
                    NetworkStatusUi(
                        text = "200",
                        status = NetworkStatusUi.Status.SUCCESS,
                    ),
                requestHeaders =
                    listOf(
                        previewNetworkDetailHeaderUi(),
                        previewNetworkDetailHeaderUi(),
                        previewNetworkDetailHeaderUi(),
                    ),
                requestBody =
                    """
                        {
                            "id": "123",
                            "name": "Flocon App",
                            "version": "1.0.0",
                            "data": {
                                "items": [
                                    {"key": "value1"},
                                    {"key": "value2"}
                                ]
                            }
                        }
                """.trimIndent(),
                requestTimeFormatted = "00:00:00.000",
                durationFormatted = "300ms",
                requestSize = "0kb",
                response = NetworkDetailViewState.Response.Success(
                    body =
                        """
                        {
                            "networkStatusUi": "success",
                            "message": "Data received and processed.",
                            "result": {
                                "timestamp": "2025-07-05T23:59:00Z",
                                "processed_count": 2
                            }
                        }
                    """.trimIndent(),
                    size = "0kb",
                    headers =
                        listOf(
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                        ),
                ),
                graphQlSection = null,
            ),
            modifier = Modifier.padding(16.dp), // Padding pour la preview
            onAction = {},
        )
    }
}
