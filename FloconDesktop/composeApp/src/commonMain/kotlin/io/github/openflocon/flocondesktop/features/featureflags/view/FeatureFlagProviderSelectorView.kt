package io.github.openflocon.flocondesktop.features.featureflags.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.no_feature_flags
import io.github.openflocon.flocondesktop.features.featureflags.model.FeatureFlagProvidersUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.DropdownFilterFieldView
import io.github.openflocon.library.designsystem.components.FloconButton
import io.github.openflocon.library.designsystem.components.FloconDropdownMenu
import io.github.openflocon.library.designsystem.components.FloconDropdownMenuItem
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconLinearProgressIndicator
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun FeatureFlagProviderSelectorView(
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
            FloconButton(
                onClick = {
                    if (providersState is FeatureFlagProvidersUiModel.WithContent) {
                        expanded = true
                    }
                },
                containerColor = FloconTheme.colors.bg200
            ) {
                when (providersState) {
                    FeatureFlagProvidersUiModel.Empty ->
                        Text(
                            stringResource(Res.string.no_feature_flags),
                            style = FloconTheme.typography.bodySmall
                        )

                    FeatureFlagProvidersUiModel.Loading -> FloconLinearProgressIndicator()

                    is FeatureFlagProvidersUiModel.WithContent -> {
                        Text(text = providersState.selected, style = FloconTheme.typography.bodySmall)
                        FloconIcon(
                            imageVector = Icons.Outlined.KeyboardArrowDown,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        },
        modifier = modifier
    ) {
        var filterText by remember { mutableStateOf("") }
        DropdownFilterFieldView(
            value = filterText,
            onValueChanged = {
                filterText = it
            },
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
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
