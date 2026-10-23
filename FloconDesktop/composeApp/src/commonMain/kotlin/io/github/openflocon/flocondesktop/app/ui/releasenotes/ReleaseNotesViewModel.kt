package io.github.openflocon.flocondesktop.app.ui.releasenotes

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.versions.model.ReleaseNoteDomainModel
import io.github.openflocon.domain.versions.usecase.GetReleaseNoteUseCase
import io.github.openflocon.domain.versions.usecase.GetReleaseNotesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Immutable
data class ReleaseNoteUiModel(
    val version: String,
    val title: String,
    val body: String,
    val date: String?,
    val link: String,
)

@Immutable
sealed interface ReleaseNotesUiState {
    data object Loading : ReleaseNotesUiState
    data class Content(val notes: ImmutableList<ReleaseNoteUiModel>) : ReleaseNotesUiState
    data class Error(val link: String) : ReleaseNotesUiState
}

class ReleaseNotesViewModel(
    private val params: Params,
    private val getReleaseNotesUseCase: GetReleaseNotesUseCase,
    private val getReleaseNoteUseCase: GetReleaseNoteUseCase,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel() {

    /** [version] null means every release. */
    data class Params(val version: String?)

    private val _state = MutableStateFlow<ReleaseNotesUiState>(ReleaseNotesUiState.Loading)
    val state: StateFlow<ReleaseNotesUiState> = _state.asStateFlow()

    init {
        load()
    }

    fun retry() {
        _state.value = ReleaseNotesUiState.Loading
        load()
    }

    private fun load() {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val version = params.version
            val result = if (version == null) {
                getReleaseNotesUseCase()
            } else {
                getReleaseNoteUseCase(version).mapSuccess { listOfNotNull(it) }
            }
            _state.value = result.fold(
                doOnFailure = { ReleaseNotesUiState.Error(link = errorLink()) },
                doOnSuccess = { notes ->
                    if (notes.isEmpty()) {
                        ReleaseNotesUiState.Error(link = errorLink())
                    } else {
                        ReleaseNotesUiState.Content(notes.map { it.toUi() }.toImmutableList())
                    }
                },
            )
        }
    }

    private fun errorLink(): String = params.version
        ?.let { "$RELEASES_URL/tag/$it" }
        ?: RELEASES_URL

    private fun ReleaseNoteDomainModel.toUi() = ReleaseNoteUiModel(
        version = version,
        title = title,
        body = body,
        date = publishedAt,
        link = link,
    )

    private companion object {
        const val RELEASES_URL = "https://github.com/openflocon/Flocon/releases"
    }
}

internal fun previewReleaseNotes() = persistentListOf(
    ReleaseNoteUiModel(
        version = "2.0.0",
        title = "Flocon 2.0.0",
        body = "## Features\n- Release notes dialog\n- New settings tab\n\n## Fixes\n- Export CSV directory",
        date = "2026-10-19",
        link = "https://github.com/openflocon/Flocon/releases/tag/2.0.0",
    ),
)
