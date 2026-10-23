package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.databases_favorites
import flocondesktop.composeapp.generated.resources.databases_title
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDoc
import io.github.openflocon.flocondesktop.common.ui.featuredoc.FeatureDocButton
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabasesAndTables(
    state: DatabasesStateUiModel,
    favorites: ImmutableList<DatabaseFavoriteQueryUiModel>,
    modifier: Modifier = Modifier,
    onAction: (DatabaseScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = FloconTheme.colors.line

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
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            stringResource(Res.string.databases_title),
                            color = FloconTheme.colors.ink,
                            style = FloconTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (state is DatabasesStateUiModel.WithContent) {
                            Text(
                                state.databases.size.toString(),
                                color = FloconTheme.colors.inkMuted,
                                style = FloconTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                modifier = Modifier
                                    .background(FloconTheme.colors.bg300, CircleShape)
                                    .padding(horizontal = FloconTheme.spacing.compact, vertical = FloconTheme.spacing.tiny),
                            )
                        }
                    }
                    FeatureDocButton(doc = FeatureDoc.Database)
                }
                when (state) {
                    DatabasesStateUiModel.Empty -> Unit

                    DatabasesStateUiModel.Loading -> Unit

                    is DatabasesStateUiModel.WithContent -> {
                        state.databases.forEach {
                            DatabaseItem(
                                state = it,
                                onSelect = {
                                    onAction(DatabaseScreenAction.OnDatabaseSelected(it))
                                },
                                modifier = Modifier.fillMaxWidth(),
                                onTableDoubleClicked = { id, table ->
                                    onAction(DatabaseScreenAction.OnTableDoubleClicked(id, table))
                                },
                                onDatabaseDoubleClicked = {
                                    onAction(DatabaseScreenAction.OnDatabaseDoubleClicked(it))
                                },
                                onTableColumnClicked = {
                                    onAction(DatabaseScreenAction.OnTableColumnClicked(it))
                                },
                                onDeleteContentClicked = { id, table ->
                                    onAction(DatabaseScreenAction.OnDeleteContentClicked(id, table))
                                },
                                onInsertContentClicked = { id, table ->
                                    onAction(DatabaseScreenAction.OnInsertContentClicked(id, table))
                                },
                                onSeeAllQueriesClicked = { id, dbName ->
                                    onAction(DatabaseScreenAction.OnSeeAllQueriesClicked(id, dbName))
                                }
                            )
                        }
                    }
                }
            }
            favorites.takeIf { it.isNotEmpty() }?.let {
                HorizontalDivider(color = borderColor)
                Column(
                    Modifier.fillMaxWidth()
                        .weight(0.4f)
                        .verticalScroll(rememberScrollState())
                        .padding(all = FloconTheme.spacing.extraSmall),
                    verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.extraSmall)
                ) {
                    Text(
                        stringResource(Res.string.databases_favorites),
                        color = FloconTheme.colors.ink,
                        style = FloconTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = FloconTheme.spacing.medium, vertical = FloconTheme.spacing.small)
                    )

                    favorites.forEach {
                        FavoriteQueryItem(
                            state = it,
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onAction(DatabaseScreenAction.OnFavoriteClicked(it))
                            },
                            onDelete = {
                                onAction(DatabaseScreenAction.DeleteFavorite(it))
                            },
                        )
                    }
                }
            }
        }
    }
}
