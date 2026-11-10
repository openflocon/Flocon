package io.github.openflocon.domain.settings.usecase

import io.github.openflocon.domain.settings.repository.TransactionPreferencesRepository

class SetTransactionsExpandedByDefaultUseCase(
    private val transactionPreferencesRepository: TransactionPreferencesRepository,
) {

    suspend operator fun invoke(expanded: Boolean) {
        transactionPreferencesRepository.setExpandedByDefault(expanded)
    }
}
