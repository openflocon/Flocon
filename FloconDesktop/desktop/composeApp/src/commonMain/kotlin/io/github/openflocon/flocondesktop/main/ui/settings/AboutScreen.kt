package io.github.openflocon.flocondesktop.main.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.ui.compose.m3.LibrariesContainer
import com.mikepenz.aboutlibraries.ui.compose.rememberLibraries
import flocondesktop.desktop.composeapp.generated.resources.Res
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
        modifier = modifier,
    )
}

private fun Libs.distinct() = this.copy(
    libraries = this.libraries.distinctBy { it.name + it.organization }.toImmutableList(),
)
