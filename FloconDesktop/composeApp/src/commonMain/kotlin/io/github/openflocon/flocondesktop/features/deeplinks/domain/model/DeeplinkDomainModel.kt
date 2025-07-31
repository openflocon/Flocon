package io.github.openflocon.flocondesktop.features.deeplinks.domain.model

class DeeplinkDomainModel(
    val label: String?,
    val link: String,
    val description: String?,
)

// can be : protocol://link/sublink?toto=tata
// or can have fields
// can be : protocol://link/[sublink]?toto=[tata]
