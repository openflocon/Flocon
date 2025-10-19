package io.github.openflocon.flocon.plugins.device

interface FloconDevicePlugin {
    fun registerWithSerial(serial: String)
}