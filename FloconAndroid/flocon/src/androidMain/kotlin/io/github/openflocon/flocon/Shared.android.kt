package io.github.openflocon.flocon

import io.github.openflocon.flocon.utils.NetUtils

internal actual fun getServerHost(floconContext: FloconContext): String {
    val appContext = floconContext.appContext
    return NetUtils.getServerHost(context = appContext)
}