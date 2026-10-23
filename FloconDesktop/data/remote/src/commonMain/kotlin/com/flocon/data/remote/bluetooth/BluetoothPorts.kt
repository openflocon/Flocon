package com.flocon.data.remote.bluetooth

/**
 * The OS exposes the RFCOMM link of a paired phone as a serial port: `/dev/rfcomm0` on Linux, an
 * outgoing "Standard Serial over Bluetooth link" COM port on Windows, `/dev/tty.<device name>` on macOS.
 */
internal fun isBluetoothSerialPort(path: String, descriptiveName: String): Boolean {
    val p = path.lowercase()
    val d = descriptiveName.lowercase()
    // macOS built-in ports that are not a paired device
    if ("incoming-port" in p || "debug-console" in p || "wlan-debug" in p) return false
    if ("rfcomm" in p || "bluetooth" in p || "bluetooth" in d) return true
    val macSerial = p.startsWith("/dev/tty.") || p.startsWith("/dev/cu.")
    return macSerial && "usb" !in p && "usb" !in d
}
