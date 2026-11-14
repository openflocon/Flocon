@file:OptIn(ExperimentalStdlibApi::class)

package io.github.openflocon.flocon.noop

import io.github.openflocon.flocon.FloconRuntime
import kotlin.native.EagerInitialization

// the marker class can't be looked up by name here: turns Flocon off when the app starts
@EagerInitialization
private val floconTurnedOff: Unit = FloconRuntime.turnOff()
