package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.flocondesktop.features.network.list.view.components.errorTagText
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter

@Composable
fun BadQualityErrorsListView(
    errors: List<BadQualityConfigUiModel.Error>,
    onErrorslicked: (error: BadQualityConfigUiModel.Error) -> Unit,
    deleteError: (error: BadQualityConfigUiModel.Error) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        ) {
            Text("Errors", style = FloconTheme.typography.titleMedium)
            FloconButton(
                onClick = {
                    onErrorslicked(
                        BadQualityConfigUiModel.Error(
                            // new error
                            weight = 1f,
                            type = BadQualityConfigUiModel.Error.Type.Body(
                                httpCode = 500,
                                body = "",
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
                    onErrorslicked(
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

        if (errors.isNotEmpty()) {
            val lazyListState = rememberLazyListState()
            val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
            Box(modifier = Modifier.height(160.dp)) {
                val contentPadding = PaddingValues(horizontal = 12.dp)
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    if (errors.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = 6.dp)
                            ) {
                                Text(
                                    "Weight",
                                    style = FloconTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Thin,
                                        fontSize = 10.sp,
                                    ),
                                )
                                Spacer(modifier = Modifier.width(28.dp))
                                Text(
                                    "Type",
                                    style = FloconTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Thin,
                                        fontSize = 10.sp,
                                    ),
                                )
                            }
                        }
                    }
                    items(errors) { error ->
                        BadQualityErrorItemView(
                            modifier = Modifier,
                            error = error,
                            deleteError = deleteError,
                            clickedError = onErrorslicked,
                            contentPadding = contentPadding,
                        )
                    }
                }
                FloconVerticalScrollbar(
                    adapter = scrollAdapter,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@Composable
private fun BadQualityErrorItemView(
    modifier: Modifier = Modifier,
    error: BadQualityConfigUiModel.Error,
    contentPadding: PaddingValues,
    clickedError: (BadQualityConfigUiModel.Error) -> Unit,
    deleteError: (BadQualityConfigUiModel.Error) -> Unit
) {
    Row(
        modifier = modifier.clickable {
            clickedError(error)
        }.padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier.size(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(FloconTheme.colorPalette.onSurface),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = if (error.weight.rem(1.0) == 0.0) {
                    // Affiche la valeur comme un entier si elle est égale à 1.0, 2.0, etc.
                    error.weight.toInt().toString()
                } else {
                    // Affiche la valeur telle quelle si elle a une décimale non nulle
                    error.weight.toString()
                },
                style = FloconTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                ),
                color = FloconTheme.colorPalette.surface,
            )
        }
        when (val t = error.type) {
            is BadQualityConfigUiModel.Error.Type.Body -> {
                Text(
                    "http(${t.httpCode})",
                    maxLines = 1,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    color = errorTagText,
                )
                Text(
                    t.body.take(20),
                    maxLines = 1,
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = FloconTheme.colorPalette.primary,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }

            is BadQualityConfigUiModel.Error.Type.Exception -> {
                Text(
                    "exception",
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = Color(0xFF7B1FA2) // FloconTheme.colorPalette.exceptions, TODO
                )
                Text(
                    text = t.classPath,
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = FloconTheme.colorPalette.primary,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 4.dp),
                )
            }
        }

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
