package io.github.openflocon.flocondesktop.features.network.body

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sebastianneubauer.jsontree.search.rememberSearchState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.search
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
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun NetworkJsonScreen(
    json: String,
    key: String = json,
) {
    val viewModel = koinViewModel<NetworkJsonViewModel>(key = key) {
        parametersOf(json)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NetworkBodyContent(
        body = uiState,
        modifier = Modifier.fillMaxSize(),
    )
}

@Composable
fun NetworkBodyWindow(
    body: NetworkBodyDetailUi
) {
    NetworkBodyContent(
        body = body,
        modifier = Modifier.fillMaxSize(),
    )
}


@Composable
private fun NetworkBodyContent(
    body: NetworkBodyDetailUi,
    modifier: Modifier = Modifier,
) {
    var jsonError by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val searchState = rememberSearchState()
    val listState = rememberLazyListState()

    LaunchedEffect(query) {
        searchState.query = query
    }

    val resultIndex = searchState.selectedResultListIndex
    LaunchedEffect(resultIndex) {
        if(resultIndex != null && !listState.isScrollInProgress) {
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
                    query = query,
                    queryChanged = {
                        query = it
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
                    .padding(8.dp),
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
    modifier: Modifier = Modifier,
    selectedResultIndex: Int?,
    totalResults: Int,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.primary)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            FloconTextFieldWithoutM3(
                value = query,
                onValueChange = { queryChanged(it) },
                placeholder = defaultPlaceHolder(
                    stringResource(Res.string.search),
                    color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.5f)
                ),
                leadingComponent = {
                    FloconIcon(
                        imageVector = Icons.Outlined.Search,
                        modifier = Modifier.size(16.dp),
                        tint = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.7f),
                    )
                },
                trailingComponent = {
                    if (query.isNotEmpty()) {
                        FloconSmallIconButton(
                            imageVector = Icons.Outlined.Close,
                            onClick = { queryChanged("") },
                            contentDescription = "Clear",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                },
                containerColor = FloconTheme.colorPalette.secondary,
                textStyle = FloconTheme.typography.bodySmall.copy(color = FloconTheme.colorPalette.onSecondary),
                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
                modifier = Modifier
                    .width(280.dp)
                    .height(30.dp)
                    .border(
                        width = 1.dp,
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.2f),
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
                            color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp)
                        )
                        .background(
                            color = FloconTheme.colorPalette.secondary,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .padding(start = 8.dp, end = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Text(
                        text = "${selectedResultIndex?.inc() ?: 0}/$totalResults",
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onSecondary,
                    )
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowUpward,
                        onClick = previousClicked,
                        contentPadding = PaddingValues(all = 4.dp),
                        enabled = selectedResultIndex != null,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                    VerticalDivider(
                        modifier = Modifier
                            .fillMaxHeight(0.6f)
                            .width(1.dp),
                        color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.2f)
                    )
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowDownward,
                        onClick = nextClicked,
                        contentPadding = PaddingValues(all = 4.dp),
                        enabled = selectedResultIndex != null,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                }
            }
        }

        FloconHorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            color = FloconTheme.colorPalette.secondary
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
            totalResults = 3,
            selectedResultIndex = 1,
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
