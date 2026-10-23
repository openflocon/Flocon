package io.github.openflocon.flocondesktop.features.network.body

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianneubauer.jsontree.search.rememberSearchState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.general_clear
import flocondesktop.composeapp.generated.resources.match_case_disabled
import flocondesktop.composeapp.generated.resources.match_case_enabled
import flocondesktop.composeapp.generated.resources.search
import flocondesktop.composeapp.generated.resources.search_match_case
import io.github.openflocon.flocondesktop.features.network.body.model.NetworkBodyDetailUi
import io.github.openflocon.flocondesktop.features.network.body.model.previewNetworkBodyDetailUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NetworkJsonScreen(
    json: String,
    modifier: Modifier = Modifier,
    key: String = json,
) {
    val viewModel = koinViewModel<NetworkJsonViewModel>(key = key) {
        parametersOf(json)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkBodyContent(
        body = uiState,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun NetworkBodyContent(
    body: NetworkBodyDetailUi,
    modifier: Modifier = Modifier,
) {
    var jsonError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val searchState = rememberSearchState()
    val listState = rememberLazyListState()

    val resultIndex = searchState.selectedResultListIndex
    LaunchedEffect(resultIndex) {
        if (resultIndex != null && !listState.isScrollInProgress) {
            listState.animateScrollToItem(resultIndex)
        }
    }

    FloconSurface(
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            if (!jsonError) {
                SearchBar(
                    modifier = Modifier.fillMaxWidth(),
                    query = searchState.query ?: "",
                    queryChanged = {
                        searchState.query = it
                    },
                    matchCaseChanged = {
                        searchState.caseSensitive = !searchState.caseSensitive
                    },
                    previousClicked = {
                        scope.launch {
                            searchState.selectPrevious()
                        }
                    },
                    nextClicked = {
                        scope.launch { searchState.selectNext() }
                    },
                    selectedResultIndex = searchState.selectedResultIndex,
                    totalResults = searchState.totalResults,
                    matchCase = searchState.caseSensitive
                )
            }
            FloconJsonTree(
                json = body.text,
                searchState = searchState,
                lazyListState = listState,
                onError = { jsonError = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(FloconTheme.spacing.small),
            )
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    previousClicked: () -> Unit,
    nextClicked: () -> Unit,
    queryChanged: (String) -> Unit,
    matchCaseChanged: () -> Unit,
    selectedResultIndex: Int?,
    totalResults: Int,
    matchCase: Boolean,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colors.bg100)
                .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.compact),
        ) {
            FloconTextFieldWithoutM3(
                value = query,
                onValueChange = { queryChanged(it) },
                placeholder = defaultPlaceHolder(
                    stringResource(Res.string.search),
                    color = FloconTheme.colors.inkMuted.copy(alpha = 0.5f)
                ),
                leadingComponent = {
                    FloconIcon(
                        imageVector = Icons.Outlined.Search,
                        modifier = Modifier.size(16.dp),
                        tint = FloconTheme.colors.inkMuted.copy(alpha = 0.7f),
                    )
                },
                trailingComponent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
                    ) {
                        FloconSmallIconButton(
                            imageVector = if (matchCase) {
                                vectorResource(Res.drawable.match_case_enabled)
                            } else {
                                vectorResource(Res.drawable.match_case_disabled)
                            },
                            onClick = { matchCaseChanged() },
                            contentDescription = stringResource(Res.string.search_match_case),
                            modifier = Modifier
                                .size(16.dp)
                                .pointerHoverIcon(PointerIcon.Hand)
                        )

                        if (query.isNotEmpty()) {
                            FloconSmallIconButton(
                                imageVector = Icons.Outlined.Close,
                                onClick = { queryChanged("") },
                                contentDescription = stringResource(Res.string.general_clear),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                containerColor = FloconTheme.colors.bg200,
                textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colors.inkMuted),
                contentPadding = PaddingValues(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.tiny),
                modifier = Modifier
                    .width(280.dp)
                    .height(30.dp)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colors.ink.copy(alpha = 0.2f),
                        shape = FloconTheme.shapes.medium
                    )
                    .focusRequester(focusRequester)
                    .onPreviewKeyEvent { event ->
                        if (event.type == KeyEventType.KeyDown) {
                            when (event.key) {
                                Key.Enter, Key.NumPadEnter -> {
                                    if (event.isShiftPressed) {
                                        previousClicked()
                                    } else {
                                        nextClicked()
                                    }
                                    true
                                }

                                Key.DirectionDown -> {
                                    nextClicked()
                                    true
                                }

                                Key.DirectionUp -> {
                                    previousClicked()
                                    true
                                }

                                else -> false
                            }
                        } else {
                            false
                        }
                    },
            )

            AnimatedVisibility(visible = totalResults > 0) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .height(30.dp)
                        .border(
                            width = 1.dp,
                            color = FloconTheme.colors.ink.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(start = FloconTheme.spacing.small, end = FloconTheme.spacing.tiny),
                    horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                ) {
                    Text(
                        text = "${selectedResultIndex?.inc() ?: 0}/$totalResults",
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colors.inkMuted,
                    )
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowUpward,
                        onClick = previousClicked,
                        contentPadding = PaddingValues(all = FloconTheme.spacing.extraSmall),
                        enabled = selectedResultIndex != null,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .width(1.dp),
                        color = FloconTheme.colors.inkMuted.copy(alpha = 0.2f)
                    )
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowDownward,
                        onClick = nextClicked,
                        contentPadding = PaddingValues(all = FloconTheme.spacing.extraSmall),
                        enabled = selectedResultIndex != null,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                }
            }
        }

        FloconHorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FloconTheme.colors.bg200
        )
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    FloconTheme {
        SearchBar(
            query = "Search",
            previousClicked = {},
            nextClicked = {},
            queryChanged = {},
            matchCaseChanged = {},
            totalResults = 3,
            selectedResultIndex = 1,
            matchCase = false
        )
    }
}

@Preview
@Composable
private fun NetworkBodyContentPreview() {
    FloconTheme {
        NetworkBodyContent(
            body = previewNetworkBodyDetailUi(),
        )
    }
}
