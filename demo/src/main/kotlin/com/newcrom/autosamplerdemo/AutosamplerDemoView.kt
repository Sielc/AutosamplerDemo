package com.newcrom.autosamplerdemo

import com.newcrom.autosamplerdemo.Styles.Companion.loginScreen
import com.newcrom.autosamplerdemo.autosamplerapi.Autosampler
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TabPane
import tornadofx.*

class AutosamplerDemoView : View("Newcrom Autosampler") {
    private val model = object : ViewModel() {
        val serial = bind { SimpleStringProperty() }

        val vial = bind { SimpleIntegerProperty(21) }
        val amount = bind { SimpleIntegerProperty(2) }
        val depth = bind { SimpleIntegerProperty(25) }
        val injectionTime = bind { SimpleIntegerProperty(500) }
        val cycles = bind { SimpleIntegerProperty(2) }
        val mode = bind { SimpleIntegerProperty(1) }
        val duration = bind { SimpleIntegerProperty(5000) }

        val vialL = bind { SimpleIntegerProperty(0) }
        val needleL = bind { SimpleIntegerProperty(0) }
        val valveL = bind { SimpleIntegerProperty(0) }
        val syringeL = bind { SimpleIntegerProperty(0) }
        val syringeDrawL = bind { SimpleIntegerProperty(10000) }
        val syringeRefillL = bind { SimpleIntegerProperty(10000) }
    }

    var autosampler: Autosampler? = getAutosamplerUnsafe()

    private fun getAutosamplerSafe(): Autosampler {
        var a = autosampler
        if (a == null || a.dead) {
            a = Autosampler.find()
            autosampler = a
        }
        if (a == null) {
            model.commit {
                model.serial.value = "No Autosampler"
            }
            throw Exception("No Autosampler")
        }
        model.serial.value = a.serial
        return a
    }

    private fun getAutosamplerUnsafe(): Autosampler? {
        return try {
            getAutosamplerSafe()
        } catch (e: Exception) {
            println("! No Autosampler")
            null
        }
    }

    override val root = form {
        addClass(loginScreen)
        fieldset {
            field("Serial                      ") {
                textfield(model.serial) {
                    isEditable = false
                }
            }
        }
        tabpane {
            tabClosingPolicy = TabPane.TabClosingPolicy.UNAVAILABLE
            tab("High level") {
                vbox {
                    fieldset {
                        field {
                            button("Get ready") {
                                action {
                                    println("Get Ready")
                                    getAutosamplerUnsafe()?.getReady()
                                }
                            }
                        }
                    }
                    fieldset("Injection") {
                        field("Vial") {
                            textfield(model.vial) {
                                required()
                                whenDocked { requestFocus() }
                            }
                        }
                        field("Amount, uL") {
                            textfield(model.amount) {
                                required()
                            }
                        }
                        field("Depth, mm") {
                            textfield(model.depth) {
                                required()
                            }
                        }
                        field("Injection Time, ms") {
                            textfield(model.injectionTime) {
                                required()
                            }
                        }
                        field {
                            button("Inject") {
                                isDefaultButton = true
                                action {
                                    println("Inject")
                                    getAutosamplerUnsafe()?.injection(model.vial.value.toInt(), model.amount.value.toDouble(), model.injectionTime.value.toLong(), model.depth.value.toInt())
                                }
                            }
                        }
                    }
                    fieldset("Washing") {
                        field("Cycles") {
                            textfield(model.cycles) {
                                required()
                                whenDocked { requestFocus() }
                            }
                        }
                        field {
                            button("Wash") {
                                action {
                                    println("Wash")
                                    getAutosamplerUnsafe()?.washNeedle(model.cycles.value.toInt())
                                }
                            }
                        }
                    }
                    fieldset("Shaking") {
                        field("Mode") {
                            textfield(model.mode) {
                                required()
                                whenDocked { requestFocus() }
                            }
                        }
                        field("Duration, ms") {
                            textfield(model.duration) {
                                required()
                                whenDocked { requestFocus() }
                            }
                        }
                        field {
                            button("Shake") {
                                action {
                                    println("Shake")
                                    getAutosamplerUnsafe()?.shaking(model.mode.value.toInt(), model.duration.value.toInt())
                                }
                            }
                        }
                    }
                }
            }


            tab("Low level") {
                vbox {
                    spacer {  }
                    fieldset("Vial") {
                        field("Vial") {
                            textfield(model.vialL) {
                                required()
                            }
                        }
                        field {
                            hbox {
                                button("Calibrate") {
                                    action {
                                        println("Calibrate vial")
                                        getAutosamplerUnsafe()?.vial?.calibrate()
                                    }
                                }
                                button("Home") {
                                    action {
                                        println("Home")
                                        getAutosamplerUnsafe()?.vial?.home()
                                    }
                                }
                                button("Vial") {
                                    isDefaultButton = true
                                    action {
                                        println("Vial locate")
                                        getAutosamplerUnsafe()?.vial?.locate(model.vialL.value.toInt())
                                    }
                                }
                                button("Washing") {
                                    action {
                                        println("Washing")
                                        getAutosamplerUnsafe()?.vial?.washing()
                                    }
                                }
                                button("Abort") {
                                    action {
                                        println("Abort vial")
                                        getAutosamplerUnsafe()?.vial?.abort()
                                    }
                                }
                            }
                        }
                    }
                    fieldset("Needle") {
                        field("Depth") {
                            textfield(model.needleL) {
                                required()
                            }
                        }
                        field {
                            hbox {
                                button("Calibrate") {
                                    action {
                                        println("Calibrate needle")
                                        getAutosamplerUnsafe()?.needle?.calibrate()
                                    }
                                }
                                button("Move") {
                                    isDefaultButton = true
                                    action {
                                        println("Move needle")
                                        getAutosamplerUnsafe()?.needle?.move(model.needleL.value.toInt())
                                    }
                                }
                                button("Abort") {
                                    action {
                                        println("Abort needle")
                                        getAutosamplerUnsafe()?.needle?.abort()
                                    }
                                }
                            }
                        }
                    }
                    fieldset("Valve") {
                        field("Position") {
                            textfield(model.valveL) {
                                required()
                            }
                        }
                        field {
                            hbox {
                                button("Calibrate") {
                                    action {
                                        println("Calibrate Valve")
                                        getAutosamplerUnsafe()?.valve?.calibrate()
                                    }
                                }
                                button("Move") {
                                    isDefaultButton = true
                                    action {
                                        println("Valve move")
                                        getAutosamplerUnsafe()?.valve?.move(model.valveL.value.toInt())
                                    }
                                }
                                button("Abort") {
                                    action {
                                        println("Abort needle")
                                        getAutosamplerUnsafe()?.valve?.abort()
                                    }
                                }
                            }
                        }
                    }
                    fieldset("Syringe") {
                        field("Position") {
                            textfield(model.syringeL) {
                                required()
                            }
                        }
                        field("Draw speed") {
                            textfield(model.syringeDrawL) {
                                required()
                            }
                        }
                        field("Refill speed") {
                            textfield(model.syringeRefillL) {
                                required()
                            }
                        }
                        field {
                            hbox {
                                button("Calibrate") {
                                    action {
                                        println("Calibrate Syringe")
                                        getAutosamplerUnsafe()?.syringe?.calibrate()
                                    }
                                }
                                button("Move") {
                                    isDefaultButton = true
                                    action {
                                        println("Move syringe")
                                        getAutosamplerUnsafe()?.syringe?.move(model.syringeL.value.toInt(), model.syringeDrawL.value.toInt(), model.syringeRefillL.value.toInt())
                                    }
                                }
                                button("Abort") {
                                    action {
                                        println("Abort needle")
                                        getAutosamplerUnsafe()?.syringe?.abort()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDock() {
        model.validate(decorateErrors = false)
    }
}
