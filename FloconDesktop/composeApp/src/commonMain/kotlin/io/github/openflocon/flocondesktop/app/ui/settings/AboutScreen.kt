package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import flocondesktop.composeapp.generated.resources.Res
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    // Multiplatform: Manually loading JSON
    val libraries by rememberLibraries {
        // Replace with your specific resource loading logic
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    LibrariesContainer(
        libraries = libraries?.distinct(),
        colors = LibraryDefaults.libraryColors(
            backgroundColor = FloconTheme.colorPalette.surface,
            contentColor = FloconTheme.colorPalette.onSurface,
            versionChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colorPalette.primary,
                contentColor = FloconTheme.colorPalette.onPrimary,
            ),
            licenseChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colorPalette.secondary,
                contentColor = FloconTheme.colorPalette.onSecondary,
            ),
            fundingChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colorPalette.tertiary,
                contentColor = FloconTheme.colorPalette.onTertiary,
            ),
            dialogConfirmButtonColor = FloconTheme.colorPalette.onSurface,
        ),
        modifier = modifier,
    )
}

private fun Libs.distinct() = this.copy(
    libraries = this.libraries.distinctBy { it.name + it.organization }.toImmutableList(),
)
