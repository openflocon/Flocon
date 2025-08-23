package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import java.util.UUID

@Composable
fun BadQuaityErrorExceptionEditor(
    error: BadQualityConfigUiModel.Error,
    errorException: BadQualityConfigUiModel.Error.Type.Exception,
    save: (BadQualityConfigUiModel.Error) -> Unit,
    cancel: () -> Unit,
) {
    var weight by remember(error) { mutableStateOf<String>(error.weight.toString()) }
    var errorClassPath by remember(error) { mutableStateOf<String>(errorException.classPath) }

    Column(
        modifier = Modifier.padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
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

        possibleExceptions.fastForEach { exception ->
            val (backgroundColor, textColor) = if (exception.classPath == errorClassPath) {
                FloconTheme.colorPalette.onSurface to FloconTheme.colorPalette.panel
            } else {
                FloconTheme.colorPalette.panel to FloconTheme.colorPalette.onSurface
            }
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = backgroundColor,
                    )
                    .then(
                        Modifier.clickable(
                            onClick = {
                                errorClassPath = exception.classPath
                            },
                        )
                    ).padding(all = 8.dp)
            ) {
                Text(
                    text = exception.description,
                    style = FloconTheme.typography.bodySmall,
                    color = textColor,
                )
                Text(
                    text = exception.classPath,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = textColor,
                )
            }
        }

        FloconDialogButtons(
            onCancel = cancel,
            onValidate = {
                save(
                    error.copy(
                        weight = weight.toFloatOrNull() ?: error.weight,
                        type = errorException.copy(
                            classPath = errorClassPath
                        )
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp),
        )
    }
}
