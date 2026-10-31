package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_sidebar_title
import flocondesktop.composeapp.generated.resources.databases_favorites
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.filterByQuery
import io.github.openflocon.library.designsystem.FloconTheme
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabasesAndTables(
    state: DatabasesStateUiModel,
    filter: String,
    favorites: ImmutableList<DatabaseFavoriteQueryUiModel>,
    onAction: (DatabaseScreenAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = FloconTheme.colors.line

    Column(modifier) {
        Column(
            Modifier.fillMaxSize()
        ) {
            Column(
                Modifier.fillMaxWidth()
                    .padding(all = FloconTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.small),
            ) {
                Text(
                    stringResource(Res.string.database_sidebar_title),
                    color = FloconTheme.colors.inkFaint,
                    style = FloconTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.8.sp,
                    ),
                    modifier = Modifier.padding(horizontal = FloconTheme.spacing.extraSmall),
                )
            }
            Column(
                Modifier.fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = FloconTheme.spacing.small)
                    .padding(bottom = FloconTheme.spacing.small),
                verticalArrangement = Arrangement.spacedBy(FloconTheme.spacing.tiny)
            ) {
                when (state) {
                    DatabasesStateUiModel.Empty -> Unit

                    DatabasesStateUiModel.Loading -> Unit

                    is DatabasesStateUiModel.WithContent -> {
                        val databases = remember(state.databases, filter) {
                            state.databases.filterByQuery(filter)
                        }
                        databases.forEach {
                            DatabaseItem(
                                state = it,
                                isFiltering = filter.isNotBlank(),
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
