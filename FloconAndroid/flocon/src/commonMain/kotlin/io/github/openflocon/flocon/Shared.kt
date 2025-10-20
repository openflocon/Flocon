package io.github.openflocon.flocon

import io.github.openflocon.flocon.core.AppInfos

internal expect fun getAppInfos(floconContext: FloconContext) : AppInfos
internal expect fun getServerHost(floconContext: FloconContext) : String