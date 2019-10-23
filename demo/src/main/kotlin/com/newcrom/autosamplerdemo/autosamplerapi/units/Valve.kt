package com.newcrom.autosamplerdemo.autosamplerapi.units

import com.newcrom.autosamplerdemo.autosamplerapi.base.SerialDeviceBase


enum class ValveState {
    READY,
    ERROR,
    MOVING,
}

enum class ValveErrors {
    POSITION_UNKNOWN
}

const val VALVE = "G1"

const val VALVE_CALIBRATE = 10001
const val VALVE_ABORT = 10002

class Valve(private val serialDevice: SerialDeviceBase) {
    var position: Int? = null
    var state: ValveState? = null
    var errors: Set<ValveErrors> = emptySet()

    public fun move(position: Int) {  // 0 - top
        set(position)
    }

    public fun calibrate() {
        set(VALVE_CALIBRATE)
    }

    public fun abort() {
        set(VALVE_ABORT)
    }

    public fun update() {
        parse(serialDevice.getInt(VALVE))
    }

    private fun set(number: Int) {
        parse(serialDevice.set(VALVE, number))
    }

    private fun parse(vial: Int?) {
//        home = false
//        washing = false
//        number = null
//        state = null
//        errors = null
//        field = null
//        when {
//            offset == null -> {               // Unknown vial state
//                state = null
//            }
//            offset in 10000..11111 -> {       // Tray or arm error
//                state = NeedleState.ERROR
//                val errors = HashSet<NeedleErrors>()
//                if (offset % 10 == 1) {
//                    errors.add(NeedleErrors.POSITION_UNKNOWN)
//                }
//                this.errors = errors
//            }
//            offset > 20000 -> {               // Moving up
//                state = NeedleState.MOVING_UP
//                field = offset - 20000
//            }
//            offset > 30000 -> {               // Moving down
//                state = NeedleState.MOVING_DOWN
//                field = offset - 30000
//            }
//            offset in 0..40 -> {              // Fixed
//                state = NeedleState.READY
//                field = offset
//            }
//        }
    }
}