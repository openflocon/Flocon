package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.database.usecase.favorite.DeleteFavoriteQueryDatabaseUseCase
import io.github.openflocon.domain.database.usecase.favorite.ObserveFavoriteQueriesUseCase
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.mapper.mapToUi
import io.github.openflocon.flocondesktop.features.database.model.DatabaseFavoriteQueryUiModel
import io.github.openflocon.flocondesktop.features.database.model.DatabaseScreenAction
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabViewAction
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class DatabaseViewModel(
    private val databaseSelectorDelegate: DatabaseSelectorDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
    private val observeFavoriteQueriesUseCase: ObserveFavoriteQueriesUseCase,
    private val deleteFavoriteQueryDatabaseUseCase: DeleteFavoriteQueryDatabaseUseCase,
    private val feedbackDisplayer: FeedbackDisplayer,
) : ViewModel(databaseSelectorDelegate) {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> = databaseSelectorDelegate.deviceDataBases

    val favorites = observeFavoriteQueriesUseCase()
        .map { list ->
            list.map { it.mapToUi() }
        }
        .flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList(),
        )

    private val _selectedTab =
        MutableStateFlow<Map<DeviceIdAndPackageNameDomainModel, DatabaseTabState>>(emptyMap())
    val selectedTab = combines(
        observeCurrentDeviceIdAndPackageNameUseCase(),
        _selectedTab,
    ).map { (deviceIdAndPackageName, selectedTab) ->
        selectedTab[deviceIdAndPackageName]
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null,
        )

    private val _tabs =
        MutableStateFlow(emptyMap<DeviceIdAndPackageNameDomainModel, List<DatabaseTabState>>())
    val tabs = combines(
        observeCurrentDeviceIdAndPackageNameUseCase(),
        _tabs,
    ).map { (deviceIdAndPackageName, tabs) ->
        tabs[deviceIdAndPackageName] ?: emptyList()
    }.flowOn(dispatcherProvider.viewModel)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onAction(action: DatabaseScreenAction) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            when (action) {
                is DatabaseScreenAction.DeleteFavorite -> deleteFavorite(
                    favorite = action.favoriteQuery,
                )
                is DatabaseScreenAction.OnDatabaseDoubleClicked -> databaseSelectorDelegate.onDatabaseSelected(
                    action.database.id
                )?.let {
                    createTabForDatabase(action.database)
                }

                is DatabaseScreenAction.OnDatabaseSelected -> databaseSelectorDelegate.onDatabaseSelected(
                    action.id
                )

                is DatabaseScreenAction.OnFavoriteClicked -> onFavoriteClicked(
                    favorite = action.favoriteQuery,
                )

                is DatabaseScreenAction.OnTabAction -> when(val a = action.action) {
                    is DatabaseTabViewAction.OnTabSelected -> onTabSelected(
                        tab = a.tab
                    )
                    is DatabaseTabViewAction.OnCloseClicked -> removeTab(
                        tab = a.tab
                    )
                    is DatabaseTabViewAction.OnCloseAllClicked -> removeAllTab()
                    is DatabaseTabViewAction.OnCloseOnLeftClicked -> removeTabOnLeft(a.tab)
                    is DatabaseTabViewAction.OnCloseOnRightClicked -> removeTabOnRight(a.tab)
                    is DatabaseTabViewAction.OnCloseOtherClicked -> removeTabsExcept(a.tab)
                }


                is DatabaseScreenAction.OnTableColumnClicked -> onTableColumnClicked(
                    column = action.column
                )

                is DatabaseScreenAction.OnTableDoubleClicked -> onTableDoubleClicked(
                    databaseId = action.id,
                    table = action.table
                )

                is DatabaseScreenAction.OnDeleteContentClicked -> createTabForQuery(
                    databaseId = action.databaseId,
                    table = action.table,
                    query = "DELETE FROM ${action.table.name}"
                )

            }
        }
    }

    private suspend fun onTableDoubleClicked(databaseId: DeviceDataBaseId, table: TableUiModel) {
        val generatedName = table.name
        createTab(
            databaseId = databaseId,
            tableName = table.name,
            generatedName = generatedName,
            favoriteId = null,
            query = null,
        )
    }

    private suspend fun createTabForQuery(databaseId: DeviceDataBaseId, table: TableUiModel, query: String) {
        val generatedName = table.name
        createTab(
            databaseId = databaseId,
            tableName = null,
            generatedName = generatedName,
            favoriteId = null,
            query = query,
        )
    }

    private fun onTableColumnClicked(column: TableUiModel.ColumnUiModel) {
        copyToClipboard(column.name)
        feedbackDisplayer.displayMessage("copied: ${column.name}")
    }

    private suspend fun onTabSelected(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        _selectedTab.update { it + (deviceIdAndPackageName to tab) }
    }

    fun onVisible() {
        databaseSelectorDelegate.start()
    }

    fun onNotVisible() {
        databaseSelectorDelegate.stop()
    }

    private fun createTabForDatabase(database: DeviceDataBaseUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            createTab(
                databaseId = database.id,
                tableName = null,
                generatedName = "${database.name}.db",
                favoriteId = null,
                query = null,
            )
        }
    }

    private suspend fun onFavoriteClicked(favorite: DatabaseFavoriteQueryUiModel) {
        createTab(
            databaseId = favorite.databaseId,
            favoriteId = favorite.id,
            generatedName = favorite.title,
            tableName = null,
            query = null,
        )
    }

    private suspend fun deleteFavorite(favorite: DatabaseFavoriteQueryUiModel) {
        deleteFavoriteQueryDatabaseUseCase(
            id = favorite.id,
            databaseId = favorite.databaseId,
        )
    }

    private fun removeAllTab() {
        _tabs.update { emptyMap() }
        _selectedTab.update { emptyMap() }
    }

    private suspend fun removeTabsExcept(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        _tabs.update {
            it.mapValues { (_, list) ->
                list.filter { it.id == tab.id }
            }
        }
        _selectedTab.update {
            it + (deviceIdAndPackageName to tab)
        }
    }

    private suspend fun removeTab(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        var tabIndex = -1
        _tabs.update {
            val list = it[deviceIdAndPackageName] ?: emptyList()
            val newList = list.toMutableList().apply {
                tabIndex = indexOf(tab)
                remove(tab)
            }
            it + (deviceIdAndPackageName to newList)
        }
        if (selectedTab.value == tab) {
            _selectedTab.update {
                val newTab = _tabs.value[deviceIdAndPackageName]?.let {
                    it.getOrNull(tabIndex) ?: it.firstOrNull()
                }
                if (newTab == null) {
                    it - deviceIdAndPackageName
                } else {
                    it + (deviceIdAndPackageName to newTab)
                }
            }
        }
    }

    private suspend fun removeTabOnLeft(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        _tabs.update {
            val list = it[deviceIdAndPackageName] ?: emptyList()
            val newList = list.dropWhile { it != tab }
            it + (deviceIdAndPackageName to newList)
        }

    }

    private suspend fun removeTabOnRight(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        _tabs.update {
            val list = it[deviceIdAndPackageName] ?: emptyList()
            val newList = list.takeWhile { it != tab } + tab
            it + (deviceIdAndPackageName to newList)
        }
    }

    private suspend fun createTab(
        databaseId: DeviceDataBaseId,
        tableName: String?,
        favoriteId: Long?,
        generatedName: String,
        query: String?,
    ) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return

        val list = _tabs.value[deviceIdAndPackageName] ?: emptyList()
        // if we have already a tab with the same "generatedName", it creates a new one with (number) after
        val maxIndexOfSameName =
            list.filter { it.generatedName == generatedName }.maxByOrNull { it.index }

        val index = if (maxIndexOfSameName != null) {
            maxIndexOfSameName.index + 1
        } else {
            0
        }

        val addedTab = DatabaseTabState(
            databaseId = databaseId,
            tableName = tableName,
            generatedName = generatedName,
            index = index,
            favoriteId = favoriteId,
            query = query,
            id = UUID.randomUUID().toString(),
        )

        val newList = list.toMutableList().apply {
            add(addedTab)
        }

        _tabs.update { it + (deviceIdAndPackageName to newList) }
        _selectedTab.update { it + (deviceIdAndPackageName to addedTab) }
    }
}
