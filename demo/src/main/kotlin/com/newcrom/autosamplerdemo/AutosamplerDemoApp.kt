package com.newcrom.autosamplerdemo

import javafx.stage.Stage
import tornadofx.*

class AutosamplerDemoApp : App(AutosamplerDemoView::class, Styles::class) {
    private val view: AutosamplerDemoView by inject()

    override fun start(stage: Stage) {
        super.start(stage)
        view.replaceWith(view, sizeToScene = true, centerOnScreen = true)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch<AutosamplerDemoApp>(args)
        }
    }
}
