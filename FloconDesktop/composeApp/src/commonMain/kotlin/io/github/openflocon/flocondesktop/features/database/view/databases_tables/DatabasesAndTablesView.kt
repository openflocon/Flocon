package io.github.openflocon.flocondesktop.features.database.view.databases_tables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Surface
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.composeunstyled.Text
import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.databases_favorites
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.library.designsystem.FloconTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun DatabasesAndTablesView(
    modifier: Modifier = Modifier,
    state: DatabasesStateUiModel,
    favorites: List<DatabaseFavoriteQueryUiModel>,
    onAction: (DatabaseScreenAction) -> Unit,
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
                    "Databases",
                    color = FloconTheme.colorPalette.onSurface,
                    style = FloconTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
                when (state) {
                    DatabasesStateUiModel.Empty -> Unit
                    DatabasesStateUiModel.Loading -> Unit
                    is DatabasesStateUiModel.WithContent -> {
                        state.databases.forEach {
                            DatabaseItemView(
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
                        .padding(all = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        stringResource(Res.string.databases_favorites),
                        color = FloconTheme.colorPalette.onSurface,
                        style = FloconTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )

                    favorites.forEach {
                        FavoriteQueryItemView(
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
