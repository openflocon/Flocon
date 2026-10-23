package com.flocon.data.remote.bluetooth

import com.fazecast.jSerialComm.SerialPort
import java.io.IOException

/** A serial port of the OS, bound to the RFCOMM link of a paired phone. */
internal class SerialPortLink private constructor(private val port: SerialPort) : BridgeLink {
    override val input get() = port.inputStream
    override val output get() = port.outputStream

    override fun close() {
        port.closePort()
    }

    companion object {
        fun listBluetoothPorts(): List<String> = SerialPort.getCommPorts()
            .filter { isBluetoothSerialPort(path = it.systemPortPath, descriptiveName = it.descriptivePortName) }
            .map { it.systemPortPath }

        fun open(path: String): SerialPortLink {
            val port = SerialPort.getCommPort(path)
            port.setComPortParameters(BAUD_RATE, 8, SerialPort.ONE_STOP_BIT, SerialPort.NO_PARITY)
            // a read waits for at least one byte, without a timeout: the link can stay quiet
            port.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0)
            if (!port.openPort()) throw IOException("Cannot open the serial port $path")
            return SerialPortLink(port)
        }

        private const val BAUD_RATE = 115_200 // ignored by RFCOMM
    }
}
