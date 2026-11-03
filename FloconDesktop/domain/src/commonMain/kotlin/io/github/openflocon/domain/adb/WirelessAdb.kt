package io.github.openflocon.domain.adb

// the adb serial of a device connected over the network: "192.168.1.20:5555", or "adb-XXXX._adb-tls-connect._tcp" once paired
// (an emulator is "emulator-5554", a cable "R5CY70X7WXX")
fun isWirelessAdbSerial(serial: String): Boolean = ':' in serial || "_adb-tls-" in serial
