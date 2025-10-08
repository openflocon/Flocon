package io.github.openflocon.flocondesktop.features.database

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.database.models.DeviceDataBaseId
import io.github.openflocon.flocondesktop.features.database.delegate.DatabaseSelectorDelegate
import io.github.openflocon.flocondesktop.features.database.model.DatabaseTabState
import io.github.openflocon.flocondesktop.features.database.model.DatabasesStateUiModel
import io.github.openflocon.flocondesktop.features.database.model.TableUiModel
import io.github.openflocon.flocondesktop.features.database.model.selectedDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DatabaseViewModel(
    private val databaseSelectorDelegate: DatabaseSelectorDelegate,
    private val dispatcherProvider: DispatcherProvider,
) : ViewModel(databaseSelectorDelegate) {
    val deviceDataBases: StateFlow<DatabasesStateUiModel> = databaseSelectorDelegate.deviceDataBases

    private val _selectedTab = MutableStateFlow<DatabaseTabState?>(null)
    val selectedTab = _selectedTab.asStateFlow()
    private val _tabs = MutableStateFlow(emptyList<DatabaseTabState>())
    val tabs = _tabs.asStateFlow()

    init {
        viewModelScope.launch(dispatcherProvider.viewModel) {
            databaseSelectorDelegate.deviceDataBases.collect {
                if(tabs.value.isEmpty()) {
                    it.selectedDatabase()?.let {
                        createTabForDatabase(it.id)
                    }
                }
            }
        }
    }

    fun onDatabaseSelected(databaseId: DeviceDataBaseId) {
        databaseSelectorDelegate.onDatabaseSelected(databaseId)

        if (tabs.value.isEmpty()) {
            createTabForDatabase(databaseId)
        }
    }

    fun onDatabaseDoubleClicked(databaseId: DeviceDataBaseId) {
        databaseSelectorDelegate.onDatabaseSelected(databaseId)

        createTabForDatabase(databaseId)
    }

    fun onTableDoubleClicked(databaseId: DeviceDataBaseId, table: TableUiModel) {
        createTab(
            DatabaseTabState(
                databaseId = databaseId,
                tableName = table.name,
            )
        )
    }

    fun onTabSelected(tab: DatabaseTabState) {
        _selectedTab.update { tab }
    }

    fun onTabCloseClicked(tab: DatabaseTabState) {
        _tabs.update { it - tab }
        if (selectedTab.value == tab) {
            _selectedTab.update { tabs.value.firstOrNull() }
        }
    }

    fun onVisible() {
        databaseSelectorDelegate.start()
    }

    fun onNotVisible() {
        databaseSelectorDelegate.stop()
    }

    private fun createTabForDatabase(databaseId: DeviceDataBaseId) {
        createTab(
            DatabaseTabState(
                databaseId = databaseId,
                tableName = null,
            )
        )
    }

    private fun createTab(tab: DatabaseTabState) {
        _selectedTab.update { tab }
        _tabs.update {
            if(it.contains(tab)) it // nothing
            else it + tab
        }
    }
}
