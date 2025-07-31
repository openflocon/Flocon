package io.github.openflocon.flocondesktop.features.grpc.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.florent37.flocondesktop.common.ui.FloconColors
import com.florent37.flocondesktop.common.ui.FloconTheme
import com.florent37.flocondesktop.features.grpc.ui.model.GrpcDetailViewState
import com.florent37.flocondesktop.features.grpc.ui.model.previewGrpcDetailViewState
import com.florent37.flocondesktop.features.network.ui.view.detail.CodeBlockView
import com.florent37.flocondesktop.features.network.ui.view.detail.DetailHeadersView
import com.florent37.flocondesktop.features.network.ui.view.detail.DetailLineTextView
import com.florent37.flocondesktop.features.network.ui.view.detail.DetailLineView
import com.florent37.flocondesktop.features.network.ui.view.detail.DetailSectionTitleView
import com.florent37.flocondesktop.features.network.ui.view.detail.ExpandedSectionView
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun GrpcDetailView(
    state: GrpcDetailViewState,
    onCopy: (String) -> Unit, // Le lambda onCopy remplace ClipboardManager
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    var isRequestExpanded by remember { mutableStateOf(true) }
    var isRequestBodyExpanded by remember { mutableStateOf(true) }
    var isRequestHeadersExpanded by remember { mutableStateOf(true) }

    var isResponseExpanded by remember { mutableStateOf(true) }
    var isResponseHeadersExpanded by remember { mutableStateOf(true) }
    var isResponseBodyExpanded by remember { mutableStateOf(true) }

    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp

    Column(
        modifier =
        modifier
            .background(FloconColors.background)
            .verticalScroll(scrollState) // Rendre le contenu défilable
            .padding(all = 12.dp),
    ) {
        DetailSectionTitleView(
            isExpanded = isRequestExpanded,
            title = "Request",
            onCopy = null,
            onToggle = {
                isRequestExpanded = it
            },
        )
        ExpandedSectionView(
            modifier = Modifier.fillMaxWidth(),
            isExpanded = isRequestExpanded,
        ) {
            Column(
                modifier =
                Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(12.dp),
                    ).padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Url",
                    value = state.url,
                    labelWidth = linesLabelWidth,
                )
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Method",
                    labelWidth = linesLabelWidth,
                    value = state.method,
                )
                DetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Status",
                    labelWidth = linesLabelWidth,
                ) {
                    GrpcStatusView(status = state.status)
                }
                DetailLineTextView(
                    modifier = Modifier.fillMaxWidth(),
                    label = "Request Time",
                    value = state.requestTimeFormatted,
                    labelWidth = linesLabelWidth,
                )
                state.durationFormatted?.let {
                    DetailLineTextView(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Duration",
                        value = it,
                        labelWidth = linesLabelWidth,
                    )
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
                onCopy = {
                    onCopy(state.requestBody ?: "")
                },
                onToggle = {
                    isRequestBodyExpanded = it
                },
            )
            ExpandedSectionView(
                modifier = Modifier.fillMaxWidth(),
                isExpanded = isRequestBodyExpanded,
            ) {
                CodeBlockView(
                    code = state.requestBody ?: "",
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

        state.response?.let { response ->
            DetailSectionTitleView(
                isExpanded = isResponseExpanded,
                title = "Response",
                onCopy = null,
                onToggle = {
                    isResponseExpanded = it
                },
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
                )
                ExpandedSectionView(
                    modifier = Modifier.fillMaxWidth(),
                    isExpanded = isResponseHeadersExpanded,
                ) {
                    DetailHeadersView(
                        headers = response.headers,
                        modifier = Modifier.fillMaxWidth(),
                        labelWidth = headersLabelWidth,
                    )
                }

                when (val r = response.result) {
                    is GrpcDetailViewState.DetailPayload.Failure -> {
                        // body
                        DetailSectionTitleView(
                            isExpanded = isResponseBodyExpanded,
                            title = "Response Error",
                            onCopy = {
                                onCopy(r.cause)
                            },
                            onToggle = {
                                isResponseBodyExpanded = it
                            },
                        )
                        ExpandedSectionView(
                            modifier = Modifier.fillMaxWidth(),
                            isExpanded = isResponseBodyExpanded,
                        ) {
                            CodeBlockView(
                                code = r.cause,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                    is GrpcDetailViewState.DetailPayload.Success -> {
                        // body
                        DetailSectionTitleView(
                            isExpanded = isResponseBodyExpanded,
                            title = "Response Body",
                            onCopy = {
                                onCopy(r.body)
                            },
                            onToggle = {
                                isResponseBodyExpanded = it
                            },
                        )
                        ExpandedSectionView(
                            modifier = Modifier.fillMaxWidth(),
                            isExpanded = isResponseBodyExpanded,
                        ) {
                            CodeBlockView(
                                code = r.body,
                                modifier = Modifier.fillMaxWidth(),
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
private fun GrpcDetailViewPreview() {
    FloconTheme {
        GrpcDetailView(
            state = previewGrpcDetailViewState(),
            modifier = Modifier.padding(16.dp), // Padding pour la preview
            onCopy = { },
        )
    }
}
