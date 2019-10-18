package com.newcrom.autosamplerdemo

import tornadofx.*

class Styles : Stylesheet() {
    companion object {
        val loginScreen by cssclass()
    }

    init {
        loginScreen {
            padding = box(15.px)
            vgap = 7.px
            hgap = 10.px
        }
    }
}