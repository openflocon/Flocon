package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.common.combines
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.domain.device.usecase.ObserveCurrentDeviceIdAndPackageNameUseCase
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.flocondesktop.features.database.model.selectedDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseSelectorDelegate: DatabaseSelectorDelegate,
    private val dispatcherProvider: DispatcherProvider,
    private val observeCurrentDeviceIdAndPackageNameUseCase: ObserveCurrentDeviceIdAndPackageNameUseCase,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) : ViewModel(databaseSelectorDelegate) {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> = databaseSelectorDelegate.deviceDataBases

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
                        createTabForDatabase(it.id)
                    }
                }
            }
        }
    }

    fun onDatabaseSelected(databaseId: DeviceDataBaseId) {
        databaseSelectorDelegate.onDatabaseSelected(databaseId)
    }

    fun onDatabaseDoubleClicked(databaseId: DeviceDataBaseId) {
        databaseSelectorDelegate.onDatabaseSelected(databaseId)

        createTabForDatabase(databaseId)
    }

    fun onTableDoubleClicked(databaseId: DeviceDataBaseId, table: TableUiModel) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            createTab(
                DatabaseTabState(
                    databaseId = databaseId,
                    tableName = table.name,
                )
            )
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

    private fun createTabForDatabase(databaseId: DeviceDataBaseId) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            createTab(
                DatabaseTabState(
                    databaseId = databaseId,
                    tableName = null,
                )
            )
        }
    }

    fun onTabCloseClicked(tab: DatabaseTabState) {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            removeTab(tab)
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

    private suspend fun createTab(tab: DatabaseTabState) {
        val deviceIdAndPackageName = getCurrentDeviceIdAndPackageNameUseCase() ?: return
        _selectedTab.update { it + (deviceIdAndPackageName to tab) }
        _tabs.update {
            val list = it[deviceIdAndPackageName] ?: emptyList()
            if (list.contains(tab)) it // nothing
            else {
                val newList = list.toMutableList().apply {
                    add(tab)
                }
                it + (deviceIdAndPackageName to newList)
            }
        }
    }
}
