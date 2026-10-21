package io.github.openflocon.flocondesktop.features.network.badquality.edition.view

import io.github.openflocon.library.designsystem.components.FloconActionButton
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.draw.drawBehind
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.BadQualityConfigUiModel
import io.github.openflocon.flocondesktop.features.network.badquality.edition.model.possibleExceptions
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.badquality_add_exception
import flocondesktop.composeapp.generated.resources.badquality_add_http_error
import flocondesktop.composeapp.generated.resources.badquality_delete_error
import flocondesktop.composeapp.generated.resources.badquality_no_error

@Composable
fun BadQualityErrorsListView(
    errors: ImmutableList<BadQualityConfigUiModel.Error>,
    onErrorslicked: (error: BadQualityConfigUiModel.Error) -> Unit,
    deleteError: (error: BadQualityConfigUiModel.Error) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            modifier = Modifier.fillMaxWidth().padding(horizontal = FloconTheme.spacing.medium),
        ) {
            Text(
                "Errors",
                style = FloconTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = FloconTheme.colors.inkMuted,
                modifier = Modifier.weight(1f),
            )
            FloconActionButton(
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
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Text(
                    text = stringResource(Res.string.badquality_add_http_error),
                )
            }
            FloconActionButton(
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
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                Text(
                    text = stringResource(Res.string.badquality_add_exception),
                )
            }
        }

        if (errors.isEmpty()) {
            val dash = FloconTheme.colors.lineStrong
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = FloconTheme.spacing.medium)
                    .padding(top = FloconTheme.spacing.medium)
                    .height(160.dp)
                    .drawBehind {
                        drawRoundRect(
                            color = dash,
                            cornerRadius = CornerRadius(8.dp.toPx()),
                            style = Stroke(width = 1.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(4.dp.toPx(), 4.dp.toPx()))),
                        )
                    }
                    .padding(FloconTheme.spacing.medium),
            ) {
                Text(
                    text = stringResource(Res.string.badquality_no_error),
                    style = FloconTheme.typography.bodySmall,
                    color = FloconTheme.colors.inkFaint,
                    textAlign = TextAlign.Center,
                )
            }
        }

        if (errors.isNotEmpty()) {
            val lazyListState = rememberLazyListState()
            val scrollAdapter = rememberFloconScrollbarAdapter(lazyListState)
            Box(modifier = Modifier.height(160.dp)) {
                val contentPadding = PaddingValues(horizontal = FloconTheme.spacing.medium)
                LazyColumn(
                    state = lazyListState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = FloconTheme.spacing.medium),
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
                ) {
                    if (errors.isNotEmpty()) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = FloconTheme.spacing.compact)
                            ) {
                                Text(
                                    "Weight",
                                    style = FloconTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Thin,
                                        fontSize = FloconTheme.typography.labelSmall.fontSize,
                                    ),
                                )
                                Spacer(modifier = Modifier.width(28.dp))
                                Text(
                                    "Type",
                                    style = FloconTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Thin,
                                        fontSize = FloconTheme.typography.labelSmall.fontSize,
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
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
    ) {
        Box(
            modifier = Modifier.size(24.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(FloconTheme.colors.bg300),
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
                ),
                color = FloconTheme.colors.ink,
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
                    color = FloconTheme.colors.statusWarning,
                )
                Text(
                    t.body.take(20),
                    maxLines = 1,
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colors.ink,
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.extraSmall),
                )
            }

            is BadQualityConfigUiModel.Error.Type.Exception -> {
                Text(
                    "exception",
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colors.ink,
                        fontWeight = FontWeight.Bold,
                    ),
                    color = FloconTheme.colors.statusError
                )
                Text(
                    text = t.classPath,
                    style = FloconTheme.typography.bodySmall.copy(
                        color = FloconTheme.colors.ink,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            color = FloconTheme.colors.bg200,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = FloconTheme.spacing.extraSmall, vertical = FloconTheme.spacing.extraSmall),
                )
            }
        }

        Image(
            imageVector = Icons.Default.Delete,
            contentDescription = stringResource(Res.string.badquality_delete_error),
            modifier = Modifier
                .size(24.dp)
                .clickable {
                    deleteError(
                        error,
                    )
                },
            colorFilter = ColorFilter.tint(FloconTheme.colors.ink),
        )
    }
}
