package icu.dxlta.patching

import kotlin.collections.ArrayList

class Variables {

    var variables : ArrayList<String> = ArrayList();

    fun push (variable : String) {
        variables.add("window.$variable")
    }

    fun get () : String {
        return "window.delta={};${variables.joinToString(";")};"
    }

}