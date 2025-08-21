package io.github.openflocon.flocondesktop.core.data.device

import io.github.openflocon.data.core.device.datasource.local.LocalCurrentDeviceDataSource
import io.github.openflocon.data.core.device.datasource.local.LocalDevicesDataSource
import io.github.openflocon.data.core.device.datasource.local.model.InsertDeviceResult
import io.github.openflocon.data.core.device.datasource.remote.RemoteDeviceDataSource
import io.github.openflocon.domain.Protocol
import io.github.openflocon.domain.adb.repository.AdbRepository
import io.github.openflocon.domain.common.DispatcherProvider
import io.github.openflocon.domain.device.models.DeviceAppDomainModel
import io.github.openflocon.domain.device.models.DeviceDomainModel
import io.github.openflocon.domain.device.models.DeviceId
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.device.models.DeviceWithAppDomainModel
import io.github.openflocon.domain.device.models.DeviceWithAppsDomainModel
import io.github.openflocon.domain.device.models.HandleDeviceResultDomainModel
import io.github.openflocon.domain.device.models.RegisterDeviceWithAppDomainModel
import io.github.openflocon.domain.device.repository.DevicesRepository
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import io.github.openflocon.domain.messages.repository.MessagesReceiverRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class DevicesRepositoryImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val adbRepository: AdbRepository,
    private val remoteDeviceDataSource: RemoteDeviceDataSource,
    private val localDevicesDataSource: LocalDevicesDataSource,
    private val localCurrentDeviceDataSource: LocalCurrentDeviceDataSource,
    applicationScope: CoroutineScope,
) : DevicesRepository,
    MessagesReceiverRepository {

    private val devicesMutex = Mutex()

    override val devices = localDevicesDataSource.devices
        .flowOn(dispatcherProvider.data)

    override val currentDeviceId: Flow<DeviceId?> = localCurrentDeviceDataSource.currentDeviceId

    override val currentDeviceWithApp: StateFlow<DeviceWithAppDomainModel?> = combine(
        localCurrentDeviceDataSource.currentDeviceId,
        localCurrentDeviceDataSource.currentDeviceApp,
    ) { device, app ->
        Pair(device, app)
    }.flatMapLatest { (deviceId, app) ->
        if (deviceId == null || app == null) {
            flowOf(null)
        } else {
            localDevicesDataSource.observeDeviceById(deviceId)
                .map { device ->
                    device?.let {
                        DeviceWithAppDomainModel(
                            device = it,
                            app = app,
                        )
                    }
                }
        }
    }
        .flowOn(dispatcherProvider.data)
        .stateIn(applicationScope, SharingStarted.Eagerly, null)

    override suspend fun getCurrentDeviceId(): DeviceId? =
        localCurrentDeviceDataSource.getCurrentDeviceId()

    override suspend fun getCurrentDevice(): DeviceDomainModel? =
        localCurrentDeviceDataSource.getCurrentDeviceId()?.let {
            localDevicesDataSource.getDeviceById(it)
        }

    override suspend fun getCurrentDeviceApp(): DeviceAppDomainModel? =
        localCurrentDeviceDataSource.getCurrentDeviceApp()

    // returns new if new device
    override suspend fun register(registerDeviceWithApp: RegisterDeviceWithAppDomainModel): HandleDeviceResultDomainModel =
        withContext(dispatcherProvider.data) {
            devicesMutex.withLock {
                val isKnownDevice =
                    localCurrentDeviceDataSource.isKnownDeviceForThisSession(
                        registerDeviceWithApp.device.deviceId
                    )
                if (!isKnownDevice) {
                    HandleDeviceResultDomainModel(
                        deviceId = registerDeviceWithApp.device.deviceId,
                        justConnectedForThisSession = false,
                        isNewDevice = false,
                    )
                } else {
                    val isNewDevice = when (localDevicesDataSource.insertDevice(registerDeviceWithApp.device)) {
                        InsertDeviceResult.New -> true
                        InsertDeviceResult.Exists -> false
                    }
                    localCurrentDeviceDataSource.addNewDeviceConnectedForThisSession(registerDeviceWithApp.device.deviceId)
                    HandleDeviceResultDomainModel(
                        deviceId = registerDeviceWithApp.device.deviceId,
                        justConnectedForThisSession = true,
                        isNewDevice = isNewDevice,
                    )
                }
            }
        }

    override suspend fun clear() {
        withContext(dispatcherProvider.data) {
            devicesMutex.withLock {
                localDevicesDataSource.clear()
            }
        }
    }

    override suspend fun selectApp(app: DeviceAppDomainModel) {
        withContext(dispatcherProvider.data) {
            localCurrentDeviceDataSource.selectApp(app)
        }
    }

    override suspend fun selectDevice(deviceId: DeviceId) {
        withContext(dispatcherProvider.data) {
            localCurrentDeviceDataSource.selectDevice(deviceId)
        }
    }

    // region apps
    override fun observeDeviceApps(deviceId: DeviceId): Flow<List<DeviceAppDomainModel>> {
        return localDevicesDataSource.observeDeviceApps(deviceId)
            .flowOn(dispatcherProvider.data)
    }

    override suspend fun getDeviceAppByPackage(deviceId: DeviceId, appPackageName: String) : DeviceAppDomainModel? {
        return withContext(dispatcherProvider.data) {
            localDevicesDataSource.getDeviceAppByPackage(deviceId, appPackageName)
        }
    }
    // endregion

    override val pluginName = listOf(Protocol.FromDevice.Device.Plugin)

    override suspend fun onMessageReceived(
        deviceId: String,
        message: FloconIncomingMessageDomainModel,
    ) {
        when (message.method) {
            Protocol.FromDevice.Device.Method.RegisterDevice -> {
                remoteDeviceDataSource.getDeviceSerial(message)?.let { serial ->
                    adbRepository.saveAdbSerial(
                        deviceId = message.deviceId,
                        serial = serial,
                    )
                }
            }
        }
    }

    override suspend fun onDeviceConnected(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        isNewDevice: Boolean,
    ) {
        // no op
    }
}
