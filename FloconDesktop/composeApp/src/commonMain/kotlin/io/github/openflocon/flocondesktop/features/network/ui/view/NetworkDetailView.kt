package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.ui.NetworkAction
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.ui.model.previewNetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.ui.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.ui.view.components.StatusView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.CodeBlockView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.DetailHeadersView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.DetailLineTextView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.DetailLineView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.DetailSectionTitleView
import io.github.openflocon.flocondesktop.features.network.ui.view.detail.ExpandedSectionView
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIconButton
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkDetailView(
    state: NetworkDetailViewState,
    onAction: (NetworkAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var isRequestExpanded by remember { mutableStateOf(true) }
    var isRequestBodyExpanded by remember { mutableStateOf(true) }
    var isRequestHeadersExpanded by remember { mutableStateOf(true) }
    var isGraphQlRequestExpanded by remember { mutableStateOf(true) }

    var isResponseExpanded by remember { mutableStateOf(true) }
    var isResponseHeadersExpanded by remember { mutableStateOf(true) }
    var isResponseBodyExpanded by remember { mutableStateOf(true) }

    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp

    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.background)
            .verticalScroll(scrollState) // Rendre le contenu défilable
            .padding(all = 12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            DetailSectionTitleView(
                isExpanded = isRequestExpanded,
                title = "Request",
                onCopy = null,
                onToggle = {
                    isRequestExpanded = it
                },
                modifier = Modifier.weight(1f)
            )
            FloconIconButton(
                imageVector = Icons.Outlined.Close,
                onClick = { onAction(NetworkAction.ClosePanel) }
            )
        }
        ExpandedSectionView(
            modifier = Modifier.fillMaxWidth(),
            isExpanded = isRequestExpanded,
        ) {
            Column(
                modifier =
                    Modifier
                        .background(
                            color = FloconTheme.colorPalette.surfaceVariant,
                            shape = RoundedCornerShape(12.dp),
                        ).padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Full url",
                    value = state.fullUrl,
                    labelWidth = linesLabelWidth,
                )
                DetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Method",
                    labelWidth = linesLabelWidth,
                ) {
                    when (val m = state.method) {
                        is NetworkDetailViewState.Method.Http -> MethodView(method = m.method)
                        is NetworkDetailViewState.Method.MethodName -> {
                            Text(
                                text = m.name,
                                style = FloconTheme.typography.bodySmall,
                                color = FloconTheme.colorPalette.onSurface,
                                modifier = Modifier.weight(2f)
                                    .background(
                                        color = FloconTheme.colorPalette.panel.copy(alpha = 0.8f),
                                        shape = RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                            )
                        }
                    }
                }
                DetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Status",
                    labelWidth = linesLabelWidth,
                ) {
                    StatusView(status = state.status)
                }
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Request Time",
                    value = state.requestTimeFormatted,
                    labelWidth = linesLabelWidth,
                )
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Time",
                    value = state.durationFormatted,
                    labelWidth = linesLabelWidth,
                )
            }

            state.graphQlSection?.let {
                Spacer(modifier = Modifier.height(12.dp))
                DetailSectionTitleView(
                    isExpanded = isRequestExpanded,
                    title = "GraphQl",
                    onCopy = null,
                    onToggle = {
                        isGraphQlRequestExpanded = !isGraphQlRequestExpanded
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                ExpandedSectionView(
                    modifier = Modifier.fillMaxWidth(),
                    isExpanded = isGraphQlRequestExpanded,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .background(
                                    color = FloconTheme.colorPalette.surfaceVariant,
                                    shape = RoundedCornerShape(12.dp),
                                ).padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        DetailLineTextView(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Query name",
                            value = it.queryName,
                            labelWidth = linesLabelWidth,
                        )
                        DetailLineView(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Type",
                            labelWidth = linesLabelWidth,
                        ) {
                            MethodView(method = it.method)
                        }
                    }
                }
            }

            // headers
            DetailSectionTitleView(
                isExpanded = isRequestHeadersExpanded,
                title = "Request Headers",
                onCopy = null,
                onToggle = {
                    isRequestHeadersExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExpandedSectionView(
                modifier = Modifier.fillMaxWidth(),
                isExpanded = isRequestHeadersExpanded,
            ) {
                DetailHeadersView(
                    headers = state.requestHeaders,
                    modifier = Modifier.fillMaxWidth(),
                    labelWidth = headersLabelWidth,
                )
            }

            // body
            DetailSectionTitleView(
                isExpanded = isRequestBodyExpanded,
                title = "Request Body",
                onCopy = { onAction(NetworkAction.CopyText(state.requestBody)) },
                onToggle = {
                    isRequestBodyExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExpandedSectionView(
                modifier = Modifier.fillMaxWidth(),
                isExpanded = isRequestBodyExpanded,
            ) {
                CodeBlockView(
                    code = state.requestBody,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        HorizontalDivider(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
                    .padding(vertical = 12.dp),
        )

        DetailSectionTitleView(
            isExpanded = isResponseExpanded,
            title = "Response",
            onCopy = null,
            onToggle = {
                isResponseExpanded = it
            },
            modifier = Modifier.fillMaxWidth()
        )
        ExpandedSectionView(
            modifier = Modifier.fillMaxWidth(),
            isExpanded = isResponseExpanded,
        ) {
            // headers
            DetailSectionTitleView(
                isExpanded = isResponseHeadersExpanded,
                title = "Response Headers",
                onCopy = null,
                onToggle = {
                    isResponseHeadersExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExpandedSectionView(
                modifier = Modifier.fillMaxWidth(),
                isExpanded = isResponseHeadersExpanded,
            ) {
                DetailHeadersView(
                    headers = state.responseHeaders,
                    modifier = Modifier.fillMaxWidth(),
                    labelWidth = headersLabelWidth,
                )
            }

            // body
            DetailSectionTitleView(
                isExpanded = isResponseBodyExpanded,
                title = "Response Body",
                onCopy = { onAction(NetworkAction.CopyText(state.responseBody)) },
                onToggle = {
                    isResponseBodyExpanded = it
                },
                modifier = Modifier.fillMaxWidth()
            )
            ExpandedSectionView(
                modifier = Modifier.fillMaxWidth(),
                isExpanded = isResponseBodyExpanded,
            ) {
                CodeBlockView(
                    code = state.responseBody,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview
@Composable
private fun NetworkDetailViewPreview() {
    FloconTheme {
        NetworkDetailView(
            state =
                NetworkDetailViewState(
                    fullUrl = "http://www.google.com",
                    method = NetworkDetailViewState.Method.Http(NetworkMethodUi.Http.GET),
                    status =
                        NetworkStatusUi(
                            text = "200",
                            isSuccess = true,
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
                    responseHeaders =
                        listOf(
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                            previewNetworkDetailHeaderUi(),
                        ),
                    requestTimeFormatted = "00:00:00.000",
                    durationFormatted = "300ms",
                    responseBody =
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
                    requestSize = "0kb",
                    responseSize = "0kb",
                    graphQlSection = null,
                ),
            modifier = Modifier.padding(16.dp), // Padding pour la preview
            onAction = {}
        )
    }
}
