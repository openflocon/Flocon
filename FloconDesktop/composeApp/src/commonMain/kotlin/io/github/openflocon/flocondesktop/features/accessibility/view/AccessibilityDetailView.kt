package io.github.openflocon.flocondesktop.features.accessibility.view

import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.ImmutableList
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.accessibility_detail_bounds
import flocondesktop.composeapp.generated.resources.accessibility_detail_class_name
import flocondesktop.composeapp.generated.resources.accessibility_detail_content_description
import flocondesktop.composeapp.generated.resources.accessibility_detail_copy
import flocondesktop.composeapp.generated.resources.accessibility_detail_extras
import flocondesktop.composeapp.generated.resources.accessibility_detail_fields
import flocondesktop.composeapp.generated.resources.accessibility_detail_no_extras
import flocondesktop.composeapp.generated.resources.accessibility_detail_screen
import flocondesktop.composeapp.generated.resources.accessibility_detail_text
import flocondesktop.composeapp.generated.resources.accessibility_detail_time
import flocondesktop.composeapp.generated.resources.accessibility_detail_type
import flocondesktop.composeapp.generated.resources.detail_copy_properties
import io.github.openflocon.flocondesktop.common.ui.detail.DetailChip
import io.github.openflocon.flocondesktop.common.ui.detail.DetailEmptyText
import io.github.openflocon.flocondesktop.common.ui.detail.DetailHeader
import io.github.openflocon.flocondesktop.common.ui.detail.DetailKeyValueList
import io.github.openflocon.flocondesktop.common.ui.detail.DetailPanel
import io.github.openflocon.flocondesktop.common.ui.detail.DetailSectionCard
import io.github.openflocon.flocondesktop.features.accessibility.AccessibilityDetailViewModel
import io.github.openflocon.flocondesktop.features.accessibility.model.AccessibilityDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun AccessibilityDetailView(
    id: String,
) {
    val viewModel = koinViewModel<AccessibilityDetailViewModel>(key = id) {
        parametersOf(id)
    }
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    uiState?.let {
        Content(
            uiState = it,
            onCopyFields = viewModel::onCopyFields,
            onCopyExtras = viewModel::onCopyExtras,
        )
    }
}

@Composable
private fun Content(
    uiState: AccessibilityDetailUiModel,
    onCopyFields: () -> Unit,
    onCopyExtras: () -> Unit,
) {
    DetailPanel(
        header = {
            DetailHeader(
                label = uiState.type,
                title = uiState.title,
                chips = {
                    DetailChip(text = uiState.timeFormatted)
                },
            )
        },
    ) {
        FieldsCard(
            uiState = uiState,
            onCopyFields = onCopyFields,
        )
        ExtrasCard(
            extras = uiState.extras,
            onCopyExtras = onCopyExtras,
        )
    }
}

@Composable
private fun FieldsCard(
    uiState: AccessibilityDetailUiModel,
    onCopyFields: () -> Unit,
) {
    val timeLabel = stringResource(Res.string.accessibility_detail_time)
    val typeLabel = stringResource(Res.string.accessibility_detail_type)
    val textLabel = stringResource(Res.string.accessibility_detail_text)
    val contentDescriptionLabel = stringResource(Res.string.accessibility_detail_content_description)
    val classNameLabel = stringResource(Res.string.accessibility_detail_class_name)
    val screenLabel = stringResource(Res.string.accessibility_detail_screen)
    val boundsLabel = stringResource(Res.string.accessibility_detail_bounds)

    // the fields the event does not set are not displayed
    val entries = remember(uiState) {
        listOfNotNull(
            timeLabel to uiState.timeFormatted,
            typeLabel to uiState.type,
            uiState.text?.let { textLabel to it },
            uiState.contentDescription?.let { contentDescriptionLabel to it },
            uiState.className?.let { classNameLabel to it },
            uiState.screen?.let { screenLabel to it },
            uiState.bounds?.let { boundsLabel to it },
        )
    }

    DetailSectionCard(
        title = stringResource(Res.string.accessibility_detail_fields),
        badge = null,
        copyContentDescription = stringResource(Res.string.accessibility_detail_copy),
        onCopy = onCopyFields,
    ) {
        DetailKeyValueList(entries = entries)
    }
}

@Composable
private fun ExtrasCard(
    extras: ImmutableList<Pair<String, String>>,
    onCopyExtras: () -> Unit,
) {
    DetailSectionCard(
        title = stringResource(Res.string.accessibility_detail_extras),
        badge = extras.size.toString(),
        copyContentDescription = stringResource(Res.string.detail_copy_properties),
        onCopy = onCopyExtras.takeIf { extras.isNotEmpty() },
    ) {
        if (extras.isNotEmpty()) {
            DetailKeyValueList(entries = extras)
        } else {
            DetailEmptyText(stringResource(Res.string.accessibility_detail_no_extras))
        }
    }
}

@Preview
@Composable
private fun AccessibilityDetailPreview() {
    FloconTheme {
        Content(
            uiState = AccessibilityDetailUiModel(
                id = "1",
                title = "Add to cart",
                type = "VIEW_FOCUSED",
                timeFormatted = "12:03:45.120",
                text = "Add to cart",
                contentDescription = null,
                className = "android.widget.Button",
                screen = "CheckoutActivity",
                bounds = "[0,0][100,48]",
                extras = persistentListOf("checked" to "false"),
            ),
            onCopyFields = {},
            onCopyExtras = {},
        )
    }
}
