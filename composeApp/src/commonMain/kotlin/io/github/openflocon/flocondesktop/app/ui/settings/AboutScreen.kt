package io.github.openflocon.flocondesktop.app.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.LibraryDefaults
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.m3.chipColors
import com.mikepenz.aboutlibraries.ui.compose.m3.libraryColors
import com.mikepenz.aboutlibraries.ui.compose.produceLibraries
import flocondesktop.composeapp.generated.resources.Res
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.toImmutableList

@Composable
fun AboutScreen(modifier: Modifier = Modifier) {
    // Multiplatform: Manually loading JSON
    val libraries by produceLibraries {
        // Replace with your specific resource loading logic
        Res.readBytes("files/aboutlibraries.json").decodeToString()
    }

    LibrariesContainer(
        libraries = libraries?.distinct(),
        colors = LibraryDefaults.libraryColors(
            libraryBackgroundColor = FloconTheme.colors.bg000,
            libraryContentColor = FloconTheme.colors.ink,
            versionChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colors.bg100,
                contentColor = FloconTheme.colors.ink
            ),
            licenseChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colors.bg200,
                contentColor = FloconTheme.colors.inkMuted
            ),
            fundingChipColors = LibraryDefaults.chipColors(
                containerColor = FloconTheme.colors.ink,
                contentColor = FloconTheme.colors.bg100
            ),
            dialogConfirmButtonColor = FloconTheme.colors.ink
        ),
        modifier = modifier,
    )
}

private fun Libs.distinct() = this.copy(
    libraries = this.libraries.distinctBy { it.name + it.organization }.toImmutableList(),
)
