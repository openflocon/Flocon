package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DatabaseFavoriteQueryDomainModel
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
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.DeviceDataBaseUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.flocondesktop.features.database.model.selectedDatabase
import io.github.openflocon.library.designsystem.common.copyToClipboard
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.emptyList

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
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
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
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
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
            started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            databaseSelectorDelegate.deviceDataBases.collect { databases ->
                getCurrentDeviceIdAndPackageNameUseCase()?.let {
                    databases.selectedDatabase()?.let {
                        createTabForDatabase(it)
                    }
                }
            }
        }
    }

    fun onDatabaseSelected(databaseId: DeviceDataBaseId) {
        databaseSelectorDelegate.onDatabaseSelected(databaseId)
    }

    fun onDatabaseDoubleClicked(database: DeviceDataBaseUiModel) {
        databaseSelectorDelegate.onDatabaseSelected(database.id)

        createTabForDatabase(database)
    }

    fun onTableDoubleClicked(databaseId: DeviceDataBaseId, table: TableUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val generatedName = table.name
            createTab(
                databaseId = databaseId,
                tableName = table.name,
                generatedName = generatedName,
                favoriteId = null,
            )
        }
    }

    fun onTableColumnClicked(column: TableUiModel.ColumnUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            copyToClipboard(column.name)
            feedbackDisplayer.displayMessage("copied: ${column.name}")
        }
    }

    fun onTabSelected(tab: DatabaseTabState) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return@launch
            _selectedTab.update { it + (deviceIdAndPackageName to tab) }
        }
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
            )
        }
    }

    fun onTabCloseClicked(tab: DatabaseTabState) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeTab(tab)
        }
    }

    fun onFavoriteClicked(favorite: DatabaseFavoriteQueryUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            createTab(
                databaseId = favorite.databaseId,
                favoriteId = favorite.id,
                generatedName = favorite.title,
                tableName = null,
            )
        }
    }

    fun deleteFavorite(favorite: DatabaseFavoriteQueryUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            deleteFavoriteQueryDatabaseUseCase(
                id = favorite.id,
                databaseId = favorite.databaseId,
            )
        }
    }

    private suspend fun removeTab(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        _tabs.update {
            val list = it[deviceIdAndPackageName] ?: emptyList()
            val newList = list.toMutableList().apply {
                remove(tab)
            }
            it + (deviceIdAndPackageName to newList)
        }
        if (selectedTab.value == tab) {
            _selectedTab.update {
                val newTab = _tabs.value[deviceIdAndPackageName]?.firstOrNull()
                if (newTab == null) {
                    it - deviceIdAndPackageName
                } else {
                    it + (deviceIdAndPackageName to newTab)
                }
            }
        }
    }

    private suspend fun createTab(
        databaseId: DeviceDataBaseId,
        tableName: String?,
        favoriteId: Long?,
        generatedName: String,
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
            id = UUID.randomUUID().toString(),
        )

        val newList = list.toMutableList().apply {
            add(addedTab)
        }

        _tabs.update { it + (deviceIdAndPackageName to newList) }
        _selectedTab.update { it + (deviceIdAndPackageName to addedTab) }
    }
}
