package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.TransactionPreferencesRepository
import kotlinx.coroutines.flow.Flow

class ObserveTransactionsExpandedByDefaultUseCase(
    private val transactionPreferencesRepository: TransactionPreferencesRepository,
) {

    operator fun invoke(): Flow<Boolean> = transactionPreferencesRepository.expandedByDefault
}
