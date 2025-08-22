package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.SelectedBadQualityUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconSurface
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.defaultLabel
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import java.util.UUID

@Composable
fun BadQualityErrorsListView(
    errors: List<BadQualityConfigUiModel.Error>,
    onErrorsClicked: (error: BadQualityConfigUiModel.Error) -> Unit,
    deleteError: (error: BadQualityConfigUiModel.Error) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text("Errors", style = FloconTheme.typography.titleMedium)
            FloconButton(
                onClick = {
                    onErrorsClicked(
                        BadQualityConfigUiModel.Error(
                            // new error
                            weight = 1f,
                            type = BadQualityConfigUiModel.Error.Type.Body(
                                httpCode = 500,
                                body = "{\"error\":\"...\"}",
                                contentType = "application/json",
                            ),
                        ),
                    )
                },
            ) {
                Text(
                    text = "Add http error",
                )
            }
            FloconButton(
                onClick = {
                    onErrorsClicked(
                        BadQualityConfigUiModel.Error(
                            // new error
                            weight = 1f,
                            type = BadQualityConfigUiModel.Error.Type.Exception(
                                classPath = possibleExceptions.first().classPath,
                            ),
                        ),
                    )
                },
            ) {
                Text(
                    text = "Add Exception",
                )
            }
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            items(errors) { error ->
                Column(
                    modifier = Modifier
                        .width(230.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFEFEFEF).copy(alpha = 0.2f))
                        .clickable {
                            onErrorsClicked(error)
                        }
                        .padding(8.dp),
                ) {
                    val textStyle = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Thin,
                        color = FloconTheme.colorPalette.onSurface,
                    )

                    Text("Weight : ${error.weight}", style = textStyle)
                    when (val t = error.type) {
                        is BadQualityConfigUiModel.Error.Type.Body -> {
                            Text("HttpCode : ${t.httpCode}", style = textStyle) // or throwable ?
                            Text(t.contentType, style = textStyle)
                            Text("Body : ${t.body}", maxLines = 2, style = textStyle)
                        }

                        is BadQualityConfigUiModel.Error.Type.Exception -> {
                            Text(
                                "Exception",
                                style = textStyle
                            )
                            Text(
                                t.classPath,
                                style = textStyle
                            )
                        }
                    }

                    // bouton supprimer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Image(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete error",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    deleteError(
                                        error,
                                    )
                                },
                            colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onSurface),
                        )
                    }
                }
            }
        }
    }
}
