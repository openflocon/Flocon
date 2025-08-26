package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.openflocon.flocondesktop.features.deeplinks.DeepLinkViewModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPart
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkViewState
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkViewState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconPageTopBar
import io.github.openflocon.library.designsystem.components.FloconSurface
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DeeplinkScreen(modifier: Modifier = Modifier) {
    val viewModel: DeepLinkViewModel = koinViewModel()
    val deepLinks by viewModel.deepLinks.collectAsStateWithLifecycle()

    DeeplinkScreen(
        deepLinks = deepLinks,
        submit = viewModel::submit,
        modifier = modifier,
    )
}

@Composable
private fun DeeplinkScreen(
    deepLinks: List<DeeplinkViewState>,
    submit: (DeeplinkViewState, values: Map<DeeplinkPart.TextField, String>) -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconSurface(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            FloconPageTopBar(
                modifier = Modifier.fillMaxWidth(),
                filterBar = {
                    DeeplinkFreeformItemView(
                        submit = submit,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    all = 12.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(deepLinks) { item ->
                    DeeplinkItemView(
                        submit = submit,
                        item = item,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun DeeplinkScreenPreview() {
    FloconTheme {
        DeeplinkScreen(
            deepLinks = listOf(
                previewDeeplinkViewState(),
                previewDeeplinkViewState(),
                previewDeeplinkViewState(),
            ),
            submit = { _, _ -> },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
