package com.newcrom.autosamplerdemo.autosamplerapi.units

import com.newcrom.autosamplerdemo.autosamplerapi.base.SerialDeviceBase


enum class SyringeState {
    READY,
    ERROR,
    REFILLING,
    DRAWING,
}

enum class SyringeErrors {
    POSITION_UNKNOWN
}

const val SYRINGE = "H1"
const val SYRINGE_DRAW_SPEED = "H2"
const val SYRINGE_REFILL_SPEED = "H3"

const val SYRINGE_CALIBRATE = 10001
const val SYRINGE_ABORT = 10002

class Syringe(private val serialDevice: SerialDeviceBase) {
    var empty: Boolean = false
    var position: Int? = null
    var drawSpeed: Int? = null
    var refillSpeed: Int? = null
    var state: SyringeState? = null
    var errors: Set<SyringeErrors> = emptySet()

    public fun move(position: Int, drawSpeed: Int, refillSpeed: Int) {  // 0 - empty
        this.drawSpeed = serialDevice.set(SYRINGE_DRAW_SPEED, drawSpeed)
        this.refillSpeed = serialDevice.set(SYRINGE_REFILL_SPEED, refillSpeed)
        set(position)
    }

    public fun calibrate() {
        set(SYRINGE_CALIBRATE)
    }

    public fun abort() {
        set(SYRINGE_ABORT)
    }

    public fun update() {
        parse(serialDevice.getInt(SYRINGE))
    }

    private fun set(number: Int) {
        parse(serialDevice.set(SYRINGE, number))
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