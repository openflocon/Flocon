package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flag_copy_payload
import flocondesktop.composeapp.generated.resources.feature_flag_no_payload
import flocondesktop.composeapp.generated.resources.feature_flag_payload
import flocondesktop.composeapp.generated.resources.feature_flag_state_custom
import flocondesktop.composeapp.generated.resources.feature_flag_state_off
import flocondesktop.composeapp.generated.resources.feature_flag_state_on
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChipText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailEmptyText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagDetailViewModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
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
        Content(
            uiState = it,
            onCopyPayload = viewModel::onCopyPayload,
        )
    }
}

@Composable
private fun Content(
    uiState: FeatureFlagDetailUiModel,
    onCopyPayload: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = uiState.provider,
                title = uiState.name,
                chips = {
                    StateChip(state = uiState.state)
                    FeatureFlagVariantChip(
                        state = uiState.state,
                        variant = uiState.variant,
                    )
                },
            )
        },
    ) {
        PayloadCard(
            uiState = uiState,
            onCopyPayload = onCopyPayload,
        )
    }
}

@Composable
private fun StateChip(state: FeatureFlagStateUiModel) {
    DetailChip {
        FeatureFlagStateDot(state = state)
        DetailChipText(
            text = stringResource(
                when (state) {
                    FeatureFlagStateUiModel.On -> Res.string.feature_flag_state_on
                    FeatureFlagStateUiModel.Off -> Res.string.feature_flag_state_off
                    FeatureFlagStateUiModel.Custom -> Res.string.feature_flag_state_custom
                }
            ),
        )
    }
}

@Composable
private fun PayloadCard(
    uiState: FeatureFlagDetailUiModel,
    onCopyPayload: () -> Unit,
) {
    val payload = uiState.payload
    DetailSectionCard(
        title = stringResource(Res.string.feature_flag_payload),
        badge = payload?.let { if (uiState.isJsonPayload) "JSON" else "TEXT" },
        copyContentDescription = stringResource(Res.string.feature_flag_copy_payload),
        onCopy = onCopyPayload.takeIf { payload != null },
    ) {
        if (payload != null) {
            FloconCodeBlock(
                code = payload,
                containerColor = FloconTheme.colors.bg100,
                modifier = Modifier.fillMaxWidth().padding(4.dp),
            )
        } else {
            DetailEmptyText(stringResource(Res.string.feature_flag_no_payload))
        }
    }
}

@Preview
@Composable
private fun FeatureFlagDetailPreview() {
    FloconTheme {
        Content(
            uiState = FeatureFlagDetailUiModel(
                provider = "firebase",
                name = "new_checkout",
                state = FeatureFlagStateUiModel.Custom,
                variant = "treatment",
                payload = "{\n  \"buttonColor\": \"#FF5722\",\n  \"steps\": 3\n}",
                rawPayload = """{"buttonColor":"#FF5722","steps":3}""",
                isJsonPayload = true,
            ),
            onCopyPayload = {},
        )
    }
}
