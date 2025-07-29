package com.florent37.flocondesktop.app

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class InitialSetupStateHolder {

    private val _needsAdbSetup = MutableStateFlow<Boolean>(false)
    val needsAdbSetup: StateFlow<Boolean> = _needsAdbSetup.asStateFlow()

    fun setRequiresInitialSetup() {
        _needsAdbSetup.update { true }
    }

    fun setAdbIsWorking() {
        _needsAdbSetup.update { false }
    }
}
