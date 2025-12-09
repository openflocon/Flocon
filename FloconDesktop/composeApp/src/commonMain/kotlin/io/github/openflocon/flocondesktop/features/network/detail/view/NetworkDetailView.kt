package io.github.openflocon.flocondesktop.features.network.detail.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailAction
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailViewModel
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.view.components.DetailHeadersView
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val LARGE_BODY_LENGHT = 30_000

@Composable
fun NetworkDetailScreen(
    requestId: String
) {
    val viewModel = koinViewModel<NetworkDetailViewModel>(key = requestId) {
        parametersOf(requestId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkDetailContent(
        uiState = uiState,
        onAction = viewModel::onAction
    )
}

@Composable
fun NetworkDetailContent(
    uiState: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState: ScrollState = rememberScrollState()
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)
    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp

    Box(
        modifier = modifier
            .background(FloconTheme.colorPalette.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 8.dp, horizontal = 4.dp),
        ) {
            Request(
                modifier = Modifier
                    .fillMaxWidth(),
                state = uiState,
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
                state = uiState,
                onAction = onAction,
                headersLabelWidth = headersLabelWidth,
            )
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun Request(
    state: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
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
                                        color = FloconTheme.colorPalette.primary.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = state.statusLabel,
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

                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Request Size",
                    value = state.requestSize,
                    labelWidth = linesLabelWidth,
                    contentColor = FloconTheme.colorPalette.onPrimary
                )
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

            state.imageUrl?.let { imageUrl ->
                // Coil image
                AsyncImage(
                    model = remember(state) {
                        ImageRequest.Builder(PlatformContext.INSTANCE)
                            .data(imageUrl)
                            .httpHeaders(
                                NetworkHeaders.Builder().apply {
                                    state.imageHeaders?.forEach { (key, value) ->
                                        set(key, value)
                                    }
                                }.build()
                            )
                            .build()
                    },
                    contentDescription = "Image preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .padding(4.dp)
                        .height(300.dp),
                    contentScale = ContentScale.Fit,
                )
            }

            state.requestHeaders?.let {
                FloconSection(
                    title = "Request - Headers",
                    initialValue = true,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    DetailHeadersView(
                        headers = state.requestHeaders,
                        labelWidth = headersLabelWidth,
                        onAuthorizationClicked = { token -> onAction(NetworkDetailAction.DisplayBearerJwt(token)) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
            }
            FloconSection(
                title = state.requestBodyTitle,
                initialValue = true,
                actions = {
                    if (state.requestBodyIsNotBlank) {
                        FloconIconButton(
                            tooltip = "View in app",
                            imageVector = Icons.Outlined.OpenInFull,
                            onClick = { onAction(NetworkDetailAction.JsonDetail(json = state.requestBody,)) }
                        )
                    }
                    if (state.canOpenRequestBody) {
                        FloconIconButton(
                            tooltip = "Open in external editor",
                            imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                            onClick = { onAction(NetworkDetailAction.OpenBodyExternally.Request(state)) }
                        )
                    }
                    if (state.requestBodyIsNotBlank) {
                        FloconIconButton(
                            tooltip = "Copy",
                            imageVector = Icons.Outlined.CopyAll,
                            onClick = { onAction(NetworkDetailAction.CopyText(state.requestBody)) }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                var displayBody by remember(state.requestBody) {
                    val isLargeResponse = state.requestBody.length > LARGE_BODY_LENGHT
                    mutableStateOf(!isLargeResponse)
                }
                if (!displayBody) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                            .background(
                                FloconTheme.colorPalette.secondary,
                                shape = FloconTheme.shapes.medium,
                            )
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Response body too large (${state.requestSize} bytes)",
                            color = FloconTheme.colorPalette.onPrimary,
                            style = FloconTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        FloconButton(
                            onClick = {
                                displayBody = true
                            },
                            containerColor = FloconTheme.colorPalette.tertiary,
                        ) {
                            Text("Display anyway")
                        }
                    }
                } else {
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
}

@Composable
private fun Response(
    state: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
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
                    Column(
                        modifier = Modifier
                            .padding(bottom = 12.dp)
                            .background(
                                color = FloconTheme.colorPalette.secondary,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        FloconLineDescription(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Response Size",
                            value = response.size,
                            labelWidth = 130.dp,
                            contentColor = FloconTheme.colorPalette.onPrimary
                        )
                    }

                    response.headers?.let {
                        FloconSection(
                            title = "Response - Headers",
                            initialValue = true,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            DetailHeadersView(
                                headers = response.headers,
                                labelWidth = headersLabelWidth,
                                onAuthorizationClicked = { token -> onAction(NetworkDetailAction.DisplayBearerJwt(token)) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                            )
                        }
                    }

                    FloconSection(
                        title = "Response - Body",
                        initialValue = true,
                        actions = {
                            if (response.responseBodyIsNotBlank) {
                                FloconIconButton(
                                    tooltip = "View body in app",
                                    imageVector = Icons.Outlined.OpenInFull,
                                    onClick = { onAction(NetworkDetailAction.JsonDetail(response.body),) }
                                )
                            }
                            if (response.canOpenResponseBody) {
                                FloconIconButton(
                                    tooltip = "Open in external editor",
                                    imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                    onClick = { onAction(NetworkDetailAction.OpenBodyExternally.Response(response,)) }
                                )
                            }
                            if (response.responseBodyIsNotBlank) {
                                FloconIconButton(
                                    tooltip = "Copy",
                                    imageVector = Icons.Outlined.CopyAll,
                                    onClick = { onAction(NetworkDetailAction.CopyText(response.body)) }
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var displayBody by remember(response.body) {
                            val isLargeResponse = response.body.length > LARGE_BODY_LENGHT
                            mutableStateOf(!isLargeResponse)
                        }
                        if (!displayBody) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                                    .background(
                                        FloconTheme.colorPalette.secondary,
                                        shape = FloconTheme.shapes.medium,
                                    )
                                    .padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Response body too large (${response.size} bytes)",
                                    color = FloconTheme.colorPalette.onPrimary,
                                    style = FloconTheme.typography.bodySmall,
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                FloconButton(
                                    onClick = {
                                        displayBody = true
                                    },
                                    containerColor = FloconTheme.colorPalette.tertiary,
                                ) {
                                    Text("Display anyway")
                                }
                            }
                        } else {
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
}

@Preview
@Composable
private fun NetworkDetailViewPreview() {
    FloconTheme {
        NetworkDetailContent(
            uiState = NetworkDetailViewState(
                callId = "",
                fullUrl = "http://www.google.com",
                method = NetworkDetailViewState.Method.Http(NetworkMethodUi.Http.GET),
                statusLabel = "Status",
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
                requestBodyTitle = "Request - Body",
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
                    canOpenResponseBody = true,
                    responseBodyIsNotBlank = true,
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
                imageUrl = null,
                canOpenRequestBody = true,
                requestBodyIsNotBlank = true,
                imageHeaders = persistentMapOf(),
            ),
            onAction = {}
        )
    }
}
