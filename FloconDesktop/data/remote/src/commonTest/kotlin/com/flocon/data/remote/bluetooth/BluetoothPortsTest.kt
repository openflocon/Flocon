package com.flocon.data.remote.bluetooth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class BluetoothPortsTest {

    @Test
    fun `linux rfcomm device`() {
        assertTrue(isBluetoothSerialPort("/dev/rfcomm0", "rfcomm0"))
    }

    @Test
    fun `windows standard serial over bluetooth link`() {
        assertTrue(isBluetoothSerialPort("\\\\.\\COM5", "Standard Serial over Bluetooth link (COM5)"))
    }

    @Test
    fun `macos paired device`() {
        assertTrue(isBluetoothSerialPort("/dev/tty.Pixel7", "Pixel7"))
        assertTrue(isBluetoothSerialPort("/dev/cu.Pixel7", "Pixel7"))
    }

    @Test
    fun `macos built-in ports are not a phone`() {
        assertFalse(isBluetoothSerialPort("/dev/tty.Bluetooth-Incoming-Port", "Bluetooth-Incoming-Port"))
        assertFalse(isBluetoothSerialPort("/dev/tty.debug-console", "debug-console"))
        assertFalse(isBluetoothSerialPort("/dev/tty.usbserial-1410", "USB Serial"))
    }

    @Test
    fun `plain serial ports are not listed`() {
        assertFalse(isBluetoothSerialPort("/dev/ttyUSB0", "USB-Serial Controller"))
        assertFalse(isBluetoothSerialPort("/dev/ttyS0", "ttyS0"))
        assertFalse(isBluetoothSerialPort("\\\\.\\COM3", "USB Serial Port (COM3)"))
    }
}
