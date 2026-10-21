package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flag_copy_payload
import flocondesktop.composeapp.generated.resources.feature_flag_no_payload
import flocondesktop.composeapp.generated.resources.feature_flag_payload
import flocondesktop.composeapp.generated.resources.feature_flag_state_custom
import flocondesktop.composeapp.generated.resources.feature_flag_state_off
import flocondesktop.composeapp.generated.resources.feature_flag_state_on
import io.github.openflocon.flocondesktop.features.featureflags.FeatureFlagDetailViewModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagDetailUiModel
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconCodeBlock
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
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
    val scrollState = rememberScrollState()
    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    Box(
        Modifier.background(FloconTheme.colorPalette.primary)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Header(uiState = uiState)
            FloconHorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = FloconTheme.colorPalette.secondary,
            )
            PayloadCard(
                uiState = uiState,
                onCopyPayload = onCopyPayload,
            )
        }
        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun Header(uiState: FeatureFlagDetailUiModel) {
    SelectionContainer {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = uiState.provider.uppercase(),
                style = FloconTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                ),
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
            )
            Text(
                text = uiState.name,
                style = FloconTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colorPalette.onPrimary,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                StateChip(state = uiState.state)
                FeatureFlagVariantChip(
                    state = uiState.state,
                    variant = uiState.variant,
                )
            }
        }
    }
}

@Composable
private fun StateChip(state: FeatureFlagStateUiModel) {
    Row(
        modifier = Modifier
            .clip(FloconTheme.shapes.medium)
            .background(FloconTheme.colorPalette.secondary)
            .padding(horizontal = 10.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeatureFlagStateDot(state = state)
        Text(
            text = stringResource(
                when (state) {
                    FeatureFlagStateUiModel.On -> Res.string.feature_flag_state_on
                    FeatureFlagStateUiModel.Off -> Res.string.feature_flag_state_off
                    FeatureFlagStateUiModel.Custom -> Res.string.feature_flag_state_custom
                }
            ),
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colorPalette.onSecondary,
        )
    }
}

@Composable
private fun PayloadCard(
    uiState: FeatureFlagDetailUiModel,
    onCopyPayload: () -> Unit,
) {
    val shape = FloconTheme.shapes.medium
    val payload = uiState.payload
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(width = 1.dp, color = FloconTheme.colorPalette.secondary, shape = shape),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(FloconTheme.colorPalette.surface)
                .padding(start = 12.dp, end = 8.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(Res.string.feature_flag_payload),
                style = FloconTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                color = FloconTheme.colorPalette.onSurface,
            )
            if (payload != null) {
                Text(
                    text = if (uiState.isJsonPayload) "JSON" else "TEXT",
                    style = FloconTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp,
                    ),
                    color = FloconTheme.colorPalette.onSecondary,
                    modifier = Modifier
                        .clip(FloconTheme.shapes.extraSmall)
                        .background(FloconTheme.colorPalette.secondary)
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                )
            }
            Spacer(Modifier.weight(1f))
            if (payload != null) {
                FloconSmallIconButton(
                    imageVector = Icons.Outlined.ContentCopy,
                    onClick = onCopyPayload,
                    contentDescription = stringResource(Res.string.feature_flag_copy_payload),
                    modifier = Modifier.size(16.dp),
                )
            }
        }
        if (payload != null) {
            FloconCodeBlock(
                code = payload,
                containerColor = FloconTheme.colorPalette.primary,
                modifier = Modifier.fillMaxWidth().padding(4.dp),
            )
        } else {
            Text(
                text = stringResource(Res.string.feature_flag_no_payload),
                style = FloconTheme.typography.bodySmall,
                color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.6f),
                modifier = Modifier.padding(12.dp),
            )
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
