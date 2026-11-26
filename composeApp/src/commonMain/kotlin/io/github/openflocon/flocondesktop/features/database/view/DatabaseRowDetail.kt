package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_row_set_null
import flocondesktop.composeapp.generated.resources.database_row_update
import flocondesktop.composeapp.generated.resources.general_copy_all_content
import flocondesktop.composeapp.generated.resources.images_preview
import io.github.openflocon.flocondesktop.features.database.DatabaseRowDetailViewModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowDetailAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowDetailUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseRowEditTargetUiModel
import io.github.openflocon.flocondesktop.features.database.model.previewDatabaseRowDetailUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.common.isImageUrl
import io.github.openflocon.library.designsystem.components.FloconCheckbox
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconTextField
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun DatabaseRowDetail(
    state: DetailResultItem,
    columns: List<String>,
    modifier: Modifier = Modifier,
    editTarget: DatabaseRowEditTargetUiModel? = null,
) {
    val params = DatabaseRowDetailViewModel.Params(
        columns = columns,
        values = state.item.items,
        editTarget = editTarget,
    )
    val viewModel = koinViewModel<DatabaseRowDetailViewModel>(key = params.toString()) {
        parametersOf(params)
    }

    DatabaseRowDetail(
        uiState = viewModel.uiState,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}

@Composable
private fun DatabaseRowDetail(
    uiState: DatabaseRowDetailUiModel,
    onAction: (DatabaseRowDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState: ScrollState = rememberScrollState()

    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)

    Box(modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Row(
                Modifier.fillMaxWidth()
                    .background(FloconTheme.colors.bg000)
                    .padding(horizontal = 1.dp) // to display the inner border
                    .border(1.dp, FloconTheme.colors.bg100)
                    .padding(vertical = FloconTheme.spacing.medium)
                    .padding(horizontal = FloconTheme.spacing.small),
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small, Alignment.End),
                verticalAlignment = Alignment.Top,
            ) {
                if (uiState.isEditable) {
                    DetailHeaderButton(
                        text = stringResource(Res.string.database_row_update),
                        onClick = { onAction(DatabaseRowDetailAction.Update) },
                        imageVector = Icons.Outlined.Save,
                        enabled = uiState.canUpdate,
                    )
                }
                DetailHeaderButton(
                    text = stringResource(Res.string.general_copy_all_content),
                    onClick = { onAction(DatabaseRowDetailAction.CopyAll) },
                    imageVector = Icons.Outlined.CopyAll,
                )
            }

            val fields: @Composable () -> Unit = {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = FloconTheme.spacing.medium, bottom = FloconTheme.spacing.doubleExtraLarge),
                ) {
                    itemsIndexed(
                        items = uiState.fields,
                        key = { index, field -> "$index-${field.column}" },
                    ) { index, field ->
                        DetailItem(
                            field = field,
                            onValueChanged = { onAction(DatabaseRowDetailAction.ValueChanged(index, it)) },
                            onNullChanged = { onAction(DatabaseRowDetailAction.NullChanged(index, it)) },
                            onCopyValue = { onAction(DatabaseRowDetailAction.CopyValue(it)) },
                            modifier = Modifier.fillMaxSize()
                                .padding(horizontal = 18.dp)
                        )

                        if (index != uiState.fields.lastIndex) {
                            FloconHorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = FloconTheme.spacing.small),
                                color = FloconTheme.colors.bg000,
                            )
                        }
                    }
                }
            }

            // text fields handle their own selection
            if (uiState.isEditable) {
                fields()
            } else {
                SelectionContainer(
                    modifier = Modifier.fillMaxSize(),
                    content = fields,
                )
            }
        }

        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}

@Composable
private fun DetailItem(
    field: DatabaseRowDetailUiModel.Field,
    onValueChanged: (String) -> Unit,
    onNullChanged: (Boolean) -> Unit,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val value = field.value
    val contentColor = FloconTheme.colors.ink

    val valueOrNull = value ?: "NULL"
    val valueColor = if (value == null) {
        FloconTheme.colors.ink.copy(alpha = 0.4f)
    } else {
        FloconTheme.colors.ink
    }

    Column(
        modifier = modifier,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Text(
                modifier = Modifier.weight(1f),
                style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                text = field.column,
                color = contentColor,
            )

            Box(
                modifier = Modifier
                    .padding(bottom = FloconTheme.spacing.tiny) // beware of this
                    .size(24.dp)
                    .padding(FloconTheme.spacing.tiny)
                    .clip(FloconTheme.shapes.small)
                    .clickable(enabled = value != null) {
                        value?.let {
                            onCopyValue(it)
                        }
                    }
            ) {
                Image(
                    imageVector = Icons.Outlined.CopyAll,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(valueColor),
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        Spacer(modifier = Modifier.height(FloconTheme.spacing.tiny))

        val isImageUrl = remember(value) {
            value?.isImageUrl() == true
        }

        if (field.isEditable) {
            FloconTextField(
                value = value.orEmpty(),
                onValueChange = onValueChanged,
                placeholder = defaultPlaceHolder("NULL".takeIf { value == null }),
                singleLine = false,
                textStyle = FloconTheme.typography.bodyMedium,
                containerColor = FloconTheme.colors.bg000,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
            ) {
                FloconCheckbox(
                    checked = value == null,
                    onCheckedChange = onNullChanged,
                )
                Text(
                    style = FloconTheme.typography.bodySmall,
                    text = stringResource(Res.string.database_row_set_null),
                    color = contentColor,
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = FloconTheme.spacing.extraSmall)
                    .background(
                        shape = FloconTheme.shapes.medium,
                        color = FloconTheme.colors.bg000,
                    )
                    .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
                style = FloconTheme.typography.bodyMedium,
                text = valueOrNull,
                color = valueColor,
            )
        }

        if (isImageUrl) {
            // Coil image
            AsyncImage(
                model = valueOrNull,
                contentDescription = stringResource(Res.string.images_preview),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(FloconTheme.spacing.extraSmall)
                    .height(300.dp),
                contentScale = ContentScale.Fit,
            )
        }
    }
}

@Composable
private fun DetailHeaderButton(
    text: String,
    imageVector: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = FloconTheme.shapes.large
    Row(
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .clip(shape)
            .border(
                width = 0.5.dp,
                shape = shape,
                color = FloconTheme.colors.ink,
            ).clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = FloconTheme.spacing.small, vertical = FloconTheme.spacing.extraSmall),
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            modifier = Modifier,
            style = FloconTheme.typography.bodySmall,
            text = text,
            color = FloconTheme.colors.ink,
        )
        Box(
            modifier = Modifier
                .size(12.dp)
        ) {
            Image(
                imageVector = imageVector,
                contentDescription = null,
                colorFilter = ColorFilter.tint(FloconTheme.colors.ink),
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Preview
@Composable
private fun DatabaseRowDetailPreview() {
    FloconTheme {
        DatabaseRowDetail(
            uiState = previewDatabaseRowDetailUiModel(),
            onAction = {},
        )
    }
}
