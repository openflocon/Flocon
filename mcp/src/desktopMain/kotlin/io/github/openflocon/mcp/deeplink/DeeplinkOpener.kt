package io.github.openflocon.mcp.deeplink

import io.github.openflocon.domain.common.Either
import io.github.openflocon.domain.deeplink.usecase.ExecuteDeeplinkUseCase
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel

/**
 * Opens a deeplink on a device, the only action of the deeplink tools that is not read only
 */
internal fun interface DeeplinkOpener {
    suspend fun open(
        link: String,
        deeplinkId: Long,
        saveIntoHistory: Boolean,
        target: DeviceIdAndPackageNameDomainModel,
    ): Either<Throwable, String>
}

internal class ExecuteDeeplinkOpener(
    private val executeDeeplinkUseCase: ExecuteDeeplinkUseCase,
) : DeeplinkOpener {
    override suspend fun open(
        link: String,
        deeplinkId: Long,
        saveIntoHistory: Boolean,
        target: DeviceIdAndPackageNameDomainModel,
    ) = executeDeeplinkUseCase(
        deeplink = link,
        deeplinkId = deeplinkId,
        saveIntoHistory = saveIntoHistory,
        target = target,
    )
}
