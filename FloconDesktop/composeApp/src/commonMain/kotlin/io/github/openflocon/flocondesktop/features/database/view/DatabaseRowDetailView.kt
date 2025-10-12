package io.github.openflocon.flocondesktop.features.database.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import com.composeunstyled.Text
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconHorizontalDivider
import io.github.openflocon.library.designsystem.components.FloconVerticalScrollbar
import io.github.openflocon.library.designsystem.components.rememberFloconScrollbarAdapter

@Composable
fun DatabaseRowDetailView(
    state: DetailResultItem,
    modifier: Modifier = Modifier,
    columns: List<String>,
) {
    val scrollState: ScrollState = rememberScrollState()

    val scrollAdapter = rememberFloconScrollbarAdapter(scrollState)


    Box(
        modifier
            .background(FloconTheme.colorPalette.primary)
    ) {
        SelectionContainer(
            Modifier.fillMaxSize()
                .verticalScroll(scrollState)
                .padding(vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
            ) {
                columns.fastForEachIndexed { index, column ->
                    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 18.dp)) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            style = FloconTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            text = column,
                            color = FloconTheme.colorPalette.onPrimary
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        state.item.items.getOrNull(index)?.let {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                style = FloconTheme.typography.bodyMedium,
                                text = it,
                                color = FloconTheme.colorPalette.onPrimary
                            )
                        }
                    }

                    if(index != columns.lastIndex) {
                        FloconHorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            color = FloconTheme.colorPalette.surface,
                        )
                    }
                }
            }
        }

        FloconVerticalScrollbar(
            adapter = scrollAdapter,
            modifier = Modifier.fillMaxHeight()
                .align(Alignment.TopEnd)
        )
    }
}
