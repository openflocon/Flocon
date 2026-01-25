package io.github.openflocon.flocondesktop.features.network.list.view.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.RemoveCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import io.github.openflocon.flocondesktop.common.ui.interactions.hover
import io.github.openflocon.flocondesktop.features.network.list.model.header.TextFilterAction
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.TextFilterStateUiModel
import io.github.openflocon.flocondesktop.features.network.list.model.header.columns.base.filter.previewTextFilterState
import io.github.openflocon.library.designsystem.FloconTheme
import io.github.openflocon.library.designsystem.components.FloconIcon
import io.github.openflocon.library.designsystem.components.FloconSmallIconButton
import io.github.openflocon.library.designsystem.components.FloconSwitch
import io.github.openflocon.library.designsystem.components.FloconTextFieldWithoutM3
import io.github.openflocon.library.designsystem.components.defaultPlaceHolder

@Composable
fun TextFilterDropdownContent(
    modifier: Modifier = Modifier,
    filterState: TextFilterStateUiModel,
    textFilterAction: (TextFilterAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TextFilterFieldView(
            submitTextField = { text, toInclude ->
                if (toInclude) {
                    textFilterAction.invoke(TextFilterAction.Include(text))
                } else {
                    textFilterAction.invoke(TextFilterAction.Exclude(text))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
        )

        if (filterState.allFilters.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                if (filterState.includedFilters.isNotEmpty()) {
                    Text(
                        text = "Includes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 6.dp),
                        style = FloconTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.5f),
                        )
                    )

                    filterState.includedFilters.fastForEach {
                        FilterItemView(
                            item = it,
                            modifier = Modifier.fillMaxWidth(),
                            clickDelete = {
                                textFilterAction(TextFilterAction.Delete(it))
                            },
                            changeIsActive = { item, newValue ->
                                textFilterAction(
                                    TextFilterAction.SetIsActive(
                                        item = item,
                                        isActive = newValue,
                                    ),
                                )
                            },
                        )
                    }
                }

                if (filterState.excludedFilters.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Excludes",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 2.dp, horizontal = 6.dp),
                        style = FloconTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FloconTheme.colorPalette.onSurface.copy(alpha = 0.5f),
                        ),
                    )

                    filterState.excludedFilters.fastForEach {
                        FilterItemView(
                            item = it,
                            modifier = Modifier.fillMaxWidth(),
                            clickDelete = {
                                textFilterAction(TextFilterAction.Delete(it))
                            },
                            changeIsActive = { item, newValue ->
                                textFilterAction(
                                    TextFilterAction.SetIsActive(
                                        item = item,
                                        isActive = newValue,
                                    ),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FilterItemView(
    item: TextFilterStateUiModel.FilterItem,
    modifier: Modifier = Modifier,
    changeIsActive: (item: TextFilterStateUiModel.FilterItem, newValue: Boolean) -> Unit,
    clickDelete: (item: TextFilterStateUiModel.FilterItem) -> Unit,
) {
    var isHover by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .hover {
                isHover = it
            }
            .padding(horizontal = 6.dp, vertical = 1.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Box(modifier = Modifier.height(12.dp)) {
            FloconSwitch(
                checked = item.isActive,
                onCheckedChange = { changeIsActive(item, it) }
            )
        }

        Text(
            text = item.text,
            style = FloconTheme.typography.bodySmall,
            modifier = Modifier.weight(1f),
            color = FloconTheme.colorPalette.onSurface,
        )

        val binAlpha by animateFloatAsState(if (isHover) 1f else 0f)
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(32.dp)
                .graphicsLayer {
                    alpha = binAlpha
                }
                .clickable(
                    enabled = isHover,
                    onClick = {
                        clickDelete(item)
                    },
                )
                .padding(5.dp),
        )
    }
}

@Composable
private fun TextFilterFieldView(
    submitTextField: (value: String, toInclude: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FloconTextFieldWithoutM3(
            value = value,
            onValueChange = { value = it },
            keyboardActions = KeyboardActions(
                onDone = {
                    // default action -> add as "include filter"
                    submitTextField(value, true)
                    value = "" // reset
                },
            ),
            placeholder = defaultPlaceHolder("By value", color = FloconTheme.colorPalette.onSecondary.copy(alpha = 0.5f)),
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colorPalette.onSecondary,
            ),
            containerColor = FloconTheme.colorPalette.secondary,
            modifier = Modifier
                .weight(1f)
                .padding(all = 2.dp)
        )
        FloconSmallIconButton(
            enabled = value.isNotEmpty(),
            onClick = {
                submitTextField(value, true)
                value = "" // reset
            }
        ) {
            FloconIcon(imageVector = Icons.Outlined.AddCircle)
        }
        FloconSmallIconButton(
            enabled = value.isNotEmpty(),
            onClick = {
                submitTextField(value, false)
                value = "" // reset
            }
        ) {
            FloconIcon(imageVector = Icons.Outlined.RemoveCircle)
        }
    }
}

@Composable
private fun TextFilterButton(
    onClick: () -> Unit,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)
            .graphicsLayer { alpha = if (enabled) 1f else 0.5f }
            .background(Color.White)
            .clickable(onClick = onClick, enabled = enabled)
            .padding(all = 8.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
private fun TextFilterDropdownContentPreview() {
    FloconTheme {
        TextFilterDropdownContent(
            filterState = previewTextFilterState(),
            textFilterAction = {},
        )
    }
}
