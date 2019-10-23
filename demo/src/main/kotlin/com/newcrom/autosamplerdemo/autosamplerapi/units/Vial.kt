package com.newcrom.autosamplerdemo.autosamplerapi.units

import com.newcrom.autosamplerdemo.autosamplerapi.base.SerialDeviceBase


enum class VialState {
    READY,
    ERROR,
    SEARCHING,
}

enum class VialErrors {
    TRAY,
    ARM
}

const val VIAL = "E1"

const val VIAL_HOME = 0
const val VIAL_WASHING = 999

const val VIAL_CALIBRATE = 10001
const val VIAL_ABORT = 10002

class Vial(private val serialDevice: SerialDeviceBase) {
    public var home: Boolean = false
    public var washing: Boolean = false
    public var number: Int? = null

    public var state: VialState? = null
    public var errors: Set<VialErrors>? = null

    public fun locate(vial: Int) {  // 0 - go home; [1, 40] - choose vial
        parse(serialDevice.set(VIAL, vial))
    }

    public fun home() {
        parse(serialDevice.set(VIAL, VIAL_HOME))
    }

    public fun washing() {
        parse(serialDevice.set(VIAL, VIAL_WASHING))
    }

    public fun calibrate() {
        parse(serialDevice.set(VIAL, VIAL_CALIBRATE))
    }

    public fun abort() {
        parse(serialDevice.set(VIAL, VIAL_ABORT))
    }

    public fun update() {
        parse(serialDevice.getInt(VIAL))
    }

    private fun parse(vial: Int?) {
        home = false
        washing = false
        number = null
        state = null
        errors = null
        when {
            vial == null -> {               // Unknown vial state
            }
            vial in 10000..11111 -> {       // Tray or arm error
                state = VialState.ERROR
                val errors = HashSet<VialErrors>()
                if (vial % 10 == 1) {           // 1***1 Tray error
                    errors.add(VialErrors.TRAY)
                }
                if ((vial / 10) % 10 == 1) {    // 1**1* Arm error
                    errors.add(VialErrors.ARM)
                }
                this.errors = errors
            }
            vial > 20000 -> {               // Moving to vial or moving home
                state = VialState.SEARCHING
                number = vial - 20000
            }
            vial in 0..999 -> {              // Fixed on vial or fixed at home
                state = VialState.READY
                when (vial) {
                    VIAL_HOME -> home = true
                    VIAL_WASHING -> washing = true
                }
                number = vial
            }
        }
    }
}