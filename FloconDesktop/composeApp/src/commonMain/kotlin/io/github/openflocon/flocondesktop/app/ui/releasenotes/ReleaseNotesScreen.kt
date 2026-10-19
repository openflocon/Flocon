package io.github.openflocon.flocondesktop.app.ui.releasenotes

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.release_notes_error
import flocondesktop.composeapp.generated.resources.release_notes_retry
import flocondesktop.composeapp.generated.resources.release_notes_view_on_github
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconCircularProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconOutlinedButton
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CurrentReleaseNoteScreen(
    version: String,
    modifier: Modifier = Modifier,
) {
    ReleaseNotes(version = version, modifier = modifier)
}

@Composable
fun AllReleaseNotesPane(modifier: Modifier = Modifier) {
    ReleaseNotes(version = null, modifier = modifier)
}

@Composable
private fun ReleaseNotes(
    version: String?,
    modifier: Modifier = Modifier,
) {
    val viewModel: ReleaseNotesViewModel = koinViewModel(
        key = "release_notes_${version ?: "all"}",
        parameters = { parametersOf(ReleaseNotesViewModel.Params(version)) },
    )
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReleaseNotesContent(
        state = state,
        onRetry = viewModel::retry,
        modifier = modifier.fillMaxSize(),
    )
}

@Composable
private fun ReleaseNotesContent(
    state: ReleaseNotesUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        ReleaseNotesUiState.Loading -> Box(modifier, contentAlignment = Alignment.Center) {
            FloconCircularProgressIndicator()
        }

        is ReleaseNotesUiState.Error -> ReleaseNotesError(
            link = state.link,
            onRetry = onRetry,
            modifier = modifier,
        )

        is ReleaseNotesUiState.Content -> LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = modifier,
        ) {
            itemsIndexed(state.notes, key = { _, note -> note.version }) { index, note ->
                if (index > 0) {
                    FloconHorizontalDivider(
                        color = FloconTheme.colorPalette.secondary,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                }
                ReleaseNoteItem(note)
            }
        }
    }
}

@Composable
private fun ReleaseNoteItem(
    note: ReleaseNoteUiModel,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column {
                Text(
                    text = note.title,
                    style = FloconTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = FloconTheme.colorPalette.onPrimary,
                )
                note.date?.let {
                    Text(
                        text = it,
                        style = FloconTheme.typography.bodySmall,
                        color = FloconTheme.colorPalette.onPrimary.copy(alpha = 0.7f),
                    )
                }
            }
            FloconButton(
                onClick = { uriHandler.openUri(note.link) },
                containerColor = FloconTheme.colorPalette.secondary,
            ) {
                Text(stringResource(Res.string.release_notes_view_on_github))
            }
        }
        Markdown(
            content = note.body,
            colors = markdownColor(text = FloconTheme.colorPalette.onPrimary),
        )
    }
}

@Composable
private fun ReleaseNotesError(
    link: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(Res.string.release_notes_error),
            style = FloconTheme.typography.bodyMedium,
            color = FloconTheme.colorPalette.onPrimary,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FloconOutlinedButton(
                onClick = onRetry,
                borderColor = FloconTheme.colorPalette.onPrimary,
            ) {
                Text(stringResource(Res.string.release_notes_retry), color = FloconTheme.colorPalette.onPrimary)
            }
            FloconButton(
                onClick = { uriHandler.openUri(link) },
                containerColor = FloconTheme.colorPalette.secondary,
            ) {
                Text(stringResource(Res.string.release_notes_view_on_github))
            }
        }
    }
}

@Preview
@Composable
private fun ReleaseNotesContentPreview() {
    FloconTheme {
        ReleaseNotesContent(
            state = ReleaseNotesUiState.Content(previewReleaseNotes()),
            onRetry = {},
        )
    }
}
