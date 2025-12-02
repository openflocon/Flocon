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
import io.github.openflocon.flocondesktop.features.network.list.view.components.FilterBar
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.DeviceSharedPrefUiModel
import io.github.openflocon.flocondesktop.features.sharedpreferences.model.SharedPrefsStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme

@Composable
fun SharedPreferenceSelectorView(
    modifier: Modifier,
    state: SharedPrefsStateUiModel,
    onSharedPrefSelected: (DeviceSharedPrefUiModel) -> Unit,
) {
    val borderColor = FloconTheme.colorPalette.secondary

    Surface(
        color = FloconTheme.colorPalette.primary,
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
                    .padding(all = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Preferences",
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
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
                            placeholderText = "Filter Preferences",
                            onTextChange = {
                                filterText.value = it
                            },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(bottom = 12.dp),
                        )
                        filtered.preferences.forEach {
                            PreferenceItemView(
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
private fun PreferenceItemView(
    state: DeviceSharedPrefUiModel,
    isSelected: Boolean,
    onSelect: (DeviceSharedPrefUiModel) -> Unit,
    modifier: Modifier
) {
    val (background, textColor) = if (isSelected) {
        FloconTheme.colorPalette.accent.copy(alpha = 0.4f) to FloconTheme.colorPalette.onAccent
    } else {
        Color.Transparent to FloconTheme.colorPalette.onSurface
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(background)
            .clickable(onClick = {
                onSelect(state)
            })
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.width(14.dp),
            imageVector = Icons.Outlined.TableRows,
            contentDescription = null,
            colorFilter = ColorFilter.tint(textColor),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            state.name,
            color = textColor,
            style = FloconTheme.typography.bodyMedium,
        )
    }
}
