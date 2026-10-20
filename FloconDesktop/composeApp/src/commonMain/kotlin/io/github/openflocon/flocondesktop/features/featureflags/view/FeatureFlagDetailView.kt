package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flag_no_payload
import flocondesktop.composeapp.generated.resources.feature_flag_payload
import flocondesktop.composeapp.generated.resources.feature_flag_provider
import flocondesktop.composeapp.generated.resources.feature_flag_variant
import flocondesktop.composeapp.generated.resources.name
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagDetailViewModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun FeatureFlagDetailView(
    provider: String,
    name: String,
) {
    val viewModel = koinViewModel<FeatureFlagDetailViewModel>(key = "$provider/$name") {
        parametersOf(provider, name)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(uiState = it)
    }
}

@Composable
private fun Content(
    uiState: FeatureFlagDetailUiModel,
) {
    val scrollState = rememberScrollState()
    val linesLabelWidth: Dp = 130.dp
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    Box(
        Modifier.background(FloconTheme.colorPalette.primary)
    ) {
        SelectionContainer(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 18.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                FeatureFlagDetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.name),
                    value = uiState.name,
                    labelWidth = linesLabelWidth,
                )
                FeatureFlagDetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.feature_flag_provider),
                    value = uiState.provider,
                    labelWidth = linesLabelWidth,
                )
                FeatureFlagDetailLineView(
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(Res.string.feature_flag_variant),
                    value = uiState.variant,
                    labelWidth = linesLabelWidth,
                )
                Text(
                    text = stringResource(Res.string.feature_flag_payload),
                    style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = FloconTheme.colorPalette.onPrimary,
                    modifier = Modifier.padding(top = 12.dp, start = 8.dp, bottom = 4.dp),
                )
                val payload = uiState.payload
                if (payload != null) {
                    FloconCodeBlock(
                        code = payload,
                        containerColor = FloconTheme.colorPalette.secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    Text(
                        text = stringResource(Res.string.feature_flag_no_payload),
                        style = FloconTheme.typography.bodyMedium,
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
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
