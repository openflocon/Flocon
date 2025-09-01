package io.github.openflocon.data.core.dashboard.datasource

import io.github.openflocon.domain.dashboard.models.DashboardDomainModel
import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.messages.models.FloconIncomingMessageDomainModel
import kotlin.uuid.ExperimentalUuidApi

interface ToDeviceDashboardDataSource {

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendClickEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        buttonId: String,
    )

    suspend fun submitFormEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        formId: String,
        values: Map<String, String>
    )

    @OptIn(ExperimentalUuidApi::class)
    suspend fun submitTextFieldEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        textFieldId: String,
        value: String,
    )

    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendUpdateCheckBoxEvent(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        checkBoxId: String,
        value: Boolean,
    )

    fun getItem(message: FloconIncomingMessageDomainModel): DashboardDomainModel?

}
