package io.github.openflocon.library.designsystem.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sebastianneubauer.jsontree.JsonTree
import com.sebastianneubauer.jsontree.defaultDarkColors
import com.sebastianneubauer.jsontree.search.SearchState
import com.sebastianneubauer.jsontree.search.rememberSearchState

@Composable
fun FloconJsonTree(
    json: String,
    modifier: Modifier = Modifier,
    searchState: SearchState = rememberSearchState()
) {
    JsonTree(
        json = json,
        onLoading = {
            FloconCircularProgressIndicator() // TODO Better?
        },
        icon = Icons.Outlined.ChevronLeft,
        searchState = searchState,
        colors = defaultDarkColors,
        modifier = modifier
    )
}
