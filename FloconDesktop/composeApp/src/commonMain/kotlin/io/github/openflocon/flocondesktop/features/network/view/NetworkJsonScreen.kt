package io.github.openflocon.flocondesktop.features.network.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sebastianneubauer.jsontree.search.rememberSearchState
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.features.network.model.NetworkBodyDetailUi
import io.github.openflocon.flocondesktop.features.network.model.previewNetworkBodyDetailUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconSmallTextField
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun NetworkBodyWindow(
    body: NetworkBodyDetailUi,
    state: FloconWindowState,
    onCloseRequest: () -> Unit,
) {
    FloconWindow(
        title = "Body",
        state = state,
        onCloseRequest = onCloseRequest,
    ) {
        NetworkBodyContent(
            body = body,
            modifier = Modifier.fillMaxSize(),
        )
    }
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

    LaunchedEffect(query) {
        searchState.query = query
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
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(FloconTheme.colorPalette.panel)
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        FloconTextField(
            value = query,
            onValueChange = { queryChanged(it) },
            placeHolderText = "Search",
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(FloconTheme.colorPalette.surfaceVariant)
                .padding(vertical = 8.dp, horizontal = 12.dp)
                .heightIn(min = 24.dp),
        )

        AnimatedVisibility(visible = totalResults > 0) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(start = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "${selectedResultIndex?.inc() ?: 0}/$totalResults",
                    modifier = Modifier.widthIn(min = 40.dp),
                    textAlign = TextAlign.End,
                    style = FloconTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                )
                Row(
                    modifier = Modifier
                        .height(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(FloconTheme.colorPalette.surfaceVariant),
                ) {
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowUpward,
                        onClick = previousClicked,
                        contentPadding = PaddingValues(all = 4.dp),
                        enabled = selectedResultIndex != null && selectedResultIndex > 0,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                    VerticalDivider(modifier = Modifier.fillMaxHeight())
                    FloconSmallIconButton(
                        imageVector = Icons.Outlined.ArrowDownward,
                        onClick = nextClicked,
                        contentPadding = PaddingValues(all = 4.dp),
                        enabled = selectedResultIndex != null && selectedResultIndex < totalResults - 1,
                        modifier = Modifier.fillMaxHeight().aspectRatio(1f),
                    )
                }
            }
        }
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
