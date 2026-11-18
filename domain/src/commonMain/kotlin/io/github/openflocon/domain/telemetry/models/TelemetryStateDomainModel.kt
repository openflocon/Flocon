package io.github.openflocon.domain.telemetry.models

data class TelemetryStateDomainModel(
    /** `false` on builds without an API key, or when DO_NOT_TRACK is set: nothing is ever sent. */
    val isAvailable: Boolean,
    val doNotTrack: Boolean,
    /** `true` once the user answered the consent prompt. Nothing is sent before that. */
    val hasChosen: Boolean,
    /** [TelemetryLevelDomainModel.Disabled] until the user explicitly picks another level. */
    val level: TelemetryLevelDomainModel,
) {
    val shouldAskConsent: Boolean get() = isAvailable && !hasChosen

    /** Whether an event would leave the computer right now. */
    val canSend: Boolean get() = isAvailable && hasChosen && level != TelemetryLevelDomainModel.Disabled
}
