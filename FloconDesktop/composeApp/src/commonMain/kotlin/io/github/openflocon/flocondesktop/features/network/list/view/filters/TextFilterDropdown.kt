package io.github.openflocon.flocondesktop.features.network.list.view.filters

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.desktop.ui.tooling.preview.Preview
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.filter_excludes
import flocondesktop.composeapp.generated.resources.filter_includes
import flocondesktop.composeapp.generated.resources.general_by_value
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
import org.jetbrains.compose.resources.stringResource

@Composable
fun TextFilterDropdownContent(
    modifier: Modifier = Modifier,
    filterState: TextFilterStateUiModel,
    textFilterAction: (TextFilterAction) -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        TextFilterField(
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
                    .padding(vertical = FloconTheme.spacing.compact)
            ) {
                if (filterState.includedFilters.isNotEmpty()) {
                    Text(
                        text = stringResource(Res.string.filter_includes),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = FloconTheme.spacing.tiny, horizontal = FloconTheme.spacing.compact),
                        style = FloconTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FloconTheme.colors.ink.copy(alpha = 0.5f),
                        )
                    )

                    filterState.includedFilters.fastForEach {
                        FilterEntry(
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
                    Spacer(modifier = Modifier.height(FloconTheme.spacing.compact))

                    Text(
                        text = stringResource(Res.string.filter_excludes),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = FloconTheme.spacing.tiny, horizontal = FloconTheme.spacing.compact),
                        style = FloconTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = FloconTheme.colors.ink.copy(alpha = 0.5f),
                        ),
                    )

                    filterState.excludedFilters.fastForEach {
                        FilterEntry(
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
private fun FilterEntry(
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
            .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny),
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
            color = FloconTheme.colors.ink,
        )

        val binAlpha by animateFloatAsState(if (isHover) 1f else 0f)
        Icon(
            imageVector = Icons.Filled.Delete,
            contentDescription = null,
            tint = FloconTheme.colors.inkMuted,
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
                .padding(FloconTheme.spacing.compact),
        )
    }
}

@Composable
private fun TextFilterField(
    submitTextField: (value: String, toInclude: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    var value by remember { mutableStateOf("") }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall),
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
            placeholder = defaultPlaceHolder(stringResource(Res.string.general_by_value), color = FloconTheme.colors.inkMuted.copy(alpha = 0.5f)),
            textStyle = FloconTheme.typography.bodySmall.copy(
                color = FloconTheme.colors.inkMuted,
            ),
            containerColor = FloconTheme.colors.floating,
            modifier = Modifier
                .weight(1f)
                .padding(all = FloconTheme.spacing.tiny)
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
            .background(FloconTheme.colors.floating)
            .clickable(onClick = onClick, enabled = enabled)
            .padding(all = FloconTheme.spacing.small),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = FloconTheme.colors.ink,
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
