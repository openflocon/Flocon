package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.feature_flags_selector_caption
import flocondesktop.composeapp.generated.resources.no_feature_flags
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagProvidersUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterField
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import io.github.openflocon.library.designsystem.components.FloconPageSelector
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeatureFlagProviderSelector(
    providersState: FeatureFlagProvidersUiModel,
    onProviderSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    FloconDropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false },
        onExpandRequest = {
            if (providersState is FeatureFlagProvidersUiModel.WithContent) {
                expanded = true
            }
        },
        anchorContent = {
            val onClick = {
                if (providersState is FeatureFlagProvidersUiModel.WithContent) {
                    expanded = true
                }
            }
            when (providersState) {
                FeatureFlagProvidersUiModel.Empty -> Unit

                FeatureFlagProvidersUiModel.Loading ->
                    FloconButton(onClick = onClick, containerColor = FloconTheme.colors.bg200) {
                        FloconLinearProgressIndicator()
                    }

                is FeatureFlagProvidersUiModel.WithContent -> FloconPageSelector(
                    icon = Icons.Outlined.Flag,
                    caption = stringResource(Res.string.feature_flags_selector_caption),
                    value = providersState.selected,
                    onClick = onClick,
                )
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterField(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            if (providersState is FeatureFlagProvidersUiModel.WithContent) {
                val filteredItems = remember(filterText, providersState.providers) {
                    providersState.providers.filter {
                        it.contains(filterText, ignoreCase = true)
                    }
                }

                filteredItems.fastForEach { provider ->
                    FloconDropdownMenuItem(
                        text = provider,
                        onClick = {
                            onProviderSelected(provider)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
