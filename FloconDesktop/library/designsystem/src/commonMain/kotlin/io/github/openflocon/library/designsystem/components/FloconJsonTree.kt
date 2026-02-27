package io.github.openflocon.library.designsystem.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.sebastianneubauer.jsontree.JsonTree
import com.sebastianneubauer.jsontree.TreeState
import com.sebastianneubauer.jsontree.defaultDarkColors
import com.sebastianneubauer.jsontree.search.SearchState
import com.sebastianneubauer.jsontree.search.rememberSearchState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun FloconJsonTree(
    json: String,
    modifier: Modifier = Modifier,
    initialState: TreeState = TreeState.FIRST_ITEM_EXPANDED,
    textStyle: TextStyle = FloconTheme.typography.bodyMedium,
    onError: (Throwable) -> Unit = {},
    searchState: SearchState = rememberSearchState(),
    lazyListState: LazyListState = rememberLazyListState()
) {
    SelectionContainer(modifier = modifier) {
        JsonTree(
            json = json,
            onLoading = {
                FloconCircularProgressIndicator() // TODO Better?
            },
            initialState = initialState,
            icon = Icons.Outlined.ChevronRight,
            searchState = searchState,
            lazyListState = lazyListState,
            colors = defaultDarkColors,
            textStyle = textStyle,
            onError = onError,
            modifier = Modifier.fillMaxSize()
        )
    }
}
