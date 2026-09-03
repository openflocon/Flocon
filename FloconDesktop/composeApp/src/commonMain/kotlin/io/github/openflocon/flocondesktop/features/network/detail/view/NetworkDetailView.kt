package io.github.openflocon.flocondesktop.features.network.detail.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Difference
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
import com.sebastianneubauer.jsontree.TreeState
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailAction
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailViewModel
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailViewState
import io.github.openflocon.flocondesktop.features.network.detail.model.previewNetworkDetailHeaderUi
import io.github.openflocon.flocondesktop.features.network.detail.view.components.DetailHeadersView
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkMethodUi
import io.github.openflocon.flocondesktop.features.network.list.model.NetworkStatusUi
import io.github.openflocon.flocondesktop.features.network.list.view.components.MethodView
import io.github.openflocon.flocondesktop.features.network.list.view.components.StatusView
import com.composeunstyled.DropdownPanelAnchor
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton

import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.TabType
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val LARGE_BODY_LENGTH = 1_000_000

@Composable
fun NetworkDetailScreen(
    requestId: String,
    key: String = requestId,
) {
    val viewModel = koinViewModel<NetworkDetailViewModel>(key = key) {
        parametersOf(requestId)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkDetailContent(
        uiState = uiState,
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
                text = "Copy as image",
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
                text = "Save as image",
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
fun NetworkDetailContent(
    uiState: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember(uiState.callId) { mutableStateOf(uiState.defaultSelectedTab) }

    val scrollState: ScrollState = rememberScrollState()
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)
    val linesLabelWidth: Dp = 130.dp
    val headersLabelWidth: Dp = 150.dp
    val fullLogGraphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var shareMenuExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .background(FloconTheme.colorPalette.primary)
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
                FullLogExportView(
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
                .padding(vertical = 8.dp, horizontal = 4.dp),
        ) {
            // Top action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Network Details",
                    style = FloconTheme.typography.titleMedium,
                    color = FloconTheme.colorPalette.onPrimary
                )

                FloconDropdownMenu(
                    expanded = shareMenuExpanded,
                    onDismissRequest = { shareMenuExpanded = false },
                    onExpandRequest = { shareMenuExpanded = true },
                    anchor = DropdownPanelAnchor.BottomEnd,
                    anchorContent = {
                        FloconIconButton(
                            tooltip = "Share & Export",
                            imageVector = Icons.Outlined.Share,
                            onClick = { shareMenuExpanded = true }
                        )
                    }
                ) {
                    FloconDropdownMenuItem(
                        text = "Copy as Markdown",
                        leadingIcon = Icons.Outlined.Share,
                        onClick = {
                            shareMenuExpanded = false
                            onAction(NetworkDetailAction.ShareAsMarkdown)
                        }
                    )
                    FloconDropdownMenuItem(
                        text = "Save full log as Image",
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
                        text = "Copy cURL command",
                        leadingIcon = Icons.Outlined.Terminal,
                        onClick = {
                            shareMenuExpanded = false
                            onAction(NetworkDetailAction.CopyCurl)
                        }
                    )
                }
            }


            // Tab bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
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

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                when (selectedTab) {
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
            .background(
                color = FloconTheme.colorPalette.secondary,
                shape = RoundedCornerShape(12.dp),
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
    ) {
        FloconLineDescription(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 36.dp),
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
            .padding(horizontal = 12.dp, vertical = 4.dp)
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
                tooltip = "Export summary as image",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 4.dp, end = 4.dp)
            )
        }

        state.graphQlSection?.let {
            Spacer(modifier = Modifier.height(12.dp))
            FloconSection(
                title = "GraphQl",
                initialValue = true,
                actions = {
                    ExportImageDropdown(
                        graphicsLayer = graphQlGraphicsLayer,
                        onAction = onAction,
                        tooltip = "Export GraphQl as image"
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
            Spacer(modifier = Modifier.height(12.dp))
            FloconSection(
                title = "Headers",
                initialValue = false,
                actions = {
                    ExportImageDropdown(
                        graphicsLayer = reqHeadersGraphicsLayer,
                        onAction = onAction,
                        tooltip = "Export headers as image"
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                DetailHeadersView(
                    headers = state.requestHeaders,
                    labelWidth = headersLabelWidth,
                    onAuthorizationClicked = { token -> onAction(NetworkDetailAction.DisplayBearerJwt(token)) },
                    onCopyValue = { value -> onAction(NetworkDetailAction.CopyText(value)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .drawWithContent {
                            reqHeadersGraphicsLayer.record {
                                this@drawWithContent.drawContent()
                            }
                            drawLayer(reqHeadersGraphicsLayer)
                        }
                        .padding(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        FloconSection(
            title = state.requestBodyTitle,
            initialValue = true,
            actions = {
                ExportImageDropdown(
                    graphicsLayer = reqBodyGraphicsLayer,
                    onAction = onAction,
                    tooltip = "Export body as image"
                )
                if (state.requestBodyIsNotBlank) {
                    FloconIconButton(
                        tooltip = "View in app",
                        imageVector = Icons.Outlined.OpenInFull,
                        onClick = { onAction(NetworkDetailAction.JsonDetail(json = state.requestBody)) }
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
    val response = state.response
    if (response == null) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Waiting for response...",
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.5f)
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
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        when (response) {
            is NetworkDetailViewState.Response.Error -> {
                FloconSection(
                    title = "Body",
                    initialValue = true,
                    actions = {
                        ExportImageDropdown(
                            graphicsLayer = resErrorGraphicsLayer,
                            onAction = onAction,
                            tooltip = "Export error as image"
                        )
                    }
                ) {
                    FloconCodeBlock(
                        code = response.issue,
                        containerColor = FloconTheme.colorPalette.secondary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .drawWithContent {
                                resErrorGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(resErrorGraphicsLayer)
                            }
                            .padding(12.dp)
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
                            .padding(bottom = 8.dp)
                            .drawWithContent {
                                resSummaryGraphicsLayer.record {
                                    this@drawWithContent.drawContent()
                                }
                                drawLayer(resSummaryGraphicsLayer)
                            }
                            .background(
                                color = FloconTheme.colorPalette.secondary,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        FloconLineDescription(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 36.dp),
                            label = "Response Size",
                            value = response.size,
                            labelWidth = 130.dp,
                            contentColor = FloconTheme.colorPalette.onPrimary
                        )
                    }

                    ExportImageDropdown(
                        graphicsLayer = resSummaryGraphicsLayer,
                        onAction = onAction,
                        tooltip = "Export response summary as image",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                    )
                }

                response.headers?.let {
                    Spacer(modifier = Modifier.height(12.dp))
                    FloconSection(
                        title = "Headers",
                        initialValue = false,
                        actions = {
                            ExportImageDropdown(
                                graphicsLayer = resHeadersGraphicsLayer,
                                onAction = onAction,
                                tooltip = "Export headers as image"
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        DetailHeadersView(
                            headers = response.headers,
                            labelWidth = headersLabelWidth,
                            onAuthorizationClicked = { token -> onAction(NetworkDetailAction.DisplayBearerJwt(token)) },
                            onCopyValue = { value -> onAction(NetworkDetailAction.CopyText(value)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .drawWithContent {
                                    resHeadersGraphicsLayer.record {
                                        this@drawWithContent.drawContent()
                                    }
                                    drawLayer(resHeadersGraphicsLayer)
                                }
                                .padding(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                FloconSection(
                    title = "Body",
                    initialValue = true,
                    actions = {
                        ExportImageDropdown(
                            graphicsLayer = resBodyGraphicsLayer,
                            onAction = onAction,
                            tooltip = "Export body as image"
                        )
                        if (response.responseBodyIsNotBlank) {
                            FloconIconButton(
                                tooltip = "View body in app",
                                imageVector = Icons.Outlined.OpenInFull,
                                onClick = { onAction(NetworkDetailAction.JsonDetail(response.body)) }
                            )
                        }
                        if (response.canOpenResponseBody) {
                            FloconIconButton(
                                tooltip = "Open in external editor",
                                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                                onClick = { onAction(NetworkDetailAction.OpenBodyExternally.Response(response)) }
                            )
                        }
                        if (response.responseBodyIsNotBlank) {
                            FloconIconButton(
                                tooltip = "Compare with clipboard",
                                imageVector = Icons.Outlined.Difference,
                                onClick = { onAction(NetworkDetailAction.DiffWithClipboard(response.body)) }
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
                                    .padding(12.dp)
                                    .background(
                                        FloconTheme.colorPalette.secondary,
                                        shape = FloconTheme.shapes.medium,
                                    )
                                    .padding(12.dp),
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
}

@Composable
private fun FullLogExportView(
    state: NetworkDetailViewState,
    linesLabelWidth: Dp = 130.dp,
    headersLabelWidth: Dp = 150.dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(FloconTheme.colorPalette.primary)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Summary Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = FloconTheme.colorPalette.secondary,
                    shape = RoundedCornerShape(12.dp),
                )
                .padding(12.dp)
        ) {
            Text(
                text = "Network Log",
                style = FloconTheme.typography.titleMedium,
                color = FloconTheme.colorPalette.onSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

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
                            modifier = Modifier
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
                StatusView(status = state.status)
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

        // GraphQL Section (if any)
        state.graphQlSection?.let {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FloconTheme.colorPalette.secondary,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "GraphQL",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
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

        // Request Headers (if any)
        state.requestHeaders?.takeIf { it.isNotEmpty() }?.let { headers ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = FloconTheme.colorPalette.secondary,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = "Request Headers",
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                DetailHeadersView(
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
                        color = FloconTheme.colorPalette.secondary,
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(12.dp)
            ) {
                Text(
                    text = state.requestBodyTitle,
                    style = FloconTheme.typography.titleSmall,
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                FloconCodeBlock(
                    code = formatBodyForExport(state.requestBody),
                    containerColor = FloconTheme.colorPalette.primary,
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
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Response (${res.size})",
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.onSecondary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FloconLineDescription(
                        modifier = Modifier.fillMaxWidth(),
                        label = "Response Size",
                        value = res.size,
                        labelWidth = linesLabelWidth,
                        contentColor = FloconTheme.colorPalette.onPrimary
                    )
                }

                res.headers?.takeIf { it.isNotEmpty() }?.let { headers ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp)
                    ) {
                        Text(
                            text = "Response Headers",
                            style = FloconTheme.typography.titleSmall,
                            color = FloconTheme.colorPalette.onSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        DetailHeadersView(
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
                                color = FloconTheme.colorPalette.secondary,
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "Response Body",
                            style = FloconTheme.typography.titleSmall,
                            color = FloconTheme.colorPalette.onSecondary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        FloconCodeBlock(
                            code = formatBodyForExport(res.body),
                            containerColor = FloconTheme.colorPalette.primary,
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
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(12.dp)
                ) {
                    Text(
                        text = "Response Error",
                        style = FloconTheme.typography.titleSmall,
                        color = FloconTheme.colorPalette.error,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    FloconCodeBlock(
                        code = formatBodyForExport(res.issue),
                        containerColor = FloconTheme.colorPalette.primary,
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
        lines.take(maxLines).joinToString("\n") + "\n\n... (truncated, ${lines.size - maxLines} more lines)"
    } else {
        body
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
                defaultSelectedTab = NetworkDetailTab.Request,
            ),
            onAction = {}
        )
    }
}
