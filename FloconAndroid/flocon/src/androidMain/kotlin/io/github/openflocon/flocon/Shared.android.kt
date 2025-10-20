package io.github.openflocon.flocon

import android.content.Context
import android.os.Build
import android.provider.Settings
import io.github.openflocon.flocon.core.AppInfos
import io.github.openflocon.flocon.utils.AppUtils
import io.github.openflocon.flocon.utils.NetUtils

internal actual fun getServerHost(floconContext: FloconContext): String {
    val appContext = floconContext.appContext
    return NetUtils.getServerHost(context = appContext)
}