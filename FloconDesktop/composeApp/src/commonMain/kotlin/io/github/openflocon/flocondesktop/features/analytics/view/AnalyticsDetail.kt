package io.github.openflocon.flocondesktop.features.analytics.view

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.detail_copy_properties
import flocondesktop.composeapp.generated.resources.detail_previous_session
import flocondesktop.composeapp.generated.resources.detail_properties
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.analytics.AnalyticsDetailViewModel
import io.github.openflocon.flocondesktop.features.analytics.model.AnalyticsDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AnalyticsDetail(
    id: String
) {
    val viewModel = koinViewModel<AnalyticsDetailViewModel>(key = id) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopyProperties = viewModel::onCopyProperties,
        )
    }
}

@Composable
private fun Content(
    uiState: AnalyticsDetailUiModel,
    onCopyProperties: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = uiState.tableId,
                title = uiState.eventName,
                chips = {
                    DetailChip(text = uiState.dateFormatted)
                    if (uiState.isFromOldAppInstance) {
                        DetailChip(text = stringResource(Res.string.detail_previous_session))
                    }
                },
            )
        },
    ) {
        PropertiesCard(
            properties = uiState.properties,
            onCopyProperties = onCopyProperties,
        )
    }
}

@Composable
private fun PropertiesCard(
    properties: List<AnalyticsDetailUiModel.PropertyUiModel>,
    onCopyProperties: () -> Unit,
) {
    val entries = remember(properties) { properties.map { it.name to it.value } }

    DetailSectionCard(
        title = stringResource(Res.string.detail_properties),
        badge = entries.size.toString(),
        copyContentDescription = stringResource(Res.string.detail_copy_properties),
        onCopy = onCopyProperties.takeIf { entries.isNotEmpty() },
    ) {
        Text(
            text = entries.joinToString("\n") { (name, value) -> "$name: $value" },
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colors.ink,
            modifier = Modifier.weight(1f)
                .padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.small),
        )
    }
}

@Preview
@Composable
private fun AnalyticsDetailLine(
    label: String,
    labelWidth: Dp,
    modifier: Modifier = Modifier,
    withDivider: Boolean,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier.height(intrinsicSize = IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = FloconTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold,
            ),
            color = FloconTheme.colors.ink,
            modifier = Modifier
                .padding(vertical = FloconTheme.spacing.compact, horizontal = FloconTheme.spacing.small)
                .width(labelWidth),
        )
    }
}
