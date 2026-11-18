package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.preference_cancel
import flocondesktop.composeapp.generated.resources.preference_delete
import flocondesktop.composeapp.generated.resources.preference_delete_message
import flocondesktop.composeapp.generated.resources.preference_delete_title
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDialog
import io.github.openflocon.library.designsystem.components.FloconDialogHeader
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedPreferenceDeleteDialog(
    key: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloconDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
    ) {
        SharedPreferenceDeleteContent(
            key = key,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
        )
    }
}

@Composable
private fun SharedPreferenceDeleteContent(
    key: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    Column {
        FloconDialogHeader(
            title = stringResource(Res.string.preference_delete_title, key),
            modifier = Modifier.fillMaxWidth(),
        )
        Column(
            verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.medium),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = FloconTheme.spacing.large)
                .padding(bottom = FloconTheme.spacing.medium),
        ) {
            Text(
                text = stringResource(Res.string.preference_delete_message),
                style = FloconTheme.typography.bodyMedium,
                color = FloconTheme.colors.inkMuted,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FloconOutlinedButton(onClick = onDismiss) {
                    Text(stringResource(Res.string.preference_cancel))
                }
                FloconButton(
                    onClick = onConfirm,
                    containerColor = FloconTheme.colors.statusError,
                ) {
                    Text(stringResource(Res.string.preference_delete))
                }
            }
        }
    }
}

@Preview
@Composable
private fun SharedPreferenceDeleteContentPreview() {
    FloconTheme {
        SharedPreferenceDeleteContent(
            key = "user_token",
            onConfirm = {},
            onDismiss = {},
        )
    }
}
