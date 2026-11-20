package io.github.openflocon.flocondesktop.app.ui.releasenotes

import androidx.navigation3.runtime.EntryProviderScope
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.release_notes_title
import io.github.openflocon.navigation.FloconRoute
import io.github.openflocon.navigation.scene.BigDialogProperties
import io.github.openflocon.navigation.scene.BigDialogSceneStrategy
import kotlinx.serialization.Serializable

sealed interface ReleaseNotesRoutes : FloconRoute {

    @Serializable
    data class Current(val version: String) : ReleaseNotesRoutes
}

fun EntryProviderScope<FloconRoute>.releaseNotesRoutes() {
    entry<ReleaseNotesRoutes.Current>(
        metadata = BigDialogSceneStrategy.bigDialog(BigDialogProperties(Res.string.release_notes_title))
    ) { route ->
        CurrentReleaseNoteScreen(version = route.version)
    }
}
