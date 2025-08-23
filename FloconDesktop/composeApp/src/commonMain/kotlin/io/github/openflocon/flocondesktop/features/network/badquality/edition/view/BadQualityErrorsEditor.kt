package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

@Composable
internal fun BadQualityErrorsEditor(
    error: BadQualityConfigUiModel.Error,
    httpType: BadQualityConfigUiModel.Error.Type.Body,
    cancel: () -> Unit,
    save: (BadQualityConfigUiModel.Error) -> Unit,
) {
    var weight by remember(error) { mutableStateOf<String>(error.weight.toString()) }
    var httpCode by remember(error) { mutableStateOf<String>(httpType.httpCode.toString()) }
    var contentType by remember { mutableStateOf<String>(httpType.contentType) }
    var body by remember(error) { mutableStateOf<String>(httpType.body) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                .padding(8.dp),
        ) {
            FloconTextField(
                label = defaultLabel("Weight"),
                placeholder = defaultPlaceHolder("eg: 1.0"),
                value = weight,
                onValueChange = {
                    if (it.isEmpty() || it.toFloatOrNull() != null) {
                        weight = it
                    }
                },
            )

            FloconTextField(
                label = defaultLabel("HTTP Code"),
                placeholder = defaultPlaceHolder("eg: 500"),
                value = httpCode,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull() != null) {
                        httpCode = it
                    }
                },
            )

            FloconTextField(
                label = defaultLabel("Content-Type"),
                placeholder = defaultPlaceHolder("application/json"),
                value = contentType,
                onValueChange = { contentType = it }
            )

            FloconTextField(
                label = defaultLabel("Body"),
                placeholder = defaultPlaceHolder("{\"error\":\"...\"}"),
                value = body,
                onValueChange = { body = it },
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
                .padding(horizontal = 4.dp),
        )
    }
}
