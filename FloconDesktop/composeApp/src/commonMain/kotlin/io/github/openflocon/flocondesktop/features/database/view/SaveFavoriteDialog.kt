package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.input.KeyboardType
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogButtons
import io.github.openflocon.library.designsystem.components.FloconTextField
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SaveFavoriteDialog(
    favoritesTitles: ImmutableSet<String>,
    onDismiss: () -> Unit,
    onSave: (queryName: String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var favoriteName by remember { mutableStateOf("") }

    val focusRequester = remember { FocusRequester() }

    FloconDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        Column {
            Text(
                "Save as favorite",
                modifier = Modifier.fillMaxWidth().background(FloconTheme.colors.floating)
                    .padding(
                        horizontal = FloconTheme.spacing.large,
                        vertical = FloconTheme.spacing.medium
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
                maxLines = 1,
                textStyle = FloconTheme.typography.bodyMedium,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                containerColor = FloconTheme.colors.floating,
                modifier = Modifier.fillMaxWidth()
                    .focusRequester(focusRequester)
                    .padding(
                        horizontal = FloconTheme.spacing.large,
                        vertical = FloconTheme.spacing.medium
                    ).onKeyEvent { keyEvent ->
                        // detect CMD + Enter
                        if (keyEvent.type == KeyEventType.KeyDown &&
                            keyEvent.key == androidx.compose.ui.input.key.Key.Enter
                        ) {
                            if (favoriteName.isNotBlank() && (favoriteName !in favoritesTitles)) {
                                onSave(favoriteName)
                            }

                            // Return 'true' to indicate that the event was consumed
                            return@onKeyEvent true
                        }
                        return@onKeyEvent false
                    }
            )
            FloconDialogButtons(
                onCancel = onDismiss,
                onValidate = {
                    if (favoriteName.isNotBlank() && (favoriteName !in favoritesTitles)) {
                        onSave(favoriteName)
                    }
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(
                        horizontal = FloconTheme.spacing.large,
                        vertical = FloconTheme.spacing.medium
                    )
            )
        }
    }
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
