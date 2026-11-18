package io.github.openflocon.data.core.sharedpreference.datasource

import io.github.openflocon.domain.device.models.DeviceIdAndPackageNameDomainModel
import io.github.openflocon.domain.sharedpreference.models.DeviceSharedPreferenceId
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceRowDomainModel
import io.github.openflocon.domain.sharedpreference.models.SharedPreferenceValuesResponseDomainModel
import kotlinx.coroutines.flow.Flow

interface DeviceSharedPreferencesValuesDataSource {

    fun onSharedPreferencesValuesReceived(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        received: SharedPreferenceValuesResponseDomainModel,
    )

    fun observe(
        deviceIdAndPackageName: DeviceIdAndPackageNameDomainModel,
        sharedPreferenceId: DeviceSharedPreferenceId,
    ): Flow<List<SharedPreferenceRowDomainModel>>
}
