package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sebastianneubauer.jsontree.search.rememberSearchState
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindow
import io.github.openflocon.flocondesktop.common.ui.window.FloconWindowState
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkJsonUi
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import kotlinx.coroutines.launch

@Composable
fun NetworkJsonScreen(
    json: NetworkJsonUi,
    state: FloconWindowState,
    onCloseRequest: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var jsonError by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val searchState = rememberSearchState()

    LaunchedEffect(query) {
        searchState.query = query
    }

    FloconWindow(
        title = "Body",
        state = state,
        onCloseRequest = onCloseRequest
    ) {
        FloconSurface(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                FloconJsonTree(
                    json = json.json,
                    searchState = searchState,
                    onError = { jsonError = true },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp)
                )
                if (!jsonError) {
                    AnimatedVisibility(
                        visible = !searchState.query.isNullOrEmpty(),
                        enter = slideInVertically { it },
                        exit = slideOutVertically { it }
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(FloconTheme.colorPalette.panel)
                                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                        ) {
                            FloconIconButton(
                                imageVector = Icons.Outlined.ArrowUpward,
                                onClick = { scope.launch { searchState.selectPrevious() } }
                            )
                            FloconIconButton(
                                imageVector = Icons.Outlined.ArrowDownward,
                                onClick = { scope.launch { searchState.selectNext() } }
                            )
                            Text(
                                text = "${searchState.selectedResultIndex?.inc() ?: 0}/${searchState.totalResults}"
                            )
                        }
                    }
                    FloconTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FloconIcon(Icons.Outlined.Search)
                                Text(text = "Search")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(FloconTheme.colorPalette.panel)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}
