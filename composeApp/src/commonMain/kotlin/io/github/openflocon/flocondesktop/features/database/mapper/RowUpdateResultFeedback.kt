package io.github.openflocon.flocondesktop.features.database.mapper

import flocondesktop.composeapp.generated.resources.Res
import flocondesktop.composeapp.generated.resources.database_row_update_device_changed
import flocondesktop.composeapp.generated.resources.database_row_update_failed
import flocondesktop.composeapp.generated.resources.database_row_update_invalid_number
import flocondesktop.composeapp.generated.resources.database_row_update_not_found
import flocondesktop.composeapp.generated.resources.database_row_updated
import io.github.openflocon.domain.database.models.DatabaseRowUpdateResultDomainModel
import io.github.openflocon.domain.feedback.FeedbackDisplayer
import org.jetbrains.compose.resources.getString

/** The result of a row update, from the detail panel or from a cell of the results. */
suspend fun FeedbackDisplayer.displayRowUpdateResult(result: DatabaseRowUpdateResultDomainModel) {
    val message = when (result) {
        DatabaseRowUpdateResultDomainModel.Updated -> getString(Res.string.database_row_updated)
        DatabaseRowUpdateResultDomainModel.RowNotFound -> getString(Res.string.database_row_update_not_found)
        DatabaseRowUpdateResultDomainModel.DeviceChanged -> getString(Res.string.database_row_update_device_changed)
        is DatabaseRowUpdateResultDomainModel.InvalidNumber -> getString(Res.string.database_row_update_invalid_number, result.column)
        is DatabaseRowUpdateResultDomainModel.Failed -> getString(Res.string.database_row_update_failed, result.message)
    }
    displayMessage(
        message = message,
        type = if (result == DatabaseRowUpdateResultDomainModel.Updated) {
            FeedbackDisplayer.MessageType.Success
        } else {
            FeedbackDisplayer.MessageType.Error
        },
    )
}
