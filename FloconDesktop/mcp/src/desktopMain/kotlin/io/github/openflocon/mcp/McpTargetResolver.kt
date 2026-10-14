package io.github.openflocon.mcp

import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.device.usecase.GetCurrentDeviceIdAndPackageNameUseCase
import kotlinx.coroutines.flow.first

/**
 * Resolves the device / app targeted by a tool call.
 * When nothing is specified, the device & app currently selected in Flocon are used.
 */
class McpTargetResolver(
    private val devicesRepository: DevicesRepository,
    private val getCurrentDeviceIdAndPackageNameUseCase: GetCurrentDeviceIdAndPackageNameUseCase,
) {

    suspend fun resolve(
        deviceId: String?,
        packageName: String?,
    ): DeviceIdAndPackageNameDomainModel = when {
        deviceId == null && packageName == null -> getCurrentDeviceIdAndPackageNameUseCase()
            ?: throw McpToolException("No device / app is currently selected in Flocon. ${availableTargetsHint()}")

        deviceId == null -> resolveByPackageName(packageName!!)

        packageName == null -> resolveByDeviceId(deviceId)

        else -> devicesRepository.getDeviceAppByPackage(deviceId, packageName)
            ?.toTarget(deviceId)
            ?: throw McpToolException("App '$packageName' not found on device '$deviceId'. ${availableTargetsHint()}")
    }

    internal suspend fun resolve(arguments: McpTargetArguments): DeviceIdAndPackageNameDomainModel = resolve(
        deviceId = arguments.deviceId?.takeIf { it.isNotBlank() },
        packageName = arguments.packageName?.takeIf { it.isNotBlank() },
    )

    /**
     * All the device / app couples known by Flocon, the current one first
     */
    suspend fun allTargets(): List<DeviceIdAndPackageNameDomainModel> {
        val current = getCurrentDeviceIdAndPackageNameUseCase()
        val all = devicesRepository.devices.first().flatMap { device ->
            devicesRepository.observeDeviceApps(device.deviceId).first().map { it.toTarget(device.deviceId) }
        }
        return listOfNotNull(current) + all.filterNot { current != null && it.isSameApp(current) }
    }

    private suspend fun resolveByPackageName(packageName: String): DeviceIdAndPackageNameDomainModel {
        val candidates = devicesRepository.devices.first().mapNotNull { device ->
            devicesRepository.getDeviceAppByPackage(device.deviceId, packageName)?.toTarget(device.deviceId)
        }
        val currentDeviceId = devicesRepository.getCurrentDeviceId()
        return candidates.firstOrNull { it.deviceId == currentDeviceId }
            ?: candidates.maxByOrNull { it.appInstance }
            ?: throw McpToolException("App '$packageName' not found on any device. ${availableTargetsHint()}")
    }

    private suspend fun resolveByDeviceId(deviceId: String): DeviceIdAndPackageNameDomainModel {
        devicesRepository.getDeviceSelectedApp(deviceId)?.let { return it.toTarget(deviceId) }

        val apps = devicesRepository.observeDeviceApps(deviceId).first()
        return when (apps.size) {
            0 -> throw McpToolException("Device '$deviceId' not found or has no app. ${availableTargetsHint()}")
            1 -> apps.first().toTarget(deviceId)
            else -> throw McpToolException(
                "Device '$deviceId' has several apps, please specify '$ARG_PACKAGE_NAME' among: ${apps.joinToString { it.packageName }}",
            )
        }
    }

    private suspend fun availableTargetsHint(): String {
        val targets = allTargets()
        return if (targets.isEmpty()) {
            "No device has been connected to Flocon yet: launch the mobile app with the Flocon client installed."
        } else {
            "Call list_devices, or pass '$ARG_DEVICE_ID' and '$ARG_PACKAGE_NAME' among: " +
                targets.joinToString { "(${it.deviceId}, ${it.packageName})" }
        }
    }
}

internal fun DeviceAppDomainModel.toTarget(deviceId: String) = DeviceIdAndPackageNameDomainModel(
    deviceId = deviceId,
    packageName = packageName,
    appInstance = lastAppInstance,
)

internal fun DeviceIdAndPackageNameDomainModel.isSameApp(other: DeviceIdAndPackageNameDomainModel): Boolean =
    deviceId == other.deviceId && packageName == other.packageName

internal fun McpTargetArguments.hasExplicitTarget(): Boolean = !deviceId.isNullOrBlank() || !packageName.isNullOrBlank()
