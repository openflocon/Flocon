package io.github.openflocon.flocondesktop.common.utils

import androidx.compose.runtime.Composable
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow

/**
 * Molecule for the ViewModels. Use it instead of `launchMolecule` on `viewModelScope`.
 *
 * Molecule sends the snapshot apply notifications from a coroutine launched at each state write.
 * On `viewModelScope` (Dispatchers.Main.immediate) that coroutine ran inside the write when it happened
 * on the UI thread, e.g. a LazyColumn saving its scroll position during its measure, while Compose holds
 * snapshot locks: it deadlocked with a background thread sending the notifications at the same time
 * (the app froze when scrolling the network list). Dispatchers.Main posts it after the write instead.
 */
fun <T> CoroutineScope.launchUiMolecule(body: @Composable () -> T): StateFlow<T> = launchMolecule(
    mode = RecompositionMode.Immediate,
    context = Dispatchers.Main,
    body = body,
)
