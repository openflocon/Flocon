package io.github.openflocon.flocondesktop.features.deeplinks.view

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.deeplink_panel_copy_link
import flocondesktop.composeapp.generated.resources.deeplink_panel_description
import flocondesktop.composeapp.generated.resources.deeplink_panel_link
import flocondesktop.composeapp.generated.resources.menu_item_deeplinks
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.deeplinks.DeeplinkPanelViewModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.DeeplinkPanelUiModel
import io.github.openflocon.flocondesktop.features.deeplinks.model.previewDeeplinkPanelUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DeeplinkPanel(
    id: Long
) {
    val viewModel = koinViewModel<DeeplinkPanelViewModel>(key = id.toString()) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopyLink = viewModel::onCopyLink,
        )
    }
}

@Composable
private fun Content(
    uiState: DeeplinkPanelUiModel,
    onCopyLink: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = stringResource(Res.string.menu_item_deeplinks),
                title = uiState.label ?: uiState.link,
            )
        },
    ) {
        DetailSectionCard(
            title = stringResource(Res.string.deeplink_panel_link),
            badge = null,
            copyContentDescription = stringResource(Res.string.deeplink_panel_copy_link),
            onCopy = onCopyLink,
        ) {
            Body(text = uiState.link)
        }
        uiState.description?.takeIf { it.isNotBlank() }?.let {
            DetailSectionCard(
                title = stringResource(Res.string.deeplink_panel_description),
                badge = null,
                copyContentDescription = "",
                onCopy = null,
            ) {
                Body(text = it)
            }
        }
    }
}

@Composable
private fun Body(text: String) {
    SelectionContainer {
        Text(
            text = text,
            style = FloconTheme.typography.bodySmall,
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .fillMaxWidth()
                .padding(FloconTheme.spacing.medium),
        )
    }
}

@Preview
@Composable
private fun DeeplinkPanelPreview() {
    FloconTheme {
        Content(
            uiState = previewDeeplinkPanelUiModel(),
            onCopyLink = {},
        )
    }
}
