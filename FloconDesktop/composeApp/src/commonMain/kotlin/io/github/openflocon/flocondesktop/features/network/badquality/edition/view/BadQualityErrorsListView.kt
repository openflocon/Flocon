package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

        val lazyListState = rememberLazyListState()
        val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(errors) { error ->
                    BadQualityErrorItemView(
                        modifier = Modifier.fillMaxWidth(),
                        error = error,
                        deleteError = deleteError,
                        clickedError = onErrorslicked,
                        contentPadding = PaddingValues(horizontal = 12.dp),
                    )
                }
            }
            FloconVerticalScrollbar(
                adapter = scrollAdapter,
                modifier = Modifier.fillMaxHeight(),
            )
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
                text = error.weight.toString(),
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
                            color = FloconTheme.colorPalette.panel,
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
                    color = FloconTheme.colorPalette.exceptions,
                )
                Text(
                    text = t.classPath,
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colorPalette.onSurface,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = FloconTheme.colorPalette.panel,
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
