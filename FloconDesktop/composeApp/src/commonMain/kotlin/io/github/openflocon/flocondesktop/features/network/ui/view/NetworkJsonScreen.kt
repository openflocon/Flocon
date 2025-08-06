package io.github.openflocon.flocondesktop.features.network.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
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
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import com.sebastianneubauer.jsontree.search.rememberSearchState
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.app_icon
import io.github.openflocon.flocondesktop.features.network.ui.model.NetworkJsonUi
import io.github.openflocon.library.designsystem.components.FloconIconButton
import io.github.openflocon.library.designsystem.components.FloconJsonTree
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@Composable
fun NetworkJsonScreen(
    json: NetworkJsonUi,
    state: WindowState,
    onCloseRequest: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val searchState = rememberSearchState()

    LaunchedEffect(query) {
        searchState.query = query
    }

    Window(
        title = "Body",
        icon = painterResource(Res.drawable.app_icon),
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
                    modifier = Modifier.weight(1f)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
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
                        text = "${searchState.selectedResultIndex?.inc()}/${searchState.totalResults}"
                    )
                }
                FloconTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                )
            }
        }
    }
}
