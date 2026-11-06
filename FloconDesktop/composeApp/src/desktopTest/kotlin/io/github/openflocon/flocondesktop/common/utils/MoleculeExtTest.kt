package io.github.openflocon.flocondesktop.common.utils

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.Snapshot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.test.Test
import kotlin.test.assertFalse

class MoleculeExtTest {

    // A write on the UI thread (e.g. the scroll position of a LazyColumn during its measure, while Compose
    // holds snapshot locks) must not make Molecule send the apply notifications right away, on the same
    // thread: with Dispatchers.Main.immediate it did, and it deadlocked with a worker thread doing the same.
    @Test
    fun `a write on the UI thread does not send the apply notifications during the write`() = runBlocking<Unit> {
        withContext(Dispatchers.Main) {
            val viewModelScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
            val state = mutableStateOf(0)
            viewModelScope.launchUiMolecule { state.value }

            var writing = false
            var notifiedDuringWrite = false
            val observer = Snapshot.registerApplyObserver { _, _ ->
                if (writing) notifiedDuringWrite = true
            }

            writing = true
            state.value = 1
            writing = false

            observer.dispose()
            viewModelScope.cancel()
            assertFalse(notifiedDuringWrite, "the apply notifications were sent inside the write")
        }
    }
}
