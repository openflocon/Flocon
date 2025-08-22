package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun BadQuaityErrorExceptionEditor(
    error: BadQualityConfigUiModel.Error,
    errorException: BadQualityConfigUiModel.Error.Type.Exception,
    onErrorsChange: (BadQualityConfigUiModel.Error) -> Unit,
) {
    Column(
        modifier = Modifier.padding(all = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        possibleExceptions.fastForEach { exception ->
            val (backgroundColor, textColor) = if (exception.classPath == errorException.classPath) {
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
                                onErrorsChange(
                                    error.copy(
                                        weight = 1f,
                                        type = errorException.copy(
                                            classPath = exception.classPath
                                        )
                                    )
                                )
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
    }
}
