package io.github.openflocon.flocondesktop.features.network.detail.view

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import flocondesktop.composeapp.generated.resources.network_action_decode
import flocondesktop.composeapp.generated.resources.network_action_diff
import flocondesktop.composeapp.generated.resources.network_action_expand
import flocondesktop.composeapp.generated.resources.network_action_image
import flocondesktop.composeapp.generated.resources.network_action_open
import flocondesktop.composeapp.generated.resources.network_body
import flocondesktop.composeapp.generated.resources.network_copy_curl_command
import flocondesktop.composeapp.generated.resources.network_copy_url
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
import flocondesktop.composeapp.generated.resources.network_image_response
import flocondesktop.composeapp.generated.resources.network_jwt
import flocondesktop.composeapp.generated.resources.network_log_title
import flocondesktop.composeapp.generated.resources.network_no_body
import flocondesktop.composeapp.generated.resources.network_open_jwt_decoder
import flocondesktop.composeapp.generated.resources.network_query_name
import flocondesktop.composeapp.generated.resources.network_query_params
import flocondesktop.composeapp.generated.resources.network_request_headers
import flocondesktop.composeapp.generated.resources.network_request_info
import flocondesktop.composeapp.generated.resources.network_request_size
import flocondesktop.composeapp.generated.resources.network_response_body
import flocondesktop.composeapp.generated.resources.network_response_error
import flocondesktop.composeapp.generated.resources.network_response_headers
import flocondesktop.composeapp.generated.resources.network_response_info
import flocondesktop.composeapp.generated.resources.network_response_size
import flocondesktop.composeapp.generated.resources.network_save_full_log_image
import flocondesktop.composeapp.generated.resources.network_save_html
import flocondesktop.composeapp.generated.resources.network_share_export
import flocondesktop.composeapp.generated.resources.network_tab_error
import flocondesktop.composeapp.generated.resources.network_view_body_in_app
import flocondesktop.composeapp.generated.resources.network_view_in_app
import flocondesktop.composeapp.generated.resources.network_waiting_response
import flocondesktop.composeapp.generated.resources.open_external
import io.github.openflocon.domain.models.settings.NetworkDetailTab
import io.github.openflocon.flocondesktop.common.ui.detail.DetailActionButton
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailEmptyText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.common.ui.detail.toCopyableText
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailAction
import io.github.openflocon.flocondesktop.features.network.detail.NetworkDetailViewModel
import io.github.openflocon.flocondesktop.features.network.detail.model.NetworkDetailHeaderUi
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
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconLineDescription
import io.github.openflocon.library.designsystem.components.FloconSection
import io.github.openflocon.library.designsystem.components.FloconSegmentedRow
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconTab
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.TabType
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

private const val LARGE_BODY_LENGTH = 1_000_000
private val KeyWidth = 140.dp
private val HeadersLabelWidth = 150.dp
private val JsonTreeHeight = 600.dp
private val ImageHeight = 300.dp
private val TabShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)

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

private fun Modifier.recordInto(graphicsLayer: GraphicsLayer): Modifier = drawWithContent {
    graphicsLayer.record {
        this@drawWithContent.drawContent()
    }
    drawLayer(graphicsLayer)
}

@Composable
private fun ExportImageDropdown(
    graphicsLayer: GraphicsLayer,
    onAction: (NetworkDetailAction) -> Unit,
    tooltip: String,
    modifier: Modifier = Modifier,
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
                DetailActionButton(
                    label = stringResource(Res.string.network_action_image),
                    imageVector = Icons.Outlined.CameraAlt,
                    onClick = { expanded = true },
                    contentDescription = tooltip,
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
                            onAction(NetworkDetailAction.CopyImage(graphicsLayer.toImageBitmap()))
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
                            onAction(NetworkDetailAction.SaveImage(graphicsLayer.toImageBitmap()))
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

    val fullLogGraphicsLayer = rememberGraphicsLayer()
    val coroutineScope = rememberCoroutineScope()
    var shareMenuExpanded by remember { mutableStateOf(false) }

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
                    linesLabelWidth = 130.dp,
                    headersLabelWidth = 150.dp,
                    modifier = Modifier.width(700.dp)
                )
            }
        }

        val listState = rememberLazyListState()
        val scrollAdapter = rememberFloconScrollbarAdapter(listState)
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .panelBackground(),
        ) {
            networkDetailSections(
                header = {
                    DetailHeader(
                        label = uiState.host.ifEmpty { stringResource(Res.string.network_details_title) },
                        title = uiState.path.ifEmpty { uiState.fullUrl },
                        chips = {
                            when (val method = uiState.method) {
                                is NetworkDetailViewState.Method.Http -> MethodTag(method = method.method)
                                is NetworkDetailViewState.Method.MethodName -> DetailChip(text = method.name)
                            }
                            StatusCell(status = uiState.status)
                            uiState.durationFormatted?.let { DetailChip(text = it) }
                            DetailChip(text = uiState.requestTimeFormatted)
                        },
                        actions = {
                            ShareMenu(
                                expanded = shareMenuExpanded,
                                onExpandedChange = { shareMenuExpanded = it },
                                onAction = onAction,
                                onSaveFullLogImage = {
                                    coroutineScope.launch {
                                        try {
                                            val bitmap = fullLogGraphicsLayer.toImageBitmap()
                                            onAction(NetworkDetailAction.SaveImage(bitmap))
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }
                                },
                            )
                        },
                    )
                },
                tabs = {
                    NetworkDetailTabItem(
                        label = stringResource(Res.string.network_request_info),
                        detail = uiState.requestSize,
                        icon = Icons.Outlined.ArrowUpward,
                        iconTint = FloconTheme.colors.statusInfo,
                        isSelected = selectedTab == NetworkDetailTab.Request,
                        onSelected = { selectedTab = NetworkDetailTab.Request },
                    )
                    NetworkDetailTabItem(
                        label = stringResource(Res.string.network_response_info),
                        detail = when (val res = uiState.response) {
                            is NetworkDetailViewState.Response.Success -> res.size
                            is NetworkDetailViewState.Response.Error -> stringResource(Res.string.network_tab_error)
                            null -> null
                        },
                        icon = Icons.Outlined.ArrowDownward,
                        iconTint = FloconTheme.colors.statusSuccess,
                        isSelected = selectedTab == NetworkDetailTab.Response,
                        onSelected = { selectedTab = NetworkDetailTab.Response },
                    )
                },
            ) {
                Crossfade(
                    targetState = selectedTab,
                    animationSpec = tween(durationMillis = 200),
                ) { currentTab ->
                    when (currentTab) {
                        NetworkDetailTab.Request -> RequestTab(
                            state = uiState,
                            onAction = onAction,
                        )

                        NetworkDetailTab.Response -> ResponseTab(
                            state = uiState,
                            onAction = onAction,
                        )
                    }
                }
            }
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.TopEnd),
        )
    }
}

/**
 * The theme surfaces can be translucent: lay them over the page and the panel surface so the content
 * scrolling under the sticky tabs doesn't show through. The panel is the lighter one
 */
@Composable
private fun Modifier.panelBackground(): Modifier {
    val colors = FloconTheme.colors
    return background(colors.pageMid)
        .background(colors.islandSidebar)
        .background(colors.bg200)
}

/** The dark block of the title and the tabs */
@Composable
private fun Modifier.headerBackground(): Modifier {
    val colors = FloconTheme.colors
    return background(colors.pageMid)
        .background(colors.islandSidebar)
        .background(colors.bg000)
}

/**
 * The header and the tabs sit on a dark block, the selected tab melts into the lighter panel holding the content.
 * The tabs stay on top while the content scrolls.
 * It has no border nor corners: the side panel / window already is the container
 */
@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.networkDetailSections(
    header: @Composable () -> Unit,
    tabs: @Composable RowScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(key = "header") {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .headerBackground()
                .padding(
                    start = FloconTheme.spacing.large,
                    end = FloconTheme.spacing.large,
                    top = FloconTheme.spacing.large,
                    bottom = FloconTheme.spacing.medium,
                ),
        ) {
            header()
        }
    }
    stickyHeader(key = "tabs") {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .headerBackground()
                .padding(horizontal = FloconTheme.spacing.small),
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
            verticalAlignment = Alignment.Bottom,
            content = tabs,
        )
    }
    item(key = "content") {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            content = content,
        )
    }
}

@Composable
private fun NetworkDetailTabItem(
    label: String,
    detail: String?,
    icon: ImageVector,
    iconTint: Color,
    isSelected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(TabShape)
            .then(if (isSelected) Modifier.panelBackground() else Modifier)
            .clickable(onClick = onSelected)
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.compact),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FloconIcon(
            imageVector = icon,
            tint = iconTint,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = label,
            style = FloconTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (isSelected) FloconTheme.colors.ink else FloconTheme.colors.inkMuted,
        )
        detail?.let {
            Text(
                text = it,
                style = FloconTheme.typography.monoSmall,
                color = FloconTheme.colors.inkMuted,
            )
        }
    }
}

@Composable
private fun ShareMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onAction: (NetworkDetailAction) -> Unit,
    onSaveFullLogImage: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { onExpandedChange(false) },
        onExpandRequest = { onExpandedChange(true) },
        anchor = DropdownPanelAnchor.BottomEnd,
        modifier = modifier,
        anchorContent = {
            FloconIconButton(
                tooltip = stringResource(Res.string.network_share_export),
                imageVector = Icons.Outlined.Share,
                onClick = { onExpandedChange(true) }
            )
        }
    ) {
        FloconDropdownMenuItem(
            text = stringResource(Res.string.general_copy_as_markdown),
            leadingIcon = Icons.Outlined.Share,
            onClick = {
                onExpandedChange(false)
                onAction(NetworkDetailAction.ShareAsMarkdown)
            }
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_save_html),
            leadingIcon = Icons.Outlined.Html,
            onClick = {
                onExpandedChange(false)
                onAction(NetworkDetailAction.SaveAsHtml)
            }
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_save_full_log_image),
            leadingIcon = Icons.Outlined.Save,
            onClick = {
                onExpandedChange(false)
                onSaveFullLogImage()
            }
        )
        FloconDropdownMenuItem(
            text = stringResource(Res.string.network_copy_curl_command),
            leadingIcon = Icons.Outlined.Terminal,
            onClick = {
                onExpandedChange(false)
                onAction(NetworkDetailAction.CopyCurl)
            }
        )
    }
}

@Composable
private fun RequestTab(
    state: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fullUrlLabel = stringResource(Res.string.network_full_url)
    val timeLabel = stringResource(Res.string.network_header_request_time)
    val durationLabel = stringResource(Res.string.network_header_time)
    val sizeLabel = stringResource(Res.string.network_request_size)
    val infoEntries = remember(state, fullUrlLabel, timeLabel, durationLabel, sizeLabel) {
        buildList {
            add(fullUrlLabel to state.fullUrl)
            add(timeLabel to state.requestTimeFormatted)
            state.durationFormatted?.let { add(durationLabel to it) }
            add(sizeLabel to state.requestSize)
        }
    }

    val infoGraphicsLayer = rememberGraphicsLayer()
    val graphQlGraphicsLayer = rememberGraphicsLayer()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.network_request_info),
            badge = null,
            copyContentDescription = stringResource(Res.string.network_copy_url),
            onCopy = null,
            collapsible = true,
            toolbar = {
                ExportImageDropdown(
                    graphicsLayer = infoGraphicsLayer,
                    onAction = onAction,
                    tooltip = stringResource(Res.string.network_export_summary_image),
                )
                DetailActionButton(
                    label = stringResource(Res.string.general_copy),
                    imageVector = Icons.Outlined.CopyAll,
                    onClick = { onAction(NetworkDetailAction.CopyText(state.fullUrl)) },
                )
            },
        ) {
            DetailKeyValueList(
                entries = infoEntries,
                modifier = Modifier.recordInto(infoGraphicsLayer),
            )
        }

        state.jwt?.let { jwt ->
            JwtCard(
                jwt = jwt,
                onAction = onAction,
            )
        }

        state.graphQlSection?.let { graphQl ->
            DetailSectionCard(
                title = "GraphQl",
                badge = null,
                copyContentDescription = stringResource(Res.string.general_copy),
                onCopy = null,
                collapsible = true,
                toolbar = {
                    ExportImageDropdown(
                        graphicsLayer = graphQlGraphicsLayer,
                        onAction = onAction,
                        tooltip = stringResource(Res.string.network_export_graphql_image),
                    )
                    DetailActionButton(
                        label = stringResource(Res.string.general_copy),
                        imageVector = Icons.Outlined.CopyAll,
                        onClick = { onAction(NetworkDetailAction.CopyText(graphQl.queryName)) },
                    )
                },
            ) {
                Column(modifier = Modifier.recordInto(graphQlGraphicsLayer)) {
                    DetailKeyValueList(
                        entries = listOf(stringResource(Res.string.network_query_name) to graphQl.queryName),
                    )
                    FloconLineDescription(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                        label = stringResource(Res.string.general_type),
                        labelWidth = KeyWidth,
                        contentColor = FloconTheme.colors.inkMuted
                    ) {
                        MethodTag(method = graphQl.method)
                    }
                }
            }
        }

        state.queryParams?.let { queryParams ->
            val entries = remember(queryParams) { queryParams.map { it.name to it.value } }
            DetailSectionCard(
                title = stringResource(Res.string.network_query_params),
                badge = entries.size.toString(),
                copyContentDescription = stringResource(Res.string.general_copy),
                onCopy = null,
                collapsible = true,
                toolbar = {
                    DetailActionButton(
                        label = stringResource(Res.string.general_copy),
                        imageVector = Icons.Outlined.CopyAll,
                        onClick = { onAction(NetworkDetailAction.CopyText(entries.toCopyableText())) },
                    )
                },
            ) {
                DetailKeyValueList(entries = entries)
            }
        }

        state.requestHeaders?.let { headers ->
            HeadersCard(
                headers = headers,
                onAction = onAction,
            )
        }

        BodyCard(
            title = state.requestBodyTitle,
            body = state.requestBody,
            bodyIsNotBlank = state.requestBodyIsNotBlank,
            canOpenExternally = state.canOpenRequestBody,
            size = state.requestSize,
            onOpenExternally = { onAction(NetworkDetailAction.OpenBodyExternally.Request(state)) },
            onAction = onAction,
        )
    }
}

@Composable
private fun ResponseTab(
    state: NetworkDetailViewState,
    onAction: (NetworkDetailAction) -> Unit,
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

    val errorGraphicsLayer = rememberGraphicsLayer()
    val responseInfoGraphicsLayer = rememberGraphicsLayer()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.large),
    ) {
        when (response) {
            is NetworkDetailViewState.Response.Error -> {
                DetailSectionCard(
                    title = stringResource(Res.string.network_body),
                    badge = null,
                    copyContentDescription = stringResource(Res.string.general_copy),
                    onCopy = null,
                    collapsible = true,
                    toolbar = {
                        ExportImageDropdown(
                            graphicsLayer = errorGraphicsLayer,
                            onAction = onAction,
                            tooltip = stringResource(Res.string.network_export_error_image),
                        )
                        DetailActionButton(
                            label = stringResource(Res.string.general_copy),
                            imageVector = Icons.Outlined.CopyAll,
                            onClick = { onAction(NetworkDetailAction.CopyText(response.issue)) },
                        )
                    },
                ) {
                    FloconCodeBlock(
                        code = response.issue,
                        containerColor = FloconTheme.colors.bg000,
                        contentColor = FloconTheme.colors.inkMuted,
                        borderColor = FloconTheme.colors.line,
                        modifier = Modifier
                            .fillMaxWidth()
                            .recordInto(errorGraphicsLayer)
                            .padding(FloconTheme.spacing.medium)
                    )
                }
            }

            is NetworkDetailViewState.Response.Success -> {
                DetailSectionCard(
                    title = stringResource(Res.string.network_response_info),
                    badge = null,
                    copyContentDescription = stringResource(Res.string.general_copy),
                    onCopy = null,
                    collapsible = true,
                    toolbar = {
                        ExportImageDropdown(
                            graphicsLayer = responseInfoGraphicsLayer,
                            onAction = onAction,
                            tooltip = stringResource(Res.string.network_export_response_summary_image),
                        )
                    },
                ) {
                    DetailKeyValueList(
                        entries = listOf(stringResource(Res.string.network_response_size) to response.size),
                        modifier = Modifier.recordInto(responseInfoGraphicsLayer),
                    )
                }

                if (state.imageUrl != null) {
                    ImageCard(
                        state = state,
                        imageUrl = state.imageUrl,
                        onAction = onAction,
                    )
                }
                // an image url can answer with a text body (an error payload for example), keep it visible
                if (state.imageUrl == null || response.responseBodyIsNotBlank) {
                    BodyCard(
                        title = stringResource(Res.string.network_body),
                        body = response.body,
                        bodyIsNotBlank = response.responseBodyIsNotBlank,
                        canOpenExternally = response.canOpenResponseBody,
                        size = response.size,
                        onOpenExternally = { onAction(NetworkDetailAction.OpenBodyExternally.Response(response)) },
                        onAction = onAction,
                        withDiff = true,
                    )
                }

                response.headers?.let { headers ->
                    HeadersCard(
                        headers = headers,
                        onAction = onAction,
                    )
                }
            }
        }
    }
}

@Composable
private fun JwtCard(
    jwt: NetworkDetailViewState.Jwt,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val entries = remember(jwt) { jwt.claims.map { it.name to it.value } }
    DetailSectionCard(
        title = stringResource(Res.string.network_jwt),
        badge = "BEARER",
        copyContentDescription = stringResource(Res.string.general_copy),
        onCopy = null,
        modifier = modifier,
        collapsible = true,
        toolbar = {
            DetailActionButton(
                label = stringResource(Res.string.network_action_decode),
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                onClick = { onAction(NetworkDetailAction.DisplayBearerJwt(jwt.token)) },
                contentDescription = stringResource(Res.string.network_open_jwt_decoder),
            )
            DetailActionButton(
                label = stringResource(Res.string.general_copy),
                imageVector = Icons.Outlined.CopyAll,
                onClick = { onAction(NetworkDetailAction.CopyText(jwt.token)) },
            )
        },
    ) {
        jwt.expiration?.let { expiration ->
            DetailChip(
                text = expiration,
                modifier = Modifier.padding(FloconTheme.spacing.medium),
            )
        }
        SelectionContainer {
            Text(
                text = jwt.token,
                style = FloconTheme.typography.monoSmall,
                color = FloconTheme.colors.inkMuted,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FloconTheme.spacing.medium)
                    .padding(bottom = FloconTheme.spacing.small),
            )
        }
        DetailKeyValueList(entries = entries)
    }
}

@Composable
private fun HeadersCard(
    headers: ImmutableList<NetworkDetailHeaderUi>,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val copyableText = remember(headers) { headers.map { it.name to it.value }.toCopyableText() }
    val graphicsLayer = rememberGraphicsLayer()
    DetailSectionCard(
        title = stringResource(Res.string.network_headers),
        badge = headers.size.toString(),
        copyContentDescription = stringResource(Res.string.general_copy),
        onCopy = null,
        modifier = modifier,
        collapsible = true,
        initiallyExpanded = false,
        toolbar = {
            ExportImageDropdown(
                graphicsLayer = graphicsLayer,
                onAction = onAction,
                tooltip = stringResource(Res.string.network_export_headers_image),
            )
            DetailActionButton(
                label = stringResource(Res.string.general_copy),
                imageVector = Icons.Outlined.CopyAll,
                onClick = { onAction(NetworkDetailAction.CopyText(copyableText)) },
            )
        },
    ) {
        DetailHeaders(
            headers = headers,
            labelWidth = HeadersLabelWidth,
            onAuthorizationClicked = { token -> onAction(NetworkDetailAction.DisplayBearerJwt(token)) },
            onCopyValue = { value -> onAction(NetworkDetailAction.CopyText(value)) },
            framed = false,
            modifier = Modifier
                .fillMaxWidth()
                .recordInto(graphicsLayer),
        )
    }
}

@Composable
private fun BodyCard(
    title: String,
    body: String,
    bodyIsNotBlank: Boolean,
    canOpenExternally: Boolean,
    size: String,
    onOpenExternally: () -> Unit,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
    withDiff: Boolean = false,
) {
    val graphicsLayer = rememberGraphicsLayer()
    val bodyToolbar: @Composable FlowRowScope.() -> Unit = {
        if (bodyIsNotBlank) {
            DetailActionButton(
                label = stringResource(Res.string.network_action_expand),
                imageVector = Icons.Outlined.OpenInFull,
                onClick = { onAction(NetworkDetailAction.JsonDetail(json = body)) },
                contentDescription = stringResource(Res.string.network_view_in_app),
            )
        }
        if (canOpenExternally) {
            DetailActionButton(
                label = stringResource(Res.string.network_action_open),
                imageVector = Icons.AutoMirrored.Outlined.OpenInNew,
                onClick = onOpenExternally,
                contentDescription = stringResource(Res.string.open_external),
            )
        }
        if (withDiff && bodyIsNotBlank) {
            DetailActionButton(
                label = stringResource(Res.string.network_action_diff),
                imageVector = Icons.Outlined.Difference,
                onClick = { onAction(NetworkDetailAction.DiffWithClipboard(body)) },
                contentDescription = stringResource(Res.string.diff_compare_clipboard),
            )
        }
        ExportImageDropdown(
            graphicsLayer = graphicsLayer,
            onAction = onAction,
            tooltip = stringResource(Res.string.network_export_body_image),
        )
        if (bodyIsNotBlank) {
            DetailActionButton(
                label = stringResource(Res.string.general_copy),
                imageVector = Icons.Outlined.CopyAll,
                onClick = { onAction(NetworkDetailAction.CopyText(body)) },
            )
        }
    }
    DetailSectionCard(
        title = title,
        badge = null,
        copyContentDescription = stringResource(Res.string.general_copy),
        onCopy = null,
        modifier = modifier,
        collapsible = true,
        toolbar = bodyToolbar.takeIf { bodyIsNotBlank },
    ) {
        if (!bodyIsNotBlank) {
            DetailEmptyText(stringResource(Res.string.network_no_body))
        } else {
            BodyContent(
                body = body,
                size = size,
                modifier = Modifier.recordInto(graphicsLayer),
            )
        }
    }
}

@Composable
private fun BodyContent(
    body: String,
    size: String,
    modifier: Modifier = Modifier,
) {
    var jsonError by remember(body) { mutableStateOf(false) }

    if (!jsonError) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(JsonTreeHeight)
                .padding(FloconTheme.spacing.small)
                .background(
                    FloconTheme.colors.bg000,
                    shape = FloconTheme.shapes.medium,
                )
                .padding(FloconTheme.spacing.medium),
        ) {
            FloconJsonTree(
                json = body,
                initialState = TreeState.EXPANDED,
                onError = { jsonError = true },
                modifier = Modifier.fillMaxSize()
            )
        }
        return
    }

    var displayBody by remember(body) { mutableStateOf(body.length <= LARGE_BODY_LENGTH) }
    if (!displayBody) {
        Column(
            modifier = modifier
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
                text = "Body too large ($size bytes)",
                color = FloconTheme.colors.ink,
                style = FloconTheme.typography.bodySmall,
            )
            Spacer(modifier = Modifier.height(FloconTheme.spacing.small))
            FloconButton(
                onClick = { displayBody = true },
                containerColor = FloconTheme.colors.ink,
            ) {
                Text(stringResource(Res.string.network_display_anyway))
            }
        }
    } else {
        FloconCodeBlock(
            code = body,
            containerColor = FloconTheme.colors.bg000,
            contentColor = FloconTheme.colors.inkMuted,
            borderColor = FloconTheme.colors.line,
            modifier = modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.medium)
        )
    }
}

@Composable
private fun ImageCard(
    state: NetworkDetailViewState,
    imageUrl: String,
    onAction: (NetworkDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    DetailSectionCard(
        title = stringResource(Res.string.network_image_response),
        badge = null,
        copyContentDescription = stringResource(Res.string.network_copy_url),
        onCopy = null,
        modifier = modifier,
        collapsible = true,
        toolbar = {
            DetailActionButton(
                label = stringResource(Res.string.general_copy),
                imageVector = Icons.Outlined.CopyAll,
                onClick = { onAction(NetworkDetailAction.CopyText(imageUrl)) },
                contentDescription = stringResource(Res.string.network_copy_url),
            )
        },
    ) {
        AsyncImage(
            model = remember(state.callId, imageUrl) {
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
                .padding(FloconTheme.spacing.medium)
                .height(ImageHeight),
            contentScale = ContentScale.Fit,
        )
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
                                    color = FloconTheme.colors.bg100,
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
                fullUrl = "http://www.google.com/search?q=flocon&lang=fr",
                host = "www.google.com",
                path = "/search",
                queryParams = persistentListOf(
                    NetworkDetailHeaderUi(name = "q", value = "flocon"),
                    NetworkDetailHeaderUi(name = "lang", value = "fr"),
                ),
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
