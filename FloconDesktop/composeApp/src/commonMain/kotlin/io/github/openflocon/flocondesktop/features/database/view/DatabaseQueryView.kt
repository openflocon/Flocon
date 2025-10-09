package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabAction
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconTextField
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun DatabaseQueryView(
    query: String,
    autoUpdate: Boolean,
    updateQuery: (query: String) -> Unit,
    onAction: (action: DatabaseTabAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(
                color = FloconTheme.colorPalette.primary,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Toopbar(
            onAction = onAction,
            modifier = Modifier.fillMaxWidth(),
            isQueryEmpty = query.isBlank(),
        )
        FloconTextField(
            value = query,
            onValueChange = updateQuery,
            singleLine = false,
            minLines = 5,
            maxLines = 10,
            textStyle = FloconTheme.typography.bodyMedium,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier.fillMaxWidth()
                .onKeyEvent { keyEvent ->
                    // detect CMD + Enter
                    if (keyEvent.type == KeyEventType.KeyDown
                        && keyEvent.key == androidx.compose.ui.input.key.Key.Enter &&
                        (keyEvent.isMetaPressed || keyEvent.isCtrlPressed)
                    ) {
                        onAction(DatabaseTabAction.ExecuteQuery(query))

                        // Return 'true' to indicate that the event was consumed
                        return@onKeyEvent true
                    }
                    return@onKeyEvent false
                }.padding(horizontal = 6.dp, vertical = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            FloconButton(
                onClick = {
                    onAction(DatabaseTabAction.ExecuteQuery(query))
                },
                containerColor = FloconTheme.colorPalette.tertiary,
                modifier = Modifier
                    .padding(all = 8.dp)
            ) {
                val contentColor = FloconTheme.colorPalette.onTertiary
                Row(
                    Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        Icons.Filled.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        colorFilter = ColorFilter.tint(contentColor)
                    )
                    Text("Run Query", color = contentColor)
                }

            }


            Row(
                Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = autoUpdate,
                    onCheckedChange = {
                        onAction(DatabaseTabAction.UpdateAutoUpdate(it))
                    }, colors = CheckboxDefaults.colors(
                        uncheckedColor = FloconTheme.colorPalette.secondary,
                        checkedColor = FloconTheme.colorPalette.secondary,
                    )
                )
                Text(
                    "Auto Update",
                    color = FloconTheme.colorPalette.onPrimary,
                    style = FloconTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun Toopbar(
    onAction: (action: DatabaseTabAction) -> Unit,
    isQueryEmpty: Boolean,
    modifier: Modifier = Modifier,
) {
    var showFavoriteDialog by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .height(40.dp)
            .padding(horizontal = 10.dp)
    ) {
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable(enabled = isQueryEmpty.not()) {
                    showFavoriteDialog = true
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.StarBorder,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
                    .graphicsLayer(
                        alpha = if (isQueryEmpty) 0.6f else 1f
                    ),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp))
                .clickable(enabled = isQueryEmpty.not()) {
                    onAction(DatabaseTabAction.Copy)
                }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.CopyAll,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
                    .graphicsLayer(
                        alpha = if (isQueryEmpty) 0.6f else 1f
                    ),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {

            }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Outlined.History,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }

        /* for another MR
        VerticalDivider(modifier = Modifier.padding(vertical = 6.dp, horizontal = 2.dp))
        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                onAction(DatabaseTabAction.Import)
            }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Outlined.FileOpen,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }

         */
        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier.clip(RoundedCornerShape(2.dp)).clickable {
                onAction(DatabaseTabAction.ClearQuery)
            }.aspectRatio(1f, true),
            contentAlignment = Alignment.Center
        ) {
            Image(
                Icons.Filled.Delete,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(FloconTheme.colorPalette.onPrimary)
            )
        }
    }

    if (showFavoriteDialog) {
        SaveFavoriteDialog(
            onDismiss = { showFavoriteDialog = false },
            onSave = { queryName ->
                onAction(DatabaseTabAction.SaveFavorite(queryName))
                showFavoriteDialog = false
            }
        )
    }
}

@Composable
fun SaveFavoriteDialog(
    onDismiss: () -> Unit, // Callback pour annuler (clic en dehors ou bouton annuler)
    onSave: (queryName: String) -> Unit // Callback pour sauvegarder (bouton Save)
) {
    var favoriteName by remember { mutableStateOf("") }

    FloconDialog(
        onDismissRequest = onDismiss,
    ) {
        Column(modifier = Modifier) {
            Text(
                "Save as favorite",
                modifier = Modifier.fillMaxWidth().background(FloconTheme.colorPalette.primary)
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            )
            FloconTextField(
                value = favoriteName,
                onValueChange = { favoriteName = it },
                placeholder = {
                    Text(
                        "give your favorite a funny name",
                        style = FloconTheme.typography.bodyMedium
                    )
                },
                singleLine = true,
                textStyle = FloconTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                containerColor = FloconTheme.colorPalette.secondary,
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            )
            FloconDialogButtons(
                onCancel = onDismiss,
                onValidate = {
                    if (favoriteName.isNotBlank()) {
                        onSave(favoriteName)
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp
                    )
            )
        }
    }
}

@Preview
@Composable
private fun DatabaseQueryViewPreview() {
    FloconTheme {
        DatabaseQueryView(
            modifier = Modifier.fillMaxWidth(),
            query = "SELECT * FROM TABLE_NAME",
            updateQuery = {},
            autoUpdate = true,
            onAction = {}
        )
    }
}
