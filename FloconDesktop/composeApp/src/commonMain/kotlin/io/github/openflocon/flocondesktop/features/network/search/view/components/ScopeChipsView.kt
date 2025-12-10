package io.github.openflocon.flocondesktop.features.network.search.view.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.openflocon.domain.network.models.SearchScope
import io.github.openflocon.domain.network.models.text
import io.github.openflocon.flocondesktop.features.network.search.model.NetworkSearchUiState
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
internal fun ScopeChipsView(
    uiState: NetworkSearchUiState,
    scope: SearchScope,
    onScopeToggled: (SearchScope) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = remember(uiState, scope) { uiState.selectedScopes.contains(scope) }
    val color by animateColorAsState(if (isSelected) FloconTheme.colorPalette.tertiary else Color.Transparent)
    val textColor by animateColorAsState(
        if (isSelected) FloconTheme.colorPalette.onTertiary else FloconTheme.colorPalette.onSurface.copy(
            alpha = 0.5f
        )
    )
    Box(
        modifier = modifier
            .clip(FloconTheme.shapes.large)
            .clickable { onScopeToggled(scope) }
            .background(color = color)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = scope.text(),
            style = FloconTheme.typography.bodySmall,
            color = textColor,
        )
    }
}
