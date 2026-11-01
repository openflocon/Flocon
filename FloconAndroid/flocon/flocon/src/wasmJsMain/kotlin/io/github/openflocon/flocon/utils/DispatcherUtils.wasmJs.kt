package kotlinx.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

public actual val Dispatchers.IO: CoroutineDispatcher get() = Dispatchers.Default

public actual val IO: CoroutineDispatcher get() = Dispatchers.Default
