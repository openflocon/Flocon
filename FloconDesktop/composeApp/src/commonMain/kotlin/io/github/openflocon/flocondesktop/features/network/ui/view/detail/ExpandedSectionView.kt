package io.github.openflocon.flocondesktop.features.network.ui.view.detail

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandedSectionView(
    modifier: Modifier = Modifier,
    isExpanded: Boolean,
    contentPadding: PaddingValues = PaddingValues(start = 12.dp),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier =
        modifier
            .animateContentSize() // Animate height changes
            .then(
                if (isExpanded) {
                    Modifier.wrapContentHeight() // Prend la hauteur de son contenu
                } else {
                    Modifier.height(0.dp) // Hauteur 0 quand replié
                },
            ).padding(contentPadding),
    ) {
        content()
    }
}
