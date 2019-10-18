package com.newcrom.autosamplerdemo

import com.newcrom.autosamplerdemo.Styles.Companion.loginScreen
import com.newcrom.autosamplerdemo.autosamplerapi.Autosampler
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import tornadofx.*

class AutosamplerDemoView : View("Newcrom Autosampler") {
    var autosampler: Autosampler? = Autosampler.find()

    private fun getAutosamplerSafe(): Autosampler {
        var a = autosampler
        if (a == null) {
            a = Autosampler.find()
            autosampler = a
        }
        if (a == null) {
            model.serial.value = "No Autosampler"
            throw Exception("No Autosampler")
        }
        model.serial.value = a.serial
        return a
    }

    private val model = object : ViewModel() {
        val serial = bind { SimpleStringProperty() }
        val vial = bind { SimpleIntegerProperty(21) }
        val amount = bind { SimpleIntegerProperty(2) }
        val depth = bind { SimpleIntegerProperty(25) }
        val injectionTime = bind { SimpleIntegerProperty(500) }
        val cycles = bind { SimpleIntegerProperty(2) }
        val mode = bind { SimpleIntegerProperty(1) }
        val duration = bind { SimpleIntegerProperty(5000) }
    }

    override val root = form {
        addClass(loginScreen)
        fieldset {
            field("Serial") {
                textfield(model.serial) {
                    isEditable = false
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
                        model.commit {
                            println("Inject")
                            try {
                                getAutosamplerSafe().injection(model.vial.value.toInt(), model.amount.value.toDouble(), model.injectionTime.value.toLong(), model.depth.value.toInt())
                            } catch (e: Exception) {
                                println("! No Autosampler")
                            }
                        }
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
                    isDefaultButton = true
                    action {
                        model.commit {
                            println("Wash")
                            try {
                                getAutosamplerSafe().washNeedle(model.cycles.value.toInt())
                            } catch (e: Exception) {
                                println("! No Autosampler")
                            }
                        }
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
                    isDefaultButton = true
                    action {
                        model.commit {
                            println("Shake")
                            try {
                                getAutosamplerSafe().shaking(model.mode.value.toInt(), model.duration.value.toInt())
                            } catch (e: Exception) {
                                println("! No Autosampler")
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
