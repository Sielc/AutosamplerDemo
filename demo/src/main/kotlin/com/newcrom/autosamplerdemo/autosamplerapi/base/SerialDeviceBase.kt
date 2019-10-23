package com.newcrom.autosamplerdemo.autosamplerapi.base

import com.fazecast.jSerialComm.SerialPort
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError
import java.util.concurrent.TimeoutException
import javax.naming.NameNotFoundException


// Classes to support DOMP protocol


class VarInt(name: String, val valueInt: Int) : VarStr(name, valueInt.toString())


open class VarStr(val name: String, val value: String) {
    companion object {
        fun parse(buffer: ByteArray): VarStr {
            return parse(buffer.toString(Charsets.US_ASCII))
        }

        fun parse(buffer: String): VarStr {
            val name = buffer.substring(3, 5).toUpperCase()
            val delimiter = buffer.substring(5, 6)
            var value = buffer.substring(6, buffer.length - 1)

            when (delimiter) {
                "!" -> {
                    println("Set $name exception $value")
                    throw PropertySetterException(value)
                }
                "=" -> {
                    println("$name = $value")
                    val valueInt = value.toIntOrNull()
                    return if (valueInt != null)
                        VarInt(name, valueInt)
                    else
                        VarStr(name, value)
                }
                "/" -> {
                    value = value.substring(1, buffer.length - 2)
                    println("$name = $value")
                    val valueInt = value.toIntOrNull()
                    return if (valueInt != null)
                        VarInt(name, valueInt)
                    else
                        VarStr(name, value)
                }
                else -> throw Exception("Unknown delimiter $delimiter")
            }
        }
    }
}


open class SerialDeviceBase(val port: SerialPort, val onDead: () -> Unit) {
    class DeviceDisconnected : Exception("Port is closed")

    var dead: Boolean = false

    fun set(name: String, value: String): String {
        write(name, value)
        val v = readBlockingInt()
        if (v.name != name) throw NameNotFoundException("Returned variable ${v.name} is not $name")
        return v.value
    }

    fun set(name: String, value: Int): Int {
        write(name, value)
        val v = readBlockingInt()
        if (v.name != name) throw NameNotFoundException("Returned variable ${v.name} is not $name")
        return v.valueInt
    }

    fun get(name: String): String {
        val write = (">1 $name?\r").toByteArray(Charsets.US_ASCII)
        port.writeBytes(write, write.size.toLong())
        val v = readBlocking()
        if (v.name != name) throw NameNotFoundException("Returned variable ${v.name} is not $name")
        return v.value
    }

    fun getInt(name: String): Int {
        val write = (">1 $name?\r").toByteArray(Charsets.US_ASCII)
        port.writeBytes(write, write.size.toLong())
        val v = readBlockingInt()
        if (v.name != name) throw NameNotFoundException("Returned variable ${v.name} is not $name")
        return v.valueInt
    }

    private fun write(name: String, value: Int) {
        val write = (">1 $name=$value\r").toByteArray(Charsets.US_ASCII)
        port.writeBytes(write, write.size.toLong())
    }

    private fun write(name: String, value: String) {
        val write = (">1 $name/\"$value\"\r").toByteArray(Charsets.US_ASCII)
        port.writeBytes(write, write.size.toLong())
    }

    private fun readBlocking(trials: Int = 20): VarStr {
        var countdown = trials
        var available = 0
        var buffer = ""
        while (available == 0 && countdown-- > 0 && (buffer.isEmpty() || buffer[buffer.length - 1] != '\r')) {
            Thread.sleep(2)
            available = port.bytesAvailable()
            print("$available available ")
            when {
                available < 0 -> {
                    dead = true
                    onDead()
                    throw DeviceDisconnected()
                }
                available > 0 -> {
                    val readBuffer = ByteArray(available)
                    val numRead = port.readBytes(readBuffer, available.toLong())
                    val read = readBuffer.toString(Charsets.US_ASCII)
                    println("read $numRead -> $read. ")
                    buffer += read
                }
            }
        }
        if (buffer.isNotEmpty() && buffer[buffer.length - 1] == '\r') return VarStr.parse(buffer)
        throw TimeoutException("$trials trials failed")
    }

    private fun readBlockingInt(trials: Int = 10): VarInt {
        val v = readBlocking(trials)
        return when (v) {
            is VarInt -> v
            else -> throw TypeCheckError(ErrorMsg("Not Int"))
        }
    }

    companion object {
        fun findPortByDescription(portDescription: String): SerialPort? {
            val ports = SerialPort.getCommPorts()
            for (port in ports) {
                if (port.portDescription != portDescription) continue

                println("Connecting to $port")

                port.baudRate = 115200
                port.numStopBits = 1
                port.numDataBits = 8
                port.parity = SerialPort.NO_PARITY
                port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING or SerialPort.TIMEOUT_WRITE_BLOCKING, 1000, 0)
                port.openPort(0)

                if (!port.isOpen) {
                    Thread.sleep(100)
                    if (!port.isOpen) {
                        continue  // Unable to connect
                    }
                }

                // Ensure the next command will be sent without trash
                port.writeBytes("\r".toByteArray(Charsets.US_ASCII), 1)

                return port
            }
            return null
        }
    }
}
