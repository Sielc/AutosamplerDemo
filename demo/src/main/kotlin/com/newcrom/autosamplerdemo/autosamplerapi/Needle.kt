package com.newcrom.autosamplerdemo.autosamplerapi


enum class NeedleState {
    READY,
    ERROR,
    MOVING_UP,
    MOVING_DOWN,
}

enum class NeedleErrors {
    POSITION_UNKNOWN
}

const val NEEDLE = "F1"

class Needle(private val serialDevice: SerialDeviceBase) {
    var top: Boolean = false
    var offset: Int? = null
    var state: NeedleState? = null
    var errors: Set<NeedleErrors> = emptySet()

    public fun move(offset: Int) {  // 0 - top
        set(offset)
    }

    public fun calibrate() {
        set(10001)
    }

    public fun abort(vial: Int) {
        set(10002)
    }

    public fun update() {
        parse(serialDevice.getInt(NEEDLE))
    }

    private fun set(number: Int) {
        parse(serialDevice.set(NEEDLE, number))
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