package io.github.openflocon.flocondesktop.features.sharedpreferences.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TableRows
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter
import flocondesktop.composeapp.generated.resources.preferences
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun SharedPreferenceSelector(
    state: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = FloconTheme.colors.bg200

    Surface(
        color = FloconTheme.colors.bg100,
        modifier = modifier
            .clip(FloconTheme.shapes.medium)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = FloconTheme.shapes.medium
            )
    ) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(all = FloconTheme.spacing.extraSmall),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        stringResource(Res.string.preferences),
                        color = FloconTheme.colors.ink,
                        style = FloconTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small)
                    )
                    FeatureDocButton(doc = FeatureDoc.SharedPreferences)
                }
                when (state) {
                    SharedPrefsStateUiModel.Empty -> Unit

                    SharedPrefsStateUiModel.Loading -> Unit

                    is SharedPrefsStateUiModel.WithContent -> {
                        val filterText = remember { mutableStateOf("") }
                        val filtered = remember(filterText.value, state) {
                            if (filterText.value.isBlank()) {
                                state
                            } else {
                                state.copy(
                                    preferences = state.preferences.filter {
                                        it.name.contains(filterText.value, ignoreCase = true)
                                    }
                                )
                            }
                        }
                        FilterBar(
                            filterText = filterText,
                            placeholderText = stringResource(Res.string.filter),
                            onTextChange = {
                                filterText.value = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = FloconTheme.spacing.medium).padding(bottom = FloconTheme.spacing.medium),
                        )
                        filtered.preferences.forEach {
                            PreferenceItem(
                                state = it,
                                onSelect = {
                                    onSharedPrefSelected(it)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                isSelected = it.id == state.selected.id
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PreferenceItem(
    state: DeviceSharedPrefUiModel,
    isSelected: Boolean,
    onSelect: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val (background, textColor) = if (isSelected) {
        FloconTheme.colors.accent.copy(alpha = 0.4f) to FloconTheme.colors.accent
    } else {
        Color.Transparent to FloconTheme.colors.ink
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = {
                onSelect(state)
            })
            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Outlined.TableRows,
            contentDescription = null,
            colorFilter = ColorFilter.tint(textColor),
        )
        Spacer(modifier = Modifier.width(FloconTheme.spacing.extraSmall))
        Text(
            state.name,
            color = textColor,
            style = FloconTheme.typography.bodyMedium,
        )
    }
}
