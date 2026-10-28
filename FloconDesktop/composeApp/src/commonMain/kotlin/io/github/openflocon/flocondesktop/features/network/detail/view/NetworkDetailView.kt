package io.github.openflocon.flocondesktop.features.network.detail.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Difference
import androidx.compose.material.icons.outlined.Html
import androidx.compose.material.icons.outlined.OpenInFull
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Terminal
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.PlatformContext
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.ImageRequest
import com.composeunstyled.DropdownPanelAnchor
import com.sebastianneubauer.jsontree.TreeState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.diff_compare_clipboard
import flocondesktop.composeapp.generated.resources.general_copy
import flocondesktop.composeapp.generated.resources.general_copy_as_image
import flocondesktop.composeapp.generated.resources.general_copy_as_markdown
import flocondesktop.composeapp.generated.resources.general_save_as_image
import flocondesktop.composeapp.generated.resources.general_type
import flocondesktop.composeapp.generated.resources.images_preview
import flocondesktop.composeapp.generated.resources.network_body
import flocondesktop.composeapp.generated.resources.network_copy_curl_command
import flocondesktop.composeapp.generated.resources.network_details_title
import flocondesktop.composeapp.generated.resources.network_display_anyway
import flocondesktop.composeapp.generated.resources.network_export_body_image
import flocondesktop.composeapp.generated.resources.network_export_error_image
import flocondesktop.composeapp.generated.resources.network_export_graphql_image
import flocondesktop.composeapp.generated.resources.network_export_headers_image
import flocondesktop.composeapp.generated.resources.network_export_response_summary_image
import flocondesktop.composeapp.generated.resources.network_export_summary_image
import flocondesktop.composeapp.generated.resources.network_full_url
import flocondesktop.composeapp.generated.resources.network_header_method
import flocondesktop.composeapp.generated.resources.network_header_request_time
import flocondesktop.composeapp.generated.resources.network_header_time
import flocondesktop.composeapp.generated.resources.network_headers
import flocondesktop.composeapp.generated.resources.network_log_title
import flocondesktop.composeapp.generated.resources.network_query_name
import flocondesktop.composeapp.generated.resources.network_request_headers
import flocondesktop.composeapp.generated.resources.network_request_size
import flocondesktop.composeapp.generated.resources.network_response_body
import flocondesktop.composeapp.generated.resources.network_response_error
import flocondesktop.composeapp.generated.resources.network_response_headers
import flocondesktop.composeapp.generated.resources.network_response_size
import flocondesktop.composeapp.generated.resources.network_save_full_log_image
import flocondesktop.composeapp.generated.resources.network_save_html
import flocondesktop.composeapp.generated.resources.network_share_export
import flocondesktop.composeapp.generated.resources.network_view_body_in_app
import flocondesktop.composeapp.generated.resources.network_view_in_app
import flocondesktop.composeapp.generated.resources.network_waiting_response
import flocondesktop.composeapp.generated.resources.open_external
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailAction
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailViewModel
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.view.components.DetailHeaders
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodTag
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusCell
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconSegmentedRow
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.TabType
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val LARGE_BODY_LENGTH = 1_000_000

@Composable
fun NetworkDetailScreen(
    requestId: String,
    key: String = requestId,
    tab: NetworkDetailTab? = null, // null: the default tab of the settings
) {
    val viewModel = koinViewModel<NetworkDetailViewModel>(key = key) {
        parametersOf(requestId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkDetailContent(
        uiState = uiState,
        tab = tab,
        onAction = viewModel::onAction
    )
}

@Composable
private fun ExportImageDropdown(
    graphicsLayer: GraphicsLayer,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    tooltip: String = "Export as image",
) {
    val coroutineScope = rememberCoroutineScope()
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        FloconDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            onExpandRequest = { expanded = true },
            anchor = DropdownPanelAnchor.BottomEnd,
            anchorContent = {
                FloconIconButton(
                    tooltip = tooltip,
                    imageVector = Icons.Outlined.CameraAlt,
                    onClick = { expanded = true }
                )
            }
        ) {
            FloconDropdownMenuItem(
                text = stringResource(Res.string.general_copy_as_image),
                leadingIcon = Icons.Outlined.CameraAlt,
                onClick = {
                    expanded = false
                    coroutineScope.launch {
                        try {
                            val bitmap = graphicsLayer.toImageBitmap()
                            onAction(NetworkDetailAction.CopyImage(bitmap))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
            FloconDropdownMenuItem(
                text = stringResource(Res.string.general_save_as_image),
                leadingIcon = Icons.Outlined.Save,
                onClick = {
                    expanded = false
                    coroutineScope.launch {
                        try {
                            val bitmap = graphicsLayer.toImageBitmap()
                            onAction(NetworkDetailAction.SaveImage(bitmap))
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun NetworkDetailContent(
    uiState: NetworkDetailViewState,
    tab: NetworkDetailTab?,
    onAction: (NetworkDetailAction) -> Unit
) {
    var selectedTab by remember(uiState.callId, tab) { mutableStateOf(tab ?: uiState.defaultSelectedTab) }

    val scrollState: ScrollState = rememberScrollState()
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)
    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp
    val fullLogGraphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var shareMenuExpanded by remember { mutableStateOf(false) }

    var openFilePicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Hidden off-screen full log view for Save full log as Image
        Box(
            modifier = Modifier
                .layout { measurable, _ ->
                    val widthPx = 700.dp.roundToPx()
                    val placeable = measurable.measure(
                        Constraints(
                            minWidth = widthPx,
                            maxWidth = widthPx,
                            minHeight = 0,
                            maxHeight = Constraints.Infinity
                        )
                    )
                    layout(0, 0) {
                        placeable.place(0, 0)
                    }
                }
                .drawWithContent {
                    fullLogGraphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                }
        ) {
            CompositionLocalProvider(
                LocalDensity provides Density(
                    density = maxOf(2f, LocalDensity.current.density),
                    fontScale = 1f
                )
            ) {
                FullLogExport(
                    state = uiState,
                    linesLabelWidth = linesLabelWidth,
                    headersLabelWidth = headersLabelWidth,
                    modifier = Modifier.width(700.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(
                    vertical = FloconTheme.spacing.small,
                    horizontal = FloconTheme.spacing.extraSmall
                ),
        ) {
            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = FloconTheme.spacing.medium,
                        vertical = FloconTheme.spacing.extraSmall
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(Res.string.network_details_title),
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = FloconTheme.colors.ink
                )

                FloconDropdownMenu(
                    expanded = shareMenuExpanded,
                    onDismissRequest = { shareMenuExpanded = false },
                    onExpandRequest = { shareMenuExpanded = true },
                    anchor = DropdownPanelAnchor.BottomEnd,
                    anchorContent = {
                        FloconIconButton(
                            tooltip = stringResource(Res.string.network_share_export),
                            imageVector = Icons.Outlined.Share,
                            onClick = { shareMenuExpanded = true }
                        )
                    }
                ) {
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.general_copy_as_markdown),
                        leadingIcon = Icons.Outlined.Share,
                        onClick = {
                            shareMenuExpanded = false
                            onAction(NetworkDetailAction.ShareAsMarkdown)
                        }
                    )
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.network_save_html),
                        leadingIcon = Icons.Outlined.Html,
                        onClick = {
                            shareMenuExpanded = false
                            onAction(NetworkDetailAction.SaveAsHtml)
                        }
                    )
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.network_save_full_log_image),
                        leadingIcon = Icons.Outlined.Save,
                        onClick = {
                            shareMenuExpanded = false
                            coroutineScope.launch {
                                try {
                                    val bitmap = fullLogGraphicsLayer.toImageBitmap()
                                    onAction(NetworkDetailAction.SaveImage(bitmap))
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    )
                    FloconDropdownMenuItem(
                        text = stringResource(Res.string.network_copy_curl_command),
                        leadingIcon = Icons.Outlined.Terminal,
                        onClick = {
                            shareMenuExpanded = false
                            onAction(NetworkDetailAction.CopyCurl)
                        }
                    )
                }
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = FloconTheme.spacing.extraSmall)
                    .height(1.dp)
                    .background(FloconTheme.colors.line)
            )

            // Tab bar
            FloconSegmentedRow(
                modifier = Modifier
                    .padding(
                        horizontal = FloconTheme.spacing.medium,
                        vertical = FloconTheme.spacing.extraSmall
                    )
            ) {
                FloconTab(
                    text = "Request (${uiState.requestSize})",
                    isSelected = selectedTab == NetworkDetailTab.Request,
                    tabType = TabType.Start,
                    onSelected = { selectedTab = NetworkDetailTab.Request },
                    modifier = Modifier.weight(1f)
                )
                val responseSizeText = when (val res = uiState.response) {
                    is NetworkDetailViewState.Response.Success -> " (${res.size})"
                    is NetworkDetailViewState.Response.Error -> " (Error)"
                    null -> ""
                }
                FloconTab(
                    text = "Response$responseSizeText",
                    isSelected = selectedTab == NetworkDetailTab.Response,
                    tabType = TabType.End,
                    onSelected = { selectedTab = NetworkDetailTab.Response },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(FloconTheme.spacing.extraSmall))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(durationMillis = 200),
                ) { tab ->
                    when (tab) {
                        NetworkDetailTab.Request -> {
                            Request(
                                modifier = Modifier.fillMaxWidth(),
                                state = uiState,
                                onAction = onAction,
                                linesLabelWidth = linesLabelWidth,
                                headersLabelWidth = headersLabelWidth,
                            )
                        }

                        NetworkDetailTab.Response -> {
                            Response(
                                modifier = Modifier.fillMaxWidth(),
                                state = uiState,
                                onAction = onAction,
                                headersLabelWidth = headersLabelWidth,
                            )
                        }
                    }
                }
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun SummaryCard(
    state: NetworkDetailViewState,
    linesLabelWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colors.bg200, FloconTheme.shapes.medium)
            .border(1.dp, FloconTheme.colors.line, FloconTheme.shapes.medium)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
    ) {
        FloconLineDescription(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = FloconTheme.spacing.doubleExtraLarge),
            label = stringResource(Res.string.network_full_url),
            value = state.fullUrl,
            contentColor = FloconTheme.colors.ink,
            labelWidth = linesLabelWidth,
        )

        FloconLineDescription(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.network_header_method),
            contentColor = FloconTheme.colors.ink,
            labelWidth = linesLabelWidth,
        ) {
            when (val m = state.method) {
                is NetworkDetailViewState.Method.Http -> MethodTag(method = m.method)

                is NetworkDetailViewState.Method.MethodName -> {
                    SelectionContainer {
                        Text(
                            text = m.name,
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colors.inkMuted,
                            modifier = Modifier.weight(2f)
                                .background(
                                    color = FloconTheme.colors.bg100.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(
                                    horizontal = FloconTheme.spacing.small,
                                    vertical = FloconTheme.spacing.compact
                                ),
                        )
                    }
                }
            }
        }
        FloconLineDescription(
            modifier = Modifier.fillMaxWidth(),
            label = state.statusLabel,
            contentColor = FloconTheme.colors.ink,
            labelWidth = linesLabelWidth,
        ) {
            StatusCell(
                status = state.status,
            )
        }
        FloconLineDescription(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.network_header_request_time),
            value = state.requestTimeFormatted,
            labelWidth = linesLabelWidth,
            contentColor = FloconTheme.colors.ink
        )
        state.durationFormatted?.let {
            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.network_header_time),
                value = it,
                labelWidth = linesLabelWidth,
                contentColor = FloconTheme.colors.ink
            )
        }

        FloconLineDescription(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(Res.string.network_request_size),
            value = state.requestSize,
            labelWidth = linesLabelWidth,
            contentColor = FloconTheme.colors.ink
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
    val summaryGraphicsLayer = rememberGraphicsLayer()
    val graphQlGraphicsLayer = rememberGraphicsLayer()
    val reqHeadersGraphicsLayer = rememberGraphicsLayer()
    val reqBodyGraphicsLayer = rememberGraphicsLayer()

    Column(
        modifier = modifier
            .padding(
                horizontal = FloconTheme.spacing.medium,
                vertical = FloconTheme.spacing.extraSmall
            )
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        summaryGraphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(summaryGraphicsLayer)
                    }
            ) {
                SummaryCard(
                    state = state,
                    linesLabelWidth = linesLabelWidth,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            ExportImageDropdown(
                graphicsLayer = summaryGraphicsLayer,
                onAction = onAction,
                tooltip = stringResource(Res.string.network_export_summary_image),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(
                        top = FloconTheme.spacing.extraSmall,
                        end = FloconTheme.spacing.extraSmall
                    )
            )
        }

        state.graphQlSection?.let {
            Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
            FloconSection(
                title = "GraphQl",
                initialValue = true,
                actions = {
                    ExportImageDropdown(
                        graphicsLayer = graphQlGraphicsLayer,
                        onAction = onAction,
                        tooltip = stringResource(Res.string.network_export_graphql_image)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            graphQlGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(graphQlGraphicsLayer)
                        }
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(FloconTheme.spacing.medium)
                ) {
                    FloconLineDescription(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(Res.string.network_query_name),
                        value = it.queryName,
                        contentColor = FloconTheme.colors.inkMuted,
                        labelWidth = linesLabelWidth,
                    )
                    FloconLineDescription(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(Res.string.general_type),
                        labelWidth = linesLabelWidth,
                        contentColor = FloconTheme.colors.inkMuted
                    ) {
                        MethodTag(method = it.method)
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
                contentDescription = stringResource(Res.string.images_preview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = FloconTheme.spacing.medium)
                    .padding(FloconTheme.spacing.extraSmall)
                    .height(300.dp),
                contentScale = ContentScale.Fit,
            )
        }

        state.requestHeaders?.let { requestHeaders ->
            Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
            FloconSection(
                title = stringResource(Res.string.network_headers),
                initialValue = false,
                actions = {
                    ExportImageDropdown(
                        graphicsLayer = reqHeadersGraphicsLayer,
                        onAction = onAction,
                        tooltip = stringResource(Res.string.network_export_headers_image)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailHeaders(
                    headers = requestHeaders,
                    labelWidth = headersLabelWidth,
                    onAuthorizationClicked = { token ->
                        onAction(
                            NetworkDetailAction.DisplayBearerJwt(
                                token
                            )
                        )
                    },
                    onCopyValue = { value -> onAction(NetworkDetailAction.CopyText(value)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            reqHeadersGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(reqHeadersGraphicsLayer)
                        }
                        .padding(FloconTheme.spacing.small)
                )
            }
        }

        Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
        FloconSection(
            title = state.requestBodyTitle,
            initialValue = true,
            actions = {
                ExportImageDropdown(
                    graphicsLayer = reqBodyGraphicsLayer,
                    onAction = onAction,
                    tooltip = stringResource(Res.string.network_export_body_image)
                )
                if (state.requestBodyIsNotBlank) {
                    FloconIconButton(
                        tooltip = stringResource(Res.string.network_view_in_app),
                        imageVector = Icons.Outlined.OpenInFull,
                        onClick = { onAction(NetworkDetailAction.JsonDetail(json = state.requestBody)) }
                    )
                }
                if (state.canOpenRequestBody) {
                    FloconIconButton(
                        tooltip = stringResource(Res.string.open_external),
                        imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                        onClick = { onAction(NetworkDetailAction.OpenBodyExternally.Request(state)) }
                    )
                }
                if (state.requestBodyIsNotBlank) {
                    FloconIconButton(
                        tooltip = stringResource(Res.string.general_copy),
                        imageVector = Icons.Outlined.CopyAll,
                        onClick = { onAction(NetworkDetailAction.CopyText(state.requestBody)) }
                    )
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .drawWithContent {
                        reqBodyGraphicsLayer.record {
                            this@drawWithContent.drawContent()
                        }
                        drawLayer(reqBodyGraphicsLayer)
                    }
            ) {
                var displayBody by remember(state.requestBody) {
                    val isLargeResponse = state.requestBody.length > LARGE_BODY_LENGTH
                    mutableStateOf(!isLargeResponse)
                }
                if (!displayBody) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(FloconTheme.spacing.medium)
                            .background(
                                FloconTheme.colors.bg200,
                                shape = FloconTheme.shapes.medium,
                            )
                            .padding(FloconTheme.spacing.medium),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Response body too large (${state.requestSize} bytes)",
                            color = FloconTheme.colors.ink,
                            style = FloconTheme.typography.bodySmall,
                        )
                        Spacer(modifier = Modifier.height(FloconTheme.spacing.small))
                        FloconButton(
                            onClick = {
                                displayBody = true
                            },
                            containerColor = FloconTheme.colors.ink,
                        ) {
                            Text(stringResource(Res.string.network_display_anyway))
                        }
                    }
                } else {
                    FloconCodeBlock(
                        code = state.requestBody,
                        containerColor = FloconTheme.colors.bg000,
                        contentColor = FloconTheme.colors.inkMuted,
                        borderColor = FloconTheme.colors.line,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(FloconTheme.spacing.medium)
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
    val response = state.response
    if (response == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.extraLarge),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.network_waiting_response),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.ink.copy(alpha = 0.5f)
            )
        }
        return
    }

    val resSummaryGraphicsLayer = rememberGraphicsLayer()
    val resHeadersGraphicsLayer = rememberGraphicsLayer()
    val resBodyGraphicsLayer = rememberGraphicsLayer()
    val resErrorGraphicsLayer = rememberGraphicsLayer()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = FloconTheme.spacing.medium,
                vertical = FloconTheme.spacing.extraSmall
            )
    ) {
        when (response) {
            is NetworkDetailViewState.Response.Error -> {
                FloconSection(
                    title = stringResource(Res.string.network_body),
                    initialValue = true,
                    actions = {
                        ExportImageDropdown(
                            graphicsLayer = resErrorGraphicsLayer,
                            onAction = onAction,
                            tooltip = stringResource(Res.string.network_export_error_image)
                        )
                    }
                ) {
                    FloconCodeBlock(
                        code = response.issue,
                        containerColor = FloconTheme.colors.bg000,
                        contentColor = FloconTheme.colors.inkMuted,
                        borderColor = FloconTheme.colors.line,
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawWithContent {
                                resErrorGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(resErrorGraphicsLayer)
                            }
                            .padding(FloconTheme.spacing.medium)
                    )
                }
            }

            is NetworkDetailViewState.Response.Success -> {
                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = FloconTheme.spacing.small)
                            .drawWithContent {
                                resSummaryGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(resSummaryGraphicsLayer)
                            }
                            .background(
                                color = FloconTheme.colors.bg200,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(
                                horizontal = FloconTheme.spacing.small,
                                vertical = FloconTheme.spacing.small
                            ),
                    ) {
                        FloconLineDescription(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = FloconTheme.spacing.doubleExtraLarge),
                            label = stringResource(Res.string.network_response_size),
                            value = response.size,
                            labelWidth = 130.dp,
                            contentColor = FloconTheme.colors.ink
                        )
                    }

                    ExportImageDropdown(
                        graphicsLayer = resSummaryGraphicsLayer,
                        onAction = onAction,
                        tooltip = stringResource(Res.string.network_export_response_summary_image),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(
                                top = FloconTheme.spacing.extraSmall,
                                end = FloconTheme.spacing.extraSmall
                            )
                    )
                }

                response.headers?.let {
                    Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
                    FloconSection(
                        title = stringResource(Res.string.network_headers),
                        initialValue = false,
                        actions = {
                            ExportImageDropdown(
                                graphicsLayer = resHeadersGraphicsLayer,
                                onAction = onAction,
                                tooltip = stringResource(Res.string.network_export_headers_image)
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DetailHeaders(
                            headers = response.headers,
                            labelWidth = headersLabelWidth,
                            onAuthorizationClicked = { token ->
                                onAction(
                                    NetworkDetailAction.DisplayBearerJwt(
                                        token
                                    )
                                )
                            },
                            onCopyValue = { value -> onAction(NetworkDetailAction.CopyText(value)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawWithContent {
                                    resHeadersGraphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(resHeadersGraphicsLayer)
                                }
                                .padding(FloconTheme.spacing.small)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(FloconTheme.spacing.medium))
                FloconSection(
                    title = stringResource(Res.string.network_body),
                    initialValue = true,
                    actions = {
                        ExportImageDropdown(
                            graphicsLayer = resBodyGraphicsLayer,
                            onAction = onAction,
                            tooltip = stringResource(Res.string.network_export_body_image)
                        )
                        if (response.responseBodyIsNotBlank) {
                            FloconIconButton(
                                tooltip = stringResource(Res.string.network_view_body_in_app),
                                imageVector = Icons.Outlined.OpenInFull,
                                onClick = { onAction(NetworkDetailAction.JsonDetail(response.body)) }
                            )
                        }
                        if (response.canOpenResponseBody) {
                            FloconIconButton(
                                tooltip = stringResource(Res.string.open_external),
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                onClick = {
                                    onAction(
                                        NetworkDetailAction.OpenBodyExternally.Response(
                                            response
                                        )
                                    )
                                }
                            )
                        }
                        if (response.responseBodyIsNotBlank) {
                            FloconIconButton(
                                tooltip = stringResource(Res.string.diff_compare_clipboard),
                                imageVector = Icons.Outlined.Difference,
                                onClick = { onAction(NetworkDetailAction.DiffWithClipboard(response.body)) }
                            )
                        }
                        if (response.responseBodyIsNotBlank) {
                            FloconIconButton(
                                tooltip = stringResource(Res.string.general_copy),
                                imageVector = Icons.Outlined.CopyAll,
                                onClick = { onAction(NetworkDetailAction.CopyText(response.body)) }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawWithContent {
                                resBodyGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(resBodyGraphicsLayer)
                            }
                    ) {
                        var jsonError by remember(response.body) { mutableStateOf(false) }

                        if (!jsonError) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(600.dp)
                                    .padding(FloconTheme.spacing.medium)
                                    .background(
                                        FloconTheme.colors.bg200,
                                        shape = FloconTheme.shapes.medium,
                                    )
                                    .padding(FloconTheme.spacing.medium),
                            ) {
                                FloconJsonTree(
                                    json = response.body,
                                    initialState = TreeState.EXPANDED,
                                    onError = { jsonError = true },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        } else {
                            var displayBody by remember(response.body) {
                                val isLargeResponse = response.body.length > LARGE_BODY_LENGTH
                                mutableStateOf(!isLargeResponse)
                            }
                            if (!displayBody) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(FloconTheme.spacing.medium)
                                        .background(
                                            FloconTheme.colors.bg200,
                                            shape = FloconTheme.shapes.medium,
                                        )
                                        .padding(FloconTheme.spacing.medium),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = "Response body too large (${response.size} bytes)",
                                        color = FloconTheme.colors.ink,
                                        style = FloconTheme.typography.bodySmall,
                                    )
                                    Spacer(modifier = Modifier.height(FloconTheme.spacing.small))
                                    FloconButton(
                                        onClick = {
                                            displayBody = true
                                        },
                                        containerColor = FloconTheme.colors.ink,
                                    ) {
                                        Text(stringResource(Res.string.network_display_anyway))
                                    }
                                }
                            } else {
                                FloconCodeBlock(
                                    code = response.body,
                                    containerColor = FloconTheme.colors.bg000,
                                    contentColor = FloconTheme.colors.inkMuted,
                                    borderColor = FloconTheme.colors.line,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(FloconTheme.spacing.medium)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FullLogExport(
    state: NetworkDetailViewState,
    modifier: Modifier = Modifier,
    linesLabelWidth: Dp = 130.dp,
    headersLabelWidth: Dp = 150.dp,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colors.bg100)
            .padding(FloconTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        // Summary Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FloconTheme.colors.bg200,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(FloconTheme.spacing.medium)
        ) {
            Text(
                text = stringResource(Res.string.network_log_title),
                style = FloconTheme.typography.titleMedium,
                color = FloconTheme.colors.inkMuted,
                modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
            )

            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.network_full_url),
                value = state.fullUrl,
                contentColor = FloconTheme.colors.ink,
                labelWidth = linesLabelWidth,
            )

            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.network_header_method),
                contentColor = FloconTheme.colors.ink,
                labelWidth = linesLabelWidth,
            ) {
                when (val m = state.method) {
                    is NetworkDetailViewState.Method.Http -> MethodTag(method = m.method)

                    is NetworkDetailViewState.Method.MethodName -> {
                        Text(
                            text = m.name,
                            style = FloconTheme.typography.bodySmall,
                            color = FloconTheme.colors.inkMuted,
                            modifier = Modifier
                                .background(
                                    color = FloconTheme.colors.bg100.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(
                                    horizontal = FloconTheme.spacing.small,
                                    vertical = FloconTheme.spacing.compact
                                ),
                        )
                    }
                }
            }

            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = state.statusLabel,
                contentColor = FloconTheme.colors.ink,
                labelWidth = linesLabelWidth,
            ) {
                StatusCell(status = state.status)
            }

            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.network_header_request_time),
                value = state.requestTimeFormatted,
                labelWidth = linesLabelWidth,
                contentColor = FloconTheme.colors.ink
            )

            state.durationFormatted?.let {
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.network_header_time),
                    value = it,
                    labelWidth = linesLabelWidth,
                    contentColor = FloconTheme.colors.ink
                )
            }

            FloconLineDescription(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(Res.string.network_request_size),
                value = state.requestSize,
                labelWidth = linesLabelWidth,
                contentColor = FloconTheme.colors.ink
            )
        }

        // GraphQL Section (if any)
        state.graphQlSection?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FloconTheme.colors.bg200,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(FloconTheme.spacing.medium)
            ) {
                Text(
                    text = "GraphQL",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colors.inkMuted,
                    modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                )
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.network_query_name),
                    value = it.queryName,
                    contentColor = FloconTheme.colors.inkMuted,
                    labelWidth = linesLabelWidth,
                )
                FloconLineDescription(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.general_type),
                    labelWidth = linesLabelWidth,
                    contentColor = FloconTheme.colors.inkMuted
                ) {
                    MethodTag(method = it.method)
                }
            }
        }

        // Request Headers (if any)
        state.requestHeaders?.takeIf { it.isNotEmpty() }?.let { headers ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FloconTheme.colors.bg200,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(FloconTheme.spacing.medium)
            ) {
                Text(
                    text = stringResource(Res.string.network_request_headers),
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colors.inkMuted,
                    modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                )
                DetailHeaders(
                    headers = headers,
                    labelWidth = headersLabelWidth,
                    onAuthorizationClicked = {},
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Request Body (if any)
        if (state.requestBodyIsNotBlank) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FloconTheme.colors.bg200,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(FloconTheme.spacing.medium)
            ) {
                Text(
                    text = state.requestBodyTitle,
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colors.inkMuted,
                    modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                )
                FloconCodeBlock(
                    code = formatBodyForExport(state.requestBody),
                    containerColor = FloconTheme.colors.bg100,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Response Section
        when (val res = state.response) {
            is NetworkDetailViewState.Response.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(FloconTheme.spacing.medium)
                ) {
                    Text(
                        text = "Response (${res.size})",
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colors.inkMuted,
                        modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                    )
                    FloconLineDescription(
                        modifier = Modifier.fillMaxWidth(),
                        label = stringResource(Res.string.network_response_size),
                        value = res.size,
                        labelWidth = linesLabelWidth,
                        contentColor = FloconTheme.colors.ink
                    )
                }

                res.headers?.takeIf { it.isNotEmpty() }?.let { headers ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = FloconTheme.colors.bg200,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(FloconTheme.spacing.medium)
                    ) {
                        Text(
                            text = stringResource(Res.string.network_response_headers),
                            style = FloconTheme.typography.titleSmall,
                            color = FloconTheme.colors.inkMuted,
                            modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                        )
                        DetailHeaders(
                            headers = headers,
                            labelWidth = headersLabelWidth,
                            onAuthorizationClicked = {},
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (res.responseBodyIsNotBlank) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = FloconTheme.colors.bg200,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(FloconTheme.spacing.medium)
                    ) {
                        Text(
                            text = stringResource(Res.string.network_response_body),
                            style = FloconTheme.typography.titleSmall,
                            color = FloconTheme.colors.inkMuted,
                            modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                        )
                        FloconCodeBlock(
                            code = formatBodyForExport(res.body),
                            containerColor = FloconTheme.colors.bg100,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            is NetworkDetailViewState.Response.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(FloconTheme.spacing.medium)
                ) {
                    Text(
                        text = stringResource(Res.string.network_response_error),
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colors.statusError,
                        modifier = Modifier.padding(bottom = FloconTheme.spacing.small)
                    )
                    FloconCodeBlock(
                        code = formatBodyForExport(res.issue),
                        containerColor = FloconTheme.colors.bg100,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            null -> Unit
        }
    }
}

private fun formatBodyForExport(body: String, maxLines: Int = 30): String {
    val lines = body.lines()
    return if (lines.size > maxLines) {
        lines.take(maxLines)
            .joinToString("\n") + "\n\n... (truncated, ${lines.size - maxLines} more lines)"
    } else {
        body
    }
}

@androidx.compose.ui.tooling.preview.Preview
@Composable
private fun NetworkDetailPreview() {
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
                persistentListOf(
                    previewNetworkDetailHeaderUi(),
                    previewNetworkDetailHeaderUi(),
                    previewNetworkDetailHeaderUi(),
                ),
                requestBodyTitle = "Body",
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
                    persistentListOf(
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
                defaultSelectedTab = NetworkDetailTab.Request,
            ),
            tab = null,
            onAction = {}
        )
    }
}
