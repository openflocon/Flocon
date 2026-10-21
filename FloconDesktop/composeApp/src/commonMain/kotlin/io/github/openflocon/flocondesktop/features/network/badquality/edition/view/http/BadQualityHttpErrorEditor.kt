package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.badquality_http_error
import flocondesktop.composeapp.generated.resources.badquality_weight
import flocondesktop.composeapp.generated.resources.mock_http_code
import flocondesktop.composeapp.generated.resources.network_body

@Composable
internal fun BadQualityHttpErrorEditor(
    error: BadQualityConfigUiModel.Error,
    httpType: BadQualityConfigUiModel.Error.Type.Body,
    cancel: () -> Unit,
    save: (BadQualityConfigUiModel.Error) -> Unit,
) {
    var weight by remember(error) { mutableStateOf<String>(error.weight.toString()) }
    var httpCode by remember(error) { mutableStateOf<String>(httpType.httpCode.toString()) }
    var contentType by remember { mutableStateOf<String>(httpType.contentType) }
    var body by remember(error) { mutableStateOf<String>(httpType.body) }

    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        FloconDialogHeader(
            title = stringResource(Res.string.badquality_http_error),
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier
                .padding(vertical = FloconTheme.spacing.small, horizontal = FloconTheme.spacing.medium),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
        ) {
            FloconTextField(
                label = defaultLabel(stringResource(Res.string.badquality_weight)),
                placeholder = defaultPlaceHolder("eg: 1.0"),
                value = weight,
                onValueChange = {
                    if (it.isEmpty() || it.toFloatOrNull() != null) {
                        weight = it
                    }
                },
                containerColor = FloconTheme.colors.bg100
            )

            FloconTextField(
                label = defaultLabel(stringResource(Res.string.mock_http_code)),
                placeholder = defaultPlaceHolder("eg: 500"),
                value = httpCode,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        httpCode = it
                    }
                },
                containerColor = FloconTheme.colors.bg100
            )

            FloconTextField(
                label = defaultLabel("Content-Type"),
                placeholder = defaultPlaceHolder("application/json"),
                value = contentType,
                onValueChange = { contentType = it },
                containerColor = FloconTheme.colors.bg100
            )

            FloconTextField(
                label = defaultLabel(stringResource(Res.string.network_body)),
                placeholder = defaultPlaceHolder("{\n\t\"error\" : \"...\"\n}"),
                value = body,
                minLines = 5,
                onValueChange = { body = it },
                containerColor = FloconTheme.colors.bg100
            )
        }

        FloconDialogButtons(
            onCancel = cancel,
            onValidate = {
                save(
                    error.copy(
                        weight = weight.toFloatOrNull() ?: error.weight,
                        type = httpType.copy(
                            httpCode = httpCode.toIntOrNull() ?: httpType.httpCode,
                            contentType = contentType,
                            body = body,
                        )
                    ),
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = FloconTheme.spacing.small),
        )
    }
}
