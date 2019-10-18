package com.newcrom.autosamplerdemo.autosamplerapi

import com.fazecast.jSerialComm.SerialPort
import com.newcrom.autosamplerdemo.autosamplerapi.SerialDeviceBase.Companion.findPortByDescription


const val AUTOSAMPLER_COMMAND_GET_READY = 0
const val AUTOSAMPLER_COMMAND_INJECTION = 1
const val AUTOSAMPLER_COMMAND_WASH_NEEDLE = 2
const val AUTOSAMPLER_COMMAND_SHAKING = 3

enum class AutosamplerState {
    READY,
    C1_TRAY_ARM_MOVING,
    C1_NEEDLE_DOWN,
    C1_SYRINGE,
    C1_HOME,
    C1_INJECTION_START,
    C1_GETTING_READY,
    C2_WASHING,
    C3_SHAKING,
    ERROR,
    GETTING_READY,
    LOW_LEVEL_COMMANDS
}

val AUTOSAMPLER_STATE_BEFORE_INJECTION = arrayOf(
        AutosamplerState.C1_TRAY_ARM_MOVING,
        AutosamplerState.C1_NEEDLE_DOWN,
        AutosamplerState.C1_SYRINGE,
        AutosamplerState.C1_HOME)

val AUTOSAMPLER_STATE_AFTER_INJECTION = arrayOf(
        AutosamplerState.C1_INJECTION_START,
        AutosamplerState.C1_GETTING_READY)

val NUMBER_TO_AUTOSAMPLER_STATE = mapOf(
        0 to AutosamplerState.READY,
        11 to AutosamplerState.C1_TRAY_ARM_MOVING,
        12 to AutosamplerState.C1_NEEDLE_DOWN,
        13 to AutosamplerState.C1_SYRINGE,
        14 to AutosamplerState.C1_HOME,
        15 to AutosamplerState.C1_INJECTION_START,
        16 to AutosamplerState.C1_GETTING_READY,
        21 to AutosamplerState.C2_WASHING,
        31 to AutosamplerState.C3_SHAKING,
        100 to AutosamplerState.ERROR,
        101 to AutosamplerState.GETTING_READY,
        102 to AutosamplerState.LOW_LEVEL_COMMANDS
)

enum class AutosamplerError {
    TRAY_NOT_PRESENT,
    TRAY_ROTATION,
    ARM_ROTATION_BLOCKED,
    NEEDLE_MOVING,
    SYRINGE_MOVING,
    VALVE_ROTATION
}

val NUMBER_TO_AUTOSAMPLER_ERROR = mapOf(
        1 to AutosamplerError.TRAY_NOT_PRESENT,
        2 to AutosamplerError.TRAY_ROTATION,
        4 to AutosamplerError.ARM_ROTATION_BLOCKED,
        8 to AutosamplerError.NEEDLE_MOVING,
        16 to AutosamplerError.SYRINGE_MOVING,
        32 to AutosamplerError.VALVE_ROTATION
)

const val SERIAL = "A1"

const val COMMAND = "B1"
const val ERRORS = "B2"

const val COMMAND_START = "B3"
const val C1_VIAL = "B4"
const val C1_AMOUNT = "B5"
const val C1_VALVE_TIME = "B6"
const val C1_DEPTH = "B7"
const val C2_CYCLES = "B8"
const val C3_MODE = "B9"
const val C3_DURATION = "B10"

class Autosampler(port: SerialPort) {
    private val serialDevice = SerialDeviceBase(port) { dead = true }
    var dead: Boolean = false

    var vial = Vial(serialDevice)
    var needle = Needle(serialDevice)

    var state: AutosamplerState? = null
    var errors: Set<AutosamplerError>? = null

    val serial: String = serialDevice.get(SERIAL)

    var onInjected: () -> Unit = {}

    public fun getReady() {
        serialDevice.set(COMMAND_START, AUTOSAMPLER_COMMAND_GET_READY)
    }

    public fun injection(vial: Int, amount: Double, valveTime: Long, depth: Int) {
        serialDevice.set(C1_VIAL, vial)
        serialDevice.set(C1_AMOUNT, amount.toInt())
        serialDevice.set(C1_VALVE_TIME, valveTime.toInt())
        serialDevice.set(C1_DEPTH, depth)
        serialDevice.set(COMMAND_START, AUTOSAMPLER_COMMAND_INJECTION)
    }

    public fun washNeedle(cycles: Int) {
        serialDevice.set(C2_CYCLES, cycles)
        serialDevice.set(COMMAND_START, AUTOSAMPLER_COMMAND_WASH_NEEDLE)
    }

    public fun shaking(mode: Int, duration: Int) {
        serialDevice.set(C3_MODE, mode)
        serialDevice.set(C3_DURATION, duration)
        serialDevice.set(COMMAND_START, AUTOSAMPLER_COMMAND_SHAKING)
    }

    public fun updateLowLevel() {
        vial.update()
        needle.update()
    }

    public fun update(): AutosamplerState? {
        return parseCommand(serialDevice.getInt(COMMAND))
    }

    private fun executeCommand(command: Int): AutosamplerState? {
        return parseCommand(serialDevice.set(COMMAND, command))
    }

    private fun parseCommand(command: Int?): AutosamplerState? {
        val statePrev = state
        state = NUMBER_TO_AUTOSAMPLER_STATE[command]
        if (state == AutosamplerState.ERROR) {
            parseErrors(serialDevice.getInt(ERRORS))
        }
        if (statePrev in AUTOSAMPLER_STATE_AFTER_INJECTION && state in AUTOSAMPLER_STATE_AFTER_INJECTION) {
            onInjected()
        }
        return state
    }

    private fun parseErrors(errorsCode: Int?) {
        if (errorsCode == null) {
            errors = null
            return
        }
        val errors = HashSet<AutosamplerError>()
        NUMBER_TO_AUTOSAMPLER_ERROR.keys.forEach { code ->
            if (code or errorsCode > 0) {
                NUMBER_TO_AUTOSAMPLER_ERROR[code]
            }
        }
        this.errors = errors
    }

    override fun toString(): String {
        return "Autosampler(stateStr='$state')"
    }

    companion object {
        fun find(): Autosampler? {
            val port = findPortByDescription("Sielc Autosampler V1.0")
            if (port != null) {
                return Autosampler(port)
            }
            return null
        }
    }
}
