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
import io.github.openflocon.library.designsystem.components.DefaultLabel
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.badquality_select_exception
import flocondesktop.composeapp.generated.resources.badquality_weight
import flocondesktop.composeapp.generated.resources.general_exception

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
        modifier = Modifier.fillMaxWidth()
    ) {
        FloconDialogHeader(
            title = stringResource(Res.string.general_exception),
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            modifier = Modifier.padding(all = FloconTheme.spacing.small),
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small)
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

            DefaultLabel(stringResource(Res.string.badquality_select_exception))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
            ) {
                NetworkExceptionSelector(
                    selected = errorClassPath,
                    onSelected = {
                        errorClassPath = it
                    }
                )
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
                    .padding(all = FloconTheme.spacing.small),
            )
        }
    }
}
